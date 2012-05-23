/*
 * Copyright (c) Members of the EGEE Collaboration. 2004. 
 * See http://www.eu-egee.org/partners/ for details on the copyright
 * holders.  
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.glite.ce.creamapi.jobmanagement.cmdexecutor;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.creamapi.cmdmanagement.CommandException;
import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.ce.creamapi.jobmanagement.JobStatus;
import org.glite.ce.creamapi.jobmanagement.Lease;
import org.glite.ce.creamapi.jobmanagement.command.JobCommand;

public final class LeaseManager extends TimerTask {
    private static Logger logger = Logger.getLogger(LeaseManager.class);
    private AbstractJobExecutor executor = null;
    private int maxLeaseTime = 0;
    private boolean isLeaseManagerConfigured = true;
    private boolean isLeaseManagerWorking = true;
    private static Object mutex = new Object();

    private final int[] status = new int[] { JobStatus.HELD, JobStatus.IDLE, JobStatus.PENDING, JobStatus.RUNNING, JobStatus.REALLY_RUNNING };

    public LeaseManager(AbstractJobExecutor executor, int maxLeaseTime) {
        super();
        logger.debug("Call LeaseManager constructor");
        this.executor = executor;
        this.maxLeaseTime = maxLeaseTime;
    }

    public boolean isLeaseManagerConfigured() {
        return isLeaseManagerConfigured;
    }

    private void setLeaseManagerConfigured(boolean isLeaseManagerConfigured) {
        this.isLeaseManagerConfigured = isLeaseManagerConfigured;
    }

    public boolean isLeaseManagerWorking() {
        return isLeaseManagerWorking;
    }

    private void setLeaseManagerWorking(boolean isLeaseManagerWorking) {
        this.isLeaseManagerWorking = isLeaseManagerWorking;
    }

    public Calendar getBoundedLeaseTime(Calendar leaseTime) throws CommandException, IllegalArgumentException {
        if (leaseTime == null) {
            throw new IllegalArgumentException("lease time not specified!");
        }

        Calendar now = Calendar.getInstance();

        if (leaseTime.before(now)) {
            throw new CommandException("lease time (" + leaseTime.getTime() + ") expired!");
        }

        Long nowInMillis = now.getTimeInMillis();
        Long leaseInMills = leaseTime.getTimeInMillis();
        Long diffTime = (leaseInMills - nowInMillis) / 1000; // diff in sec

        now.add(Calendar.SECOND, (int) ((diffTime < 0 || diffTime > maxLeaseTime) ? maxLeaseTime : diffTime));

        // int minutes = now.get(Calendar.MINUTE);
        // now.set(Calendar.MINUTE, (minutes < 10) ? 10 : (minutes - (minutes %
        // 10) + 10));
        // now.set(Calendar.SECOND, 0);
        // now.set(Calendar.MILLISECOND, 0);

        return now;
    }

    public void deleteLease(String leaseId, String userId) throws CommandException, IllegalArgumentException {
        logger.debug("Begin deleteLease");
        if (userId == null) {
            throw new IllegalArgumentException("userId not specified!");
        }
        if (leaseId == null) {
            throw new IllegalArgumentException("leaseId not specified!");
        }

        /*
         * if (isLeaseManagerWorking()){logger.error(
         * "Lease Manager is working. Impossible to execute deleteLease.");
         * throw newCommandException(
         * "Lease Manager is working. Impossible to execute deleteLease."); }
         */
        logger.debug("First synchronized (mutex)");
        synchronized (mutex) {
            try {
                executor.getJobDB().deleteJobLease(leaseId, userId);
            } catch (DatabaseException e) {
                logger.error("delete job lease is failed! " + e.getMessage());
                throw new CommandException(e.getMessage());
            }
        }
        logger.debug("After synchronized (mutex)");
        logger.info("Lease Removed: leaseId = " + leaseId + " and userId = " + userId);
        logger.debug("End deleteLease");
    }

    public Calendar setLease(Lease lease) throws CommandException, IllegalArgumentException {
        logger.debug("Begin setLease");
        Calendar boundedLeaseTime = null;
        if (lease == null) {
            logger.error("jobLease not specified!");
            throw new IllegalArgumentException("jobLease not specified!");
        }
        if (lease.getUserId() == null) {
            logger.error("userId not specified!");
            throw new IllegalArgumentException("userId not specified!");
        }
        if (lease.getLeaseId() == null) {
            logger.error("leaseId not specified!");
            throw new IllegalArgumentException("leaseId not specified!");
        }
        if (lease.getLeaseTime() == null) {
            logger.error("leaseTime not specified!");
            throw new IllegalArgumentException("leaseTime not specified!");
        }

        /*
         * if (isLeaseManagerWorking()){
         * logger.error("Lease Manager is working. Impossible to execute setLease."
         * ); throw newCommandException(
         * "Lease Manager is working. Impossible to execute setLease."); }
         */

        boundedLeaseTime = getBoundedLeaseTime(lease.getLeaseTime());
        lease.setLeaseTime(boundedLeaseTime);
        logger.debug("First synchronized (mutex)");
        synchronized (mutex) {
            try {
                executor.getJobDB().insertJobLease(lease);
                logger.info("Lease created: leaseId = " + lease.getLeaseId() + " leaseTime = " + new java.sql.Timestamp(lease.getLeaseTime().getTimeInMillis()).toString()
                        + " userId = " + lease.getUserId());
            } catch (DatabaseException de) {
                logger.debug("insertJobLease is failed. Now attempting to update ...");
                try {
                    executor.getJobDB().updateJobLease(lease);
                    logger.info("Lease updated: leaseId = " + lease.getLeaseId() + " leaseTime = " + new java.sql.Timestamp(lease.getLeaseTime().getTimeInMillis()).toString()
                            + " userId = " + lease.getUserId());
                } catch (DatabaseException de2) {
                    logger.error("Problem to update/insert jobLease. " + de2.getMessage());
                    throw new CommandException("Problem to update/insert jobLease. " + de2.getMessage());
                }
            }
        }
        logger.debug("After synchronized (mutex)");
        logger.debug("End setLease");
        return boundedLeaseTime;
    }

    public void setJobLeaseId(String jobId, String leaseId, String userId) throws IllegalArgumentException, CommandException {
        logger.debug("Begin setJobLeaseId. jobId = " + jobId + " leaseId = " + leaseId + " userId = " + userId);
        if (jobId == null) {
            throw new IllegalArgumentException("jobId not specified!");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId not specified!");
        }

        /*
         * if (isLeaseManagerWorking()){logger.error(
         * "Lease Manager is working. Impossible to execute setJobLeaseId.");
         * throw newCommandException(
         * "Lease Manager is working. Impossible to execute setJobLeaseId."); }
         */
        logger.debug("First synchronized (mutex)");
        synchronized (mutex) {
            try {
                executor.getJobDB().setLeaseId(leaseId, jobId, userId);
            } catch (DatabaseException e) {
                throw new CommandException(e.getMessage());
            }
        }
        logger.debug("After synchronized (mutex)");
        logger.info("LeaseId field has been set for  jobId = " + jobId + " leaseId = " + leaseId + " userId = " + userId);
        logger.debug("End setJobLeaseId. jobId = " + jobId + " leaseId = " + leaseId + " userId = " + userId);
    }

    private int checkCommand(List<JobCommand> command) {
        if (command == null) {
            return 0;
        }
        int count = 1;
        JobCommand jobCmd = null;
        for (int x = 0; x < command.size(); x++) {
            jobCmd = command.get(x);

            if (jobCmd != null && JobCommandConstant.JOB_CANCEL.equals(jobCmd.getName()) && jobCmd.getDescription() != null && jobCmd.getDescription().indexOf("Lease") > 0) {
                logger.debug("jobCmd.getExecutionCompletedTime() == null ? " + (jobCmd.getExecutionCompletedTime() == null));
                if (jobCmd.getExecutionCompletedTime() == null) {
                    return -1;
                }
                count++;
            }
        }
        return count;
    }

    private boolean jobCancelByLeaseManager(Job job, Calendar now) {
        boolean ok = false;
        int count = checkCommand(job.getCommandHistory());
        if ((count != -1) && (count <= 3)) {
            logger.debug("The lease time is expired (" + count + "/3): the job " + job.getId() + " will be cancelled!");
            JobCommand cmd = new JobCommand(JobCommandConstant.JOB_CANCEL, job.getId());
            cmd.setCreationTime(now);
            cmd.setDescription("Job cancelled by Lease Manager! (try " + count + "/3)!");
            cmd.setUserId(job.getUserId());
            cmd.setStartSchedulingTime(now);
            cmd.setStatus(JobCommand.PROCESSING);

            try {
                executor.getJobDB().insertJobCommand(cmd);
                executor.cancel(job);
                cmd.setStatus(JobCommand.SUCCESSFULL);
                executor.getJobDB().updateJobCommand(cmd);
                ok = true;
            } catch (CommandException e) {
                logger.error(e.getMessage());
                cmd.setFailureReason(e.getMessage());
                cmd.setStatus(JobCommand.ERROR);
                try {
                    executor.getJobDB().updateJobCommand(cmd);
                } catch (IllegalArgumentException iae) {
                    logger.error(iae.getMessage());
                } catch (DatabaseException de) {
                    logger.error(de.getMessage());
                }
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
            } catch (DatabaseException e) {
                logger.error(e.getMessage());
            }
        } else {
            ok = true;
        }
        return ok;
    }

    public void run() {
        logger.info("Begin LeaseManager");

        if (executor == null) {
            this.setLeaseManagerConfigured(false);
            logger.error("LeaseManager is not configured: executor not specified!");
            return;
        }

        if (executor.getJobDB() == null) {
            this.setLeaseManagerConfigured(false);
            logger.error("LeaseManager is not configured: jobDb reference not specified!");
            return;
        }

        this.setLeaseManagerConfigured(true);
        this.setLeaseManagerWorking(true);
        Calendar now = Calendar.getInstance();

        logger.debug("First synchronized (mutex)");
        synchronized (mutex) {
            try {
                // retrieving lease obj expired.
                List<Lease> leaseList = executor.getJobDB().retrieveJobLease(now, null);

                for (Lease lease : leaseList) {
                    try {
                        executor.getJobDB().setLeaseExpired(lease);
                        executor.getJobDB().deleteJobLease(lease.getLeaseId(), lease.getUserId());
                        logger.info("Lease Removed: leaseId = " + lease.getLeaseId() + " and userId = " + lease.getUserId());
                        //deleteLease(lease.getLeaseId(), lease.getUserId());
                    } catch (Exception e) {
                        logger.error("setLeaseExpired / delete job lease is failed! " + e.getMessage());
                        // throw new CommandException(e.getMessage());
                    }
                } // for
            } catch (DatabaseException e) {
                logger.error("retrieveJobLease for leaseId expired is failed! " + e.getMessage());
                // throw new CommandException(e.getMessage());
            }
        } // synchronized
        logger.debug("First synchronized (mutex)");

        // cancelling job expired.
        try {
            List<String> jobIdListToCancel = executor.getJobDB().retrieveJobIdLeaseTimeExpired(status, null, null);

            Job job = null;

            for (int jobIndex = 0; jobIndex < jobIdListToCancel.size(); jobIndex++) {
                logger.debug("jobIdListToCancel = " + jobIdListToCancel.get(jobIndex));
                try {
                    job = executor.getJobDB().retrieveJob(jobIdListToCancel.get(jobIndex), null);

                    if (jobCancelByLeaseManager(job, now)) {
                        logger.info("Job has been cancelled. jobId = " + job.getId());
                    } else {
                        logger.warn("Problem to cancel job by LeaseManager. jobId = " + job.getId());
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage());
                    continue;
                }
            }
        } catch (DatabaseException de) {
            logger.error("Retrieving jobs expired by LeaseManager is failed! " + de.getMessage());
        }

        this.setLeaseManagerWorking(false);
        logger.info("End LeaseManager");
    }
}
