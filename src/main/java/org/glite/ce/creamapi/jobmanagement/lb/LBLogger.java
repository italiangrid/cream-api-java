package org.glite.ce.creamapi.jobmanagement.lb;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.ce.creamapi.jobmanagement.cmdexecutor.JobCommandConstant;
import org.glite.ce.creamapi.cmdmanagement.Command;
import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.ce.creamapi.jobmanagement.JobStatus;
import org.glite.ce.creamapi.jobmanagement.db.JobDBInterface;

import org.glite.jobid.Jobid;
//import org.glite.lb.ContextDirect;
import org.glite.lb.ContextIL;
import org.glite.lb.Sources;
import org.glite.lb.LBException;
import org.glite.lb.EventRegJob;
import org.glite.lb.EventCREAMStore;
import org.glite.lb.EventCREAMCall;
import org.glite.lb.EventCREAMStatus;
import org.glite.lb.EventCREAMAccepted;
import org.glite.lb.SeqCode;
import org.glite.lb.Timeval;

public class LBLogger {
    private final static Logger logger = Logger.getLogger(LBLogger.class.getName());
    private static LBLogger lbLogger = null;
    private static URI defaultLBURI = null;
    private static String ILPrefix = null;
    private static JobDBInterface jobDB = null;

    public static final int START = 0;
    public static final int OK = 1;
    public static final int FAILED = 2;

    public static LBLogger getInstance() throws LBException {
        if (lbLogger == null) {
            lbLogger = new LBLogger();
        }

        return lbLogger;
    }

    public static void setJobDB(JobDBInterface jdb) {
        jobDB = jdb;
    }

    public static void setDefaultLBURI(URI lbURI) {
        defaultLBURI = lbURI;
    }

    public static boolean isEnabled() {
        return (defaultLBURI != null && ILPrefix != null); 
    }

    public static void setILPrefix(String prefix) {
        ILPrefix = prefix;
    }

    private LBLogger() throws LBException {
        if (defaultLBURI == null) {
            throw new LBException("defaultLBURI must be defined!");
        }

        if (ILPrefix == null) {
            throw new LBException("ILPrefix must be defined!");
        }
    }

    public void register(Job job) throws LBException {
        if (job == null) {
            return;
        }

        String gridJobId = job.getGridJobId();
        if (gridJobId == null || Job.NOT_AVAILABLE_VALUE.equals(gridJobId)) {
            String userLBAddress = (String) job.getVolatileProperty(Job.LB_ADDRESS);
            URI lb = defaultLBURI;

            if (userLBAddress != null) {
                try {
                    if (!userLBAddress.startsWith("https://")) {
                        userLBAddress = "https://" + userLBAddress;
                    }
                    lb = new URI(userLBAddress);
                } catch (URISyntaxException e) {
                    logger.warn("malformed LBAddress (" + userLBAddress + "), using default");
                }
            }

            Jobid lbjob = new Jobid(lb.getHost(), lb.getPort());
            logger.info(job.getId() + " assigned gridJobId " + lbjob.toString());

            /*
             * TODO: remove this code, and assign the grid jobid to this job
             * really
             */
            lbjob = new Jobid(lb.getHost(), lb.getPort(), job.getId());
            logger.info("... but using instead " + lbjob.toString());

            //LBCredentials cred = new LBCredentials(job.getDelegationProxyCertPath(), "/etc/grid-security/certificates");
            //ContextDirect ctxd = new ContextDirect(lb.getHost(), lb.getPort());
            //ctxd.setCredentials(cred);
            //ctxd.setJobid(lbjob);

            if (job.getSequenceCode() != null) {
              //  ctxd.setSeqCode(new SeqCode(SeqCode.CREAM, "no_seqcodes_with_cream_register"));
           // } else {
                SeqCode sc = new SeqCode(SeqCode.CREAMWMS, job.getSequenceCode());
                sc.incrementSeqCode(new Sources(Sources.CREAM_EXECUTOR));

                job.setSequenceCode(sc.toString());

                //ctxd.setSeqCode(sc);

                if (jobDB != null) {
                    try {
                       jobDB.update(job);
                    } catch (DatabaseException ex) {
                        throw new LBException(ex.getMessage());
                    }
                }
            }

           // ctxd.setSource(new Sources(Sources.CREAM_EXECUTOR));

            EventRegJob reg = new EventRegJob();
            reg.setNs(job.getCreamURL());
            reg.setJobtype(EventRegJob.Jobtype.CREAM);

            //ctxd.log(reg);

            ContextIL ctx = new ContextIL(ILPrefix);
            ctx.setJobid(lbjob);

            if (job.getSequenceCode() == null) {
                ctx.setSeqCode(new SeqCode(SeqCode.CREAM, "no_seqcodes_with_cream_register_cheat"));
            } else {
                ctx.setSeqCode(new SeqCode(SeqCode.CREAM, job.getSequenceCode()));
            }

            ctx.setSource(new Sources(Sources.CREAM_EXECUTOR));
            ctx.setUser(job.getExtraAttribute("USER_DN_X500"));

            //ctx.setUser(ctxd.getUser());
            reg.setJdl(job.getJDL());

            ctx.log(reg);
        } else {
            throw new LBException("LBLogger.register(): grid jobid set, not registering in LB");
        }
    }

    public void insertCommand(Job job, Command cmd, int phase) throws LBException {
        insertCommand(job, cmd, phase, null);
    }

    public void insertCommand(Job job, Command cmd, int phase, Throwable reason) throws LBException {
        if (job == null) {
            return;
        }

        ContextIL ctx = new ContextIL(ILPrefix);

        if (job.getSequenceCode() == null) {
            ctx.setSeqCode(new SeqCode(SeqCode.CREAM, "no_seqcodes_with_cream_insertcmd_" + cmd.getId() + "_" + phase));
        } else {
            SeqCode sc = new SeqCode(SeqCode.CREAMWMS, job.getSequenceCode());
            sc.incrementSeqCode(new Sources(Sources.CREAM_EXECUTOR));

            job.setSequenceCode(sc.toString());

            ctx.setSeqCode(sc);

            if (jobDB != null && !JobCommandConstant.JOB_PURGE.equals(cmd.getName())) {
                try {
                   jobDB.update(job);
                } catch (DatabaseException ex) {
                    throw new LBException(ex.getMessage());
                }
            }
        }

        ctx.setSource(new Sources(Sources.CREAM_INTERFACE));
        ctx.setUser(cmd.getUserId());

        Jobid jobid;
        if (job.getGridJobId() == null || Job.NOT_AVAILABLE_VALUE.equals(job.getGridJobId())) {
            jobid = new Jobid(defaultLBURI.getHost(), defaultLBURI.getPort(), job.getId());
        } else {
            jobid = new Jobid(job.getGridJobId());
        }

        ctx.setJobid(jobid);

        // logger.debug("insertCommand: id = "+ cmd.getId() + " name = " +
        // cmd.getName()+ " category = " + cmd.getCategory() +
        // " failureReason = " + cmd.getFailureReason() + " description = " +
        // cmd.getDescription());

        EventCREAMStore event = new EventCREAMStore();

         switch (phase) {
             case START:
                event.setResult(EventCREAMStore.Result.START);
                    break;
                case OK:
                    event.setResult(EventCREAMStore.Result.OK);
                    break;
                case FAILED:
                    event.setResult(EventCREAMStore.Result.FAILED);
                    event.setReason(reason.getMessage());
                    break;
            }

            event.setCmdid(""+cmd.getId());

        if (JobCommandConstant.JOB_START.equals(cmd.getName())) {
            event.setCommand(EventCREAMStore.Command.CMDSTART);
        } else if(JobCommandConstant.JOB_CANCEL.equals(cmd.getName())) {
            event.setCommand(EventCREAMStore.Command.CMDCANCEL);
        } else if(JobCommandConstant.JOB_PURGE.equals(cmd.getName())) {
            event.setCommand(EventCREAMStore.Command.CMDPURGE);
        } else if(JobCommandConstant.JOB_SUSPEND.equals(cmd.getName())) {
            event.setCommand(EventCREAMStore.Command.CMDSUSPEND);
        } else if(JobCommandConstant.JOB_RESUME.equals(cmd.getName())) {
            event.setCommand(EventCREAMStore.Command.CMDRESUME);
        } else {
            throw new LBException("LBLogger.insertCommand(): not handled " + cmd.getName());
        }

        ctx.log(event);
    }

    public void execute(Job job, Command cmd, int phase, String destid, Throwable reason) throws LBException {
        if (job == null) {
            return;
        }

        ContextIL ctx = new ContextIL(ILPrefix);

        if (job.getSequenceCode() == null) {
            ctx.setSeqCode(new SeqCode(SeqCode.CREAM, "no_seqcodes_with_cream_execute_" + cmd.getId() + "_" + phase));
        } else {
            SeqCode sc = new SeqCode(SeqCode.CREAMWMS, job.getSequenceCode());
            sc.incrementSeqCode(new Sources(Sources.CREAM_EXECUTOR));
            job.setSequenceCode(sc.toString());
            ctx.setSeqCode(sc);

            if (jobDB != null && !JobCommandConstant.JOB_PURGE.equals(cmd.getName())) {
                try {
                   jobDB.update(job);
                } catch (DatabaseException ex) {
                    throw new LBException(ex.getMessage());
                }
            }
        }

        ctx.setSource(new Sources(Sources.CREAM_EXECUTOR));
        ctx.setUser(cmd.getUserId());

        Jobid jobid;
        if (job.getGridJobId() == null || Job.NOT_AVAILABLE_VALUE.equals(job.getGridJobId())) {
            jobid = new Jobid(defaultLBURI.getHost(), defaultLBURI.getPort(), job.getId());
        } else {
            jobid = new Jobid(job.getGridJobId());
        }

        ctx.setJobid(jobid);

        EventCREAMCall event = new EventCREAMCall();
        event.setCallee(new Sources(Sources.LRMS));
        event.setCmdid(""+cmd.getId());
        
        // logger.debug("execute: id = " + cmd.getId() + " name = " +
        // cmd.getName()+ " category = " + cmd.getCategory() +
        // " failureReason = " + cmd.getFailureReason() + " description = " +
        // cmd.getDescription());

        switch (phase) {
            case START:
                event.setResult(EventCREAMCall.Result.START);
                break;
            case OK:
                event.setResult(EventCREAMCall.Result.OK);
                event.setDestid(destid);
                break;
            case FAILED:
                event.setResult(EventCREAMCall.Result.FAILED);
                event.setReason(reason.getMessage());
                break;
        }

        if (JobCommandConstant.JOB_START.equals(cmd.getName())) {
            event.setCommand(EventCREAMCall.Command.CMDSTART);
        } else if (JobCommandConstant.JOB_CANCEL.equals(cmd.getName())) {
            event.setCommand(EventCREAMCall.Command.CMDCANCEL);
        } else if (JobCommandConstant.JOB_PURGE.equals(cmd.getName())) {
            event.setCommand(EventCREAMCall.Command.CMDPURGE);
        } else if (JobCommandConstant.JOB_SUSPEND.equals(cmd.getName())) {
            event.setCommand(EventCREAMCall.Command.CMDSUSPEND);
        } else if (JobCommandConstant.JOB_RESUME.equals(cmd.getName())) {
            event.setCommand(EventCREAMCall.Command.CMDRESUME);
        } else {
            throw new LBException("LBLogger.execute(): not handled " + cmd.getName());
        }
        
        ctx.log(event);
    }

    public void statusChanged(Job job, JobStatus status, JobStatus lastStatus, int phase) throws LBException {
        if (job == null) {
            return;
        }

        ContextIL ctx = new ContextIL(ILPrefix);
        
        if (job.getSequenceCode() == null) {
            ctx.setSeqCode(new SeqCode(SeqCode.CREAM, "no_seqcodes_with_cream_statuschange_" + status.getName() + "_" + phase));
        } else {
            SeqCode sc = new SeqCode(SeqCode.CREAMWMS, job.getSequenceCode());
            sc.incrementSeqCode(new Sources(Sources.CREAM_EXECUTOR));
            job.setSequenceCode(sc.toString());
            ctx.setSeqCode(sc);

            if (jobDB != null) {
                try {
                   jobDB.update(job);
                } catch (DatabaseException ex) {
                    throw new LBException(ex.getMessage());
                }
            }
        }
        
        ctx.setSource(new Sources(Sources.CREAM_EXECUTOR));
        ctx.setUser(job.getUserId());

        Jobid jobid;
        if (job.getGridJobId() == null || Job.NOT_AVAILABLE_VALUE.equals(job.getGridJobId())) {
            jobid = new Jobid(defaultLBURI.getHost(), defaultLBURI.getPort(), job.getId());
        } else {
            jobid = new Jobid(job.getGridJobId());
        }
        
        ctx.setJobid(jobid);

        EventCREAMStatus event = new EventCREAMStatus();

        event.setNewState(status.getName());
        event.setNewState(status.getName());
        event.setDescr(status.getDescription());
        
        if (lastStatus != null) {
            event.setOldState(lastStatus.getName());
        }
        
        event.setExitCode(status.getExitCode()); /*
                                                  * reasonable only in Done
                                                  * state
                                                  */
        event.setWorkerNode(job.getWorkerNode());
        event.setLRMSJobid(job.getLRMSJobId());
        event.setFailureReason(status.getFailureReason());
        event.setOrigTimestamp(new Timeval(0, 0)); /* TODO */

        switch (phase) {
            case START:
                event.setResult(EventCREAMStatus.Result.ARRIVED);
                break;
            case OK:
                event.setResult(EventCREAMStatus.Result.DONE);
                break;
            default:
                break; /* XXX */
        }

        ctx.log(event);
    }

    public void accept(Job job) throws LBException {
        if (job ==null) {
            return;
        }

        ContextIL ctx = new ContextIL(ILPrefix);
        if (job.getSequenceCode() == null) {
            ctx.setSeqCode(new SeqCode(SeqCode.CREAM, "no_seqcodes_with_cream_accept"));
        } else {
            SeqCode sc = new SeqCode(SeqCode.CREAMWMS, job.getSequenceCode());
            sc.incrementSeqCode(new Sources(Sources.CREAM_EXECUTOR));
            
            job.setSequenceCode(sc.toString());
            
            ctx.setSeqCode(sc);

            if (jobDB != null) {
                try {
                   jobDB.update(job);
                } catch (DatabaseException ex) {
                    throw new LBException(ex.getMessage());
                }
            }
        }
        
        ctx.setSource(new Sources(Sources.CREAM_EXECUTOR));
        ctx.setUser(job.getUserId());

        Jobid jobid;
        if (job.getGridJobId() == null || Job.NOT_AVAILABLE_VALUE.equals(job.getGridJobId())) {
            jobid = new Jobid(defaultLBURI.getHost(), defaultLBURI.getPort(), job.getId());
        } else {
            jobid = new Jobid(job.getGridJobId());
        }
        
        ctx.setJobid(jobid);

        EventCREAMAccepted event = new EventCREAMAccepted();
        event.setLocalJobid(job.getId());

        ctx.log(event);
    }
}
