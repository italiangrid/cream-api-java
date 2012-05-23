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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.glite.ce.creamapi.cmdmanagement.Command;
import org.glite.ce.creamapi.jobmanagement.JobStatus;

public class JobPurger extends TimerTask {
    private static Logger logger = Logger.getLogger(JobPurger.class.getName());
    private AbstractJobExecutor executor = null;
    private String policy = null;
    private JOB_STATUS jobStatus;
    private Calendar date;


    public static enum JOB_STATUS {
        ABORTED("ABORTED", JobStatus.ABORTED),
        CANCELLED("CANCELLED", JobStatus.CANCELLED),
        REGISTERED("REGISTERED", JobStatus.REGISTERED),
        DONE_FAILED("DONE-FAILED", JobStatus.DONE_FAILED),
        DONE_OK("DONE-OK", JobStatus.DONE_OK);

        String name;
        int id;

        JOB_STATUS(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
        
        public String toString() {
            return name;
        }
    }

    public JobPurger(AbstractJobExecutor executor, String policy) {
        super();
        this.executor = executor;
        this.policy = policy;
    }

    public String getPolicy() {
        return policy;
    }
    
    public void setPolicy(String policy) {
        this.policy = policy;
    }
  
    private Calendar parseDate(String date) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("date not specified!");
        }

        date.trim();

        GregorianCalendar todate = new GregorianCalendar();

        if (date.endsWith("minutes")) {
            todate.add(Calendar.MINUTE, -Integer.parseInt(date.substring(0, date.indexOf("minutes")).trim()));
        } else if (date.endsWith("hours")) {
            todate.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(date.substring(0, date.indexOf("hours")).trim()));
        } else if (date.endsWith("days")) {
            todate.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(date.substring(0, date.indexOf("days")).trim()));
        } else if (date.endsWith("months")) {
            todate.add(Calendar.MONTH, -Integer.parseInt(date.substring(0, date.indexOf("months")).trim()));
        } else if (date.endsWith("years")) {
            todate.add(Calendar.YEAR, -Integer.parseInt(date.substring(0, date.indexOf("years")).trim()));
        } else {
            throw new IllegalArgumentException("illegal date format: " + date + "! use: time_value { minutes | hours | days | months | years }");
        }

        return todate;
    }

    private void parsePolicy(String policy) throws IllegalArgumentException, Exception {
        if (policy == null) {
            throw new IllegalArgumentException("policy not specified!");
        }

        int index = policy.indexOf(" ");
        if (index < 0) {
            throw new Exception("malformed policy " + policy);
        }

        String status = policy.substring(0, index).trim();

        if (status == null || status.length() == 0) {
            throw new Exception("job status not specified!");
        }

        JOB_STATUS[] jobStatusList = JOB_STATUS.values();
        for (int i = 0; i < jobStatusList.length; i++) {
            if (jobStatusList[i].getName().equalsIgnoreCase(status)) {
                jobStatus = jobStatusList[i];
                break;
            }
        }

        if (jobStatus == null) {
            throw new Exception("invalid job status name: " + status + "! use { ABORTED | CANCELLED | DONE-FAILED | DONE-OK | REGISTERED }");
        }

        if (index >= policy.length()) {
            throw new Exception("date not specified!");
        }

        String dateValue = policy.substring(index, policy.length()).trim();

        if (dateValue == null || dateValue.length() == 0) {
            throw new Exception("date not specified!");
        }

        date = parseDate(dateValue);
        return;
    }

    public void run() {
        if(policy == null) {
            return;
        }
        
        StringTokenizer st = new StringTokenizer(policy, ";");
        String token = null;
        while(st.hasMoreTokens()) {
            token = st.nextToken().trim();
            if(token == null) {
                continue;
            }
           
            try {
                parsePolicy(token);
            } catch(Throwable e) {
                logger.error("policy parsing error: " + e.getMessage());
                continue;
            }
 
            try {
                int[] purgeCompatibleStatus = new int[] { jobStatus.getId() };

                List<String> jobIdList = executor.getJobDB().retrieveJobId(null, null, purgeCompatibleStatus, null, date);

                logger.info("purging " + jobIdList.size() + " jobs with status " + jobStatus.getName() + " <= " + date.getTime());
                
                for(String jobId : jobIdList) {
                    Command purgeCmd = new Command(JobCommandConstant.JOB_PURGE, JobCommandConstant.JOB_MANAGEMENT);
                    purgeCmd.setUserId("ADMIN");
                    purgeCmd.setDescription("Cancelled by CREAM's job purger");
                    purgeCmd.setAsynchronous(true);
                    purgeCmd.setCommandGroupId(jobId);
                    purgeCmd.addParameter("JOB_ID", jobId);
                    purgeCmd.addParameter("IS_ADMIN", Boolean.toString(true));
                    purgeCmd.setPriorityLevel(Command.LOW_PRIORITY);

                    executor.getCommandManager().execute(purgeCmd);
                }
            } catch (Throwable t) {
                logger.error(t.getMessage());
            }    
        }
    }
    
//    public static void main(String[] args) {
//        JobPurger jc = new JobPurger(null, "DONE-OK=10 days; ABORTED = 2 years;DONE-FAILED=1months");
//        jc.run();
//    }
}
