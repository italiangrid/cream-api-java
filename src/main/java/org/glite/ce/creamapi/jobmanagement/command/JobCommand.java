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
 
package org.glite.ce.creamapi.jobmanagement.command;

import java.util.Calendar;
import java.util.List;

import org.glite.ce.creamapi.jobmanagement.cmdexecutor.JobCommandConstant;

public class JobCommand {

    public static String getCommandName(int type) {
        if (type < 0 || type >= JobCommandConstant.commandNameList.size()) {
            return null;
        }
        return JobCommandConstant.commandNameList.get(type);
    }

    public static final int CREATED = 0;
    public static final int QUEUED = 1;
    public static final int SCHEDULED = 2;
    public static final int RESCHEDULED = 3;
    public static final int PROCESSING = 4;
    public static final int REMOVED = 5;
    public static final int SUCCESSFULL = 6;
    public static final int ERROR = 7;
    public static final int ABORTED = 8;

    public static final String[] statusName = new String[] { "CREATED", "QUEUED", "SCHEDULED", "RESCHEDULED", "PROCESSING", "REMOVED", "SUCCESSFULL", "ERROR", "ABORTED" };

    public static String getStatusName(int type) {
        if (type < 0 || type >= statusName.length) {
            return null;
        }
        return statusName[type];
    }

    private int type = 0;
    private int status;
    private Calendar creationTime, startSchedulingTime, startProcessingTime, executionCompletedTime;
    private String jobId, userId;
    private String failureReason;
    private String description;
    private String cmdExecutorName = null;
	
    private long id = -1;

    public JobCommand() {
    }

    public JobCommand(String name) {
        this(name, null);
    }

    public JobCommand(String name, String jobId) {
        List<String> commandNames = JobCommandConstant.commandNameList;
        for (int i=0; i<commandNames.size(); i++) {
            if (name.equals(commandNames.get(i))) {
                this.jobId = jobId;
                this.type = i;
                this.status = CREATED;
                setCreationTime(Calendar.getInstance());
                
                break;
            }
        }
    }

    public JobCommand(int type) throws IllegalArgumentException {
        this(type, null);
    }

    public JobCommand(int type, String jobId) throws IllegalArgumentException {
        super();
        if (type >= 0 && type < JobCommandConstant.commandNameList.size()) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Job Command error: command type out of range (type=" + type + ")");
        }

        this.jobId = jobId;
        this.status = CREATED;
        setCreationTime(Calendar.getInstance());
    }

    public Calendar getCreationTime() {
        return creationTime;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getExecutionCompletedTime() {
        return executionCompletedTime;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getJobId() {
        return jobId;
    }

    public String getName() {
        return JobCommandConstant.commandNameList.get(type);
    }

    public Calendar getStartProcessingTime() {
        return startProcessingTime;
    }

    public Calendar getStartSchedulingTime() {
        return startSchedulingTime;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName[status];
    }

    public int getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSuccessfull() {
        return status == SUCCESSFULL;
    }
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    public void setCreationTime(Calendar creationTime) {
        this.creationTime = creationTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExecutionCompletedTime(Calendar executionCompletedTime) {
        this.executionCompletedTime = executionCompletedTime;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
        
        if (failureReason != null) {
            status = ERROR;
        }
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setStartProcessingTime(Calendar startProcessingTime) {
        this.startProcessingTime = startProcessingTime;
    }

    public void setStartSchedulingTime(Calendar startSchedulingTime) {
        this.startSchedulingTime = startSchedulingTime;
    }

    public void setStatus(int status) {
        if (status < 0 || status >= statusName.length) {
            return;
        }
        this.status = status;

        switch (status) {
        case SCHEDULED:
            startSchedulingTime = Calendar.getInstance();
            startProcessingTime = null;
            executionCompletedTime = null;
            break;
        case PROCESSING:
            startProcessingTime = Calendar.getInstance();
            executionCompletedTime = null;
            break;
        case ABORTED:
        case ERROR:
        case REMOVED:
        case SUCCESSFULL:
            executionCompletedTime = Calendar.getInstance();
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setType(int type) throws IllegalArgumentException {
        if (type >= 0 && type < JobCommandConstant.commandNameList.size()) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Job Command error: command type out of range (type=" + type + ")");
        }
    }
    
    public String getCommandExecutorName() {
        return cmdExecutorName;
    }

    public void setCommandExecutorName(String cmdExecutorName) {
        this.cmdExecutorName = cmdExecutorName;
    }

    public String toString() {
        StringBuffer info = new StringBuffer("commandName=");
        info.append(getCommandName(type));
        
        if(cmdExecutorName != null) {
            info.append("; cmdExecutorName=").append(cmdExecutorName);
        }
        
        if(userId != null) {
            info.append("; userId=").append(userId);
        }

        if(jobId != null) {
            info.append("; jobId=").append(jobId);
        }
        
        info.append("; status=").append(statusName[status]); 
        
        if(failureReason != null) {
            info.append("; failureReason=").append(failureReason);             
        }

        if(description != null) {
            info.append("; description=").append(description);             
        }

        info.append(";");
                
        return info.toString();
    }
}
