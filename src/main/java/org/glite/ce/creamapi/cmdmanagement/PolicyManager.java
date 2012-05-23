package org.glite.ce.creamapi.cmdmanagement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.utils.Timer;
import org.glite.ce.commonj.utils.TimerTask;
import org.glite.ce.creamapi.cmdmanagement.Policy.TIME_UNIT;

public class PolicyManager {
    private static final Logger logger = Logger.getLogger(PolicyManager.class.getName());
    private static Timer timer = null;
    private static PolicyManager policyManager = null;
    private Hashtable<String, PolicyTask> policyTaskTable;
    private Hashtable<String, Policy> policyTable;
   
    private PolicyManager() {
        policyTaskTable = new Hashtable<String, PolicyTask>(0);
        policyTable = new Hashtable<String, Policy>(0);
    }
    
    public static PolicyManager getInstance() {
        if(policyManager == null) {
            policyManager = new PolicyManager();
        }
        
        return policyManager;
    }

    public void addPolicy(Policy policy) throws IllegalArgumentException, PolicyManagerException {
        if (policy == null) {
            throw new IllegalArgumentException("policy not specified!");
        }

        if (policy.getName() == null) {
            throw new PolicyManagerException("policy name not specified!");
        }

        if (policyTable.containsKey(policy.getName())) {
            throw new PolicyManagerException("Policy " + policy.getName() + " already exists!");
        }

        policyTable.put(policy.getName(), policy);
        
        if (timer != null && policyTaskTable.containsKey(policy.getType())) {
            PolicyTask task = policyTaskTable.get(policy.getType());

            if (policy.getTimeUnit() == TIME_UNIT.DATE) {
                timer.schedule(new PolicyTimerTask(policy, task), policy.getDate().getTime());
            } else {
                timer.schedule(new PolicyTimerTask(policy, task), 0, getPeriod(policy.getTimeUnit(), policy.getTimeValue()), TimerTask.EXECUTION_TYPE.FIXED_DELAY_POST_EXECUTION);
            }
        }

        logger.info("new policy " + policy.getName() + " added!");
    }

    public void addPolicyTask(PolicyTask task) throws IllegalArgumentException, PolicyManagerException {
        if (task == null) {
            throw new IllegalArgumentException("task not specified!");
        }

        if (task.getName() == null) {
            throw new PolicyManagerException("task name not specified!");
        }
        
        if (task.getCommandExecutor() == null) {
            throw new PolicyManagerException("CommandExecutor not specified!");
        }
        
        if (policyTaskTable.containsKey(task.getName())) {
            throw new PolicyManagerException("PolicyTask " + task.getName() + " already exists!");
        }

        policyTaskTable.put(task.getName(), task);

        if (timer != null) {
            List<Policy> list = getPolicyByType(task.getName());

            for (Policy policy : list) {
                if (policy.getTimeUnit() == TIME_UNIT.DATE) {
                    timer.schedule(new PolicyTimerTask(policy, task), policy.getDate().getTime());
                } else {
                    timer.schedule(new PolicyTimerTask(policy, task), 0, getPeriod(policy.getTimeUnit(), policy.getTimeValue()),
                            TimerTask.EXECUTION_TYPE.FIXED_DELAY_POST_EXECUTION);
                }
            }
        }

        logger.info("new PolicyTask " + task.getName() + " added!");
    }

    public Long getPeriod(TIME_UNIT timeUnit, int timeValue) {
        Long period = 0L;

        switch (timeUnit) {
        case SECOND:
            period = 1L;
            break;
        case MINUTE:
            period = 60L;
            break;
        case HOUR:
            period = 3600L;
            break;
        case DAY:
            period = 86400L;
            break;
        case MONTH:
            period = 2592000L;
            break;
        case YEAR:
            period = 31104000L;
            break;
        }

        return period * timeValue * 1000;
    }

    public List<Policy> getPolicyByType(String type) {
        List<Policy> result = new ArrayList<Policy>(0);
        if (type == null) {
            return result;
        }

        for (Policy policy : policyTable.values()) {
            if (policy.getType() != null && policy.getType().equalsIgnoreCase(type)) {
                result.add(policy);
            }
        }

        return result;
    }

    public List<Policy> getPolicyList() {
        return new ArrayList(policyTable.values());
    }

    public PolicyTask getPolicyTask(String name) {
        if (name != null && policyTaskTable.containsKey(name)) {
            return policyTaskTable.get(name);
        }

        return null;
    }

    public List<PolicyTask> getPolicyTaskList() {
        return new ArrayList(policyTaskTable.values());
    }

    public void removePolicyTask(String name) {
        if (name != null && policyTaskTable.containsKey(name)) {
            for (int i = 0; i < timer.size(); i++) {
                TimerTask timerTask = timer.getTimerTask(i);

                if (timerTask != null && timerTask.getName().equalsIgnoreCase(name)) {
                    timerTask.cancel();
                }
            }

            timer.purge();
            PolicyTask task = policyTaskTable.remove(name);

            logger.info("removed " + task.getName() + " PolicyTask");
        }
    }

    public void setPolicyList(List<Policy> list) throws IllegalArgumentException, PolicyManagerException {
        if (list != null) {
            policyTable.clear();
            for (Policy policy : list) {
                addPolicy(policy);
            }
        }
    }

    public void setPolicyTask(List<PolicyTask> policTaskList) throws IllegalArgumentException, PolicyManagerException {
        if (policyTaskTable != null) {
            policyTaskTable.clear();
            for (PolicyTask task : policTaskList) {
                addPolicyTask(task);
            }
        }
    }

    public void start() {
        timer = new Timer("POLICY_TIMER", true);
        
        for (Policy policy : policyTable.values()) {
            PolicyTask task = getPolicyTask(policy.getType());
            
            if (task != null) {
                if (policy.getTimeUnit() == TIME_UNIT.DATE) {
                    timer.schedule(new PolicyTimerTask(policy, task), policy.getDate().getTime());
                } else {
                    timer.schedule(new PolicyTimerTask(policy, task), 0, getPeriod(policy.getTimeUnit(), policy.getTimeValue()),
                            TimerTask.EXECUTION_TYPE.FIXED_DELAY_POST_EXECUTION);
                }
            }
        }
    }

    public void stop() {
        timer.cancel();
        timer.purge();
    }

    public void destroy() {
        logger.info("destroy invoked!");
        
        timer.cancel();
        timer.purge();
        timer = null;
        
        policyTaskTable.clear();
        policyTaskTable = null;
        
        policyTable.clear();
        policyTable = null;
        
        logger.info("destroyed!");
    }
    
    private class PolicyTimerTask extends TimerTask {
        private Policy policy;
        private PolicyTask task;

        public PolicyTimerTask(Policy policy, PolicyTask task) {
            this.policy = policy;
            this.task = task;
        }

        public void run() {
            if (task != null) {
                logger.info(task.getName() + ": evaluating policy \"" + policy.getName() + "\"...");                
                try {
                    task.execute(policy);
                    logger.info(task.getName() + ": policy \"" + policy.getName() + "\" evaluated!");
                } catch (PolicyException e) {
                    logger.info(task.getName() + ": policy \"" + policy.getName() + "\" failed with message: " + e.getMessage());
                }

                if(policy.getTimeUnit() == TIME_UNIT.DATE) {
                    policyTable.remove(policy.getName());
                }
            }
        }
    }    
}
