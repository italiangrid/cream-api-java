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
 
/*
 *
 * Authors: Eric Frizziero <eric.frizziero@pd.infn.it>
 *
 */

package org.glite.ce.creamapi.jobmanagement.cmdexecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.glite.ce.creamapi.jobmanagement.db.DBInfoManager;
import org.glite.ce.creamapi.jobmanagement.db.JobDBInterface;

public final class JobSubmissionManager extends TimerTask {
    private static Logger logger = Logger.getLogger(JobSubmissionManager.class.getName());

    public static final String SHOW_PARAMETER = "--show";
    public static final String TEST_PARAMETER = "--test";
    
    public static final int SUBMISSION_ENABLED = 0;
    public static final int SUBMISSION_DISABLED_BY_LIMITER = 1;
    public static final int SUBMISSION_DISABLED_BY_ADMIN = 2;
    
    private static JobSubmissionManager jobSubmissionManager = null;
    private static String gliteCreamLoadMonitorScriptPath = null;
    private static String gliteCreamLoadMonitorScriptConfigurationFile = "";
    private boolean enableScript  = true;
    private boolean isLoadMonitorScriptConfigurated = false;
    private JobSubmissionManagerInfo jobSubmissionManagerInfo = new JobSubmissionManagerInfo();

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();
    
    
    public static JobSubmissionManager getInstance() throws IllegalArgumentException {
        if(jobSubmissionManager == null) {
            jobSubmissionManager = new JobSubmissionManager();
        }
        return jobSubmissionManager;
    }

    private JobSubmissionManager() throws IllegalArgumentException {
        super();
        if ((gliteCreamLoadMonitorScriptPath != null) && (!"".equals(gliteCreamLoadMonitorScriptPath))){
            //to verify if the script is executable. it throws an IllegalArgument exception if an error occurred.
            jobSubmissionManagerInfo.setExecutionTimestamp(Calendar.getInstance());
            jobSubmissionManagerInfo.setShowMessage(executeScript(SHOW_PARAMETER));
            logger.info("Cream LoadMonitor Script enabled.");
            isLoadMonitorScriptConfigurated = true;
        } else {
            logger.info("Cream LoadMonitor Script disabled.");
            isLoadMonitorScriptConfigurated = false;
        }
    }

    public static void setGliteCreamLoadMonitorScriptPath(String gliteCreamLMScriptPath) {
        if (gliteCreamLMScriptPath != null) {
            gliteCreamLoadMonitorScriptPath = gliteCreamLMScriptPath.trim();
            int index = gliteCreamLMScriptPath.indexOf(" ");
            if (index != -1) {
                gliteCreamLoadMonitorScriptConfigurationFile = gliteCreamLMScriptPath.substring(index+1);
                gliteCreamLoadMonitorScriptPath = gliteCreamLMScriptPath.substring(0, index);
            }
        }
    }
    
    public JobSubmissionManagerInfo getJobSubmissionManagerInfo() {
        logger.debug("Begin getJobSubmissionManagerInfo");
        JobSubmissionManagerInfo clone = null;
        readLock.lock();
        clone = (JobSubmissionManagerInfo)jobSubmissionManagerInfo.clone();
        readLock.unlock();
        logger.debug("End getJobSubmissionManagerInfo");
        return clone;
    }

    // ( (!enable) && (enableScript || acceptNewJobs))
	public void enableAcceptNewJobs(int enable) {
	    logger.debug("Begin enableAcceptNewJobs with enable = " + enable);
	    writeLock.lock();
        enableScript = (JobSubmissionManager.SUBMISSION_DISABLED_BY_ADMIN != enable);
        DBInfoManager.updateSubmissionEnabled(JobDBInterface.JOB_DATASOURCE_NAME, enable);
        jobSubmissionManagerInfo.setAcceptNewJobs(JobSubmissionManager.SUBMISSION_ENABLED == enable);
        logger.info("AcceptNewJobs = " + jobSubmissionManagerInfo.isAcceptNewJobs());        
        if (!enableScript){
            jobSubmissionManagerInfo.setTestErrorMessage(null);
            jobSubmissionManagerInfo.setShowMessage(null);
        }
        writeLock.unlock();
        logger.debug("End enableAcceptNewJobs with enable = " + enable);
	 }
	
	public void run() {
	    if (enableScript && isLoadMonitorScriptConfigurated) {
	        Calendar executionTimestamp = Calendar.getInstance();
	        String testErrorMessage = null;
	        boolean acceptNewJobs = false;
	        String showMessage = null;
	        String result = null;
	        try{
	            result = executeScript(TEST_PARAMETER);
	            if (result != null){
	                acceptNewJobs = false;
	                testErrorMessage = result;
	            } else {
	                acceptNewJobs = true; 
	                testErrorMessage = null;
	            } 
	        }catch(IllegalArgumentException iae){
	            logger.error("Error in execution of gliteCreamLoadMonitorScript: " + iae.getMessage()); 
	            testErrorMessage = "Error in execution of gliteCreamLoadMonitorScript: " + iae.getMessage();
	        }
	        try{
	            result = executeScript(SHOW_PARAMETER);
	            showMessage = result;
	        }catch(IllegalArgumentException iae){
	            logger.error("Error in execution of gliteCreamLoadMonitorScript: " + iae.getMessage()); 
	            showMessage = "Error in execution of gliteCreamLoadMonitorScript: " + iae.getMessage();
	        }
	        writeLock.lock();
	        if (enableScript) {
	            jobSubmissionManagerInfo.setExecutionTimestamp(executionTimestamp);
	            jobSubmissionManagerInfo.setAcceptNewJobs(acceptNewJobs);
	            if (acceptNewJobs) {
	                DBInfoManager.updateSubmissionEnabled(JobDBInterface.JOB_DATASOURCE_NAME, JobSubmissionManager.SUBMISSION_ENABLED);    
	            } else {
	                DBInfoManager.updateSubmissionEnabled(JobDBInterface.JOB_DATASOURCE_NAME, JobSubmissionManager.SUBMISSION_DISABLED_BY_LIMITER);
	            }
	            logger.info("AcceptNewJobs by script = " + acceptNewJobs);
	            jobSubmissionManagerInfo.setShowMessage(showMessage);
	            jobSubmissionManagerInfo.setTestErrorMessage(testErrorMessage);
	        }
	        writeLock.unlock();
	    }
    }

    /**
     * parameter == TEST_PARAMETER: if it's all ok the method returns null, else the standardError of the executed script.
     * parameter == SHOW_PARAMETER: if it's all ok the method returns the standardOutput of the executed script, else the standardError of the executed script.
     * @param parameter (TEST_PARAMETER or SHOW_PARAMETER).
     * @return
     */
    private static String executeScript(String parameter) throws IllegalArgumentException{   
    	String message = null;
    	Process proc = null;

    	try {
    		String[] cmd = new String[] { gliteCreamLoadMonitorScriptPath, gliteCreamLoadMonitorScriptConfigurationFile, parameter};

    		proc = Runtime.getRuntime().exec(cmd);
    	} catch (Throwable e) {
    		logger.error(e.getMessage());
    		throw new IllegalArgumentException(e.getMessage());
    	} finally {
    		if (proc != null) {
    			try {
    				proc.waitFor();
    			} catch (InterruptedException e) {
    			}

    			StringBuffer errorMessage = null;
    			StringBuffer outputMessage = null;

    			if(proc.exitValue() != 0) {
    				BufferedReader readErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
    				errorMessage = new StringBuffer();
    				String inputLine = null;

    				try {
    					while ((inputLine = readErr.readLine()) != null) {
    						errorMessage.append(inputLine + "\n");
    					}
    				} catch (IOException ioe) {
    					logger.error(ioe.getMessage());
    				} finally {
    					try {
    						readErr.close();
    					} catch (IOException ioe) {}
    				}

    				if (errorMessage.length() > 0) {
    					errorMessage.append("\n");
    				}
    				message = errorMessage.toString();
    				logger.info("gliteCreamLoadMonitor: exitCode = " + proc.exitValue() + " messageError = " + message);
    			} else if (SHOW_PARAMETER.equals(parameter)){
    				BufferedReader readOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));

    				outputMessage = new StringBuffer();
    				String outputLine = null;

    				try {
    					while ((outputLine = readOut.readLine()) != null) {
    						outputMessage.append(outputLine + "\n");
    					}
    				} catch (IOException ioe) {
    					logger.error(ioe.getMessage());
    				} finally {
    					try {
    						readOut.close();
    					} catch (IOException ioe) {}
    				}

    				if (outputMessage.length() > 0) {
    					outputMessage.append("\n");
    				}
    				message = outputMessage.toString();
    				logger.debug("gliteCreamLoadMonitor show: " + message);
    			}
    			try {
    				proc.getInputStream().close();
    			} catch (IOException ioe) {}
    			try {
    				proc.getErrorStream().close();
    			} catch (IOException ioe) {}
    			try {
    				proc.getOutputStream().close();
    			} catch (IOException ioe) {}
    		}
    	}
    	return message;
    }
   
}
