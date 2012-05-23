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

package org.glite.ce.creamapi.cmdmanagement;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class Command implements Comparable {
    private final static Logger logger = Logger.getLogger(Command.class.getName());

    public enum ExecutionModeValues {
        SERIAL(EXECUTION_MODE_SERIAL), PARALLEL(EXECUTION_MODE_PARALLEL);
        private final String stringValue;

        ExecutionModeValues(String stringValue) {
            this.stringValue = stringValue;
        }

        public boolean equals(ExecutionModeValues value) {
            if (value == null) {
                return false;
            }

            return value.getStringValue().equals(stringValue);
        }

        public boolean equals(String value) {
            if (value == null) {
                return false;
            }

            return value.equals(stringValue);
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    private static final long serialVersionUID = 1L;

    public static final int LOW_PRIORITY = 0;
    public static final int NORMAL_PRIORITY = 1;
    public static final int MEDIUM_PRIORITY = 2;
    public static final int HIGH_PRIORITY = 3;

    public static final int ACCEPTED = 0;
    public static final int QUEUED = 1;
    public static final int SCHEDULED = 2;
    public static final int RESCHEDULED = 3;
    public static final int EXECUTING = 4;
    public static final int EXECUTED_OK = 5;
    public static final int EXECUTED_ERROR = 6;
    public static final int ERROR = 7;

    public static final String[] statusName = new String[] { "ACCEPTED", "QUEUED", "SCHEDULED", "RESCHEDULED", "EXECUTING", "EXECUTED-OK", "EXECUTED-ERROR", "ERROR" };
    public static final String EXECUTION_MODE_SERIAL = "S";
    public static final String EXECUTION_MODE_PARALLEL = "P";

    public static String getStatusName(int type) {
        if (type < 0 || type >= statusName.length) {
            return null;
        }
        return statusName[type];
    }

    // private long id = 0L;
    private long queueId = 0L;
    private long id = -1;
    private String cmdExecutorName = null;
    private String commandGroupId = null;
    private String userId = null;
    private String name = null;
    private String category = null;
    private String failureReason = null;
    private String description = null;
    private Calendar creationTime, startSchedulingTime, startProcessingTime, executionCompletedTime;
    private Hashtable<String, Object> parameter = null;
    private boolean isAsynchronous = false;
    private int status;
    private int priorityLevel = NORMAL_PRIORITY;
    private CommandResult result;
    private ExecutionModeValues executionMode = ExecutionModeValues.SERIAL;

    public Command() {
        this(null, null);
    }

    public Command(String name) {
        this(name, null);
    }

    public Command(String name, String category) {
        super();
        setName(name);
        setCreationTime(Calendar.getInstance());
        status = ACCEPTED;
        parameter = new Hashtable<String, Object>(0);
        this.category = category;
    }

    public void addParameter(String key, List<String> value) {
        if (key != null && value != null) {
            if (parameter.containsKey(key)) {
                parameter.remove(key);
            }
            parameter.put(key.toUpperCase(), value);
        }
    }

    public void addParameter(String key, Serializable value) {
        if (key != null && value != null) {
            if (parameter.containsKey(key)) {
                parameter.remove(key);
            }
            parameter.put(key.toUpperCase(), value);
        }
    }

    public void addParameter(String key, Object value) {
        if (key != null && value != null) {
            if (parameter.containsKey(key)) {
                parameter.remove(key);
            }
            parameter.put(key.toUpperCase(), value);
        }
    }

    public boolean checkResult() {
        return result != null;
    }

    public void clearParameter() {
        parameter.clear();
    }

    public int compareTo(Object obj) {
        if (obj instanceof Command) {
            if (id < ((Command)obj).getId()) {
                return -1;
            } else {
                return id == ((Command)obj).getId() ? 0 : 1;
            }
        }

        return 0;
    }

    public boolean containsParameterKey(String key) {
        return parameter.containsKey(key.toUpperCase());
    }

    public void deleteParameter(String key) {
        if (key != null && parameter.containsKey(key.toUpperCase())) {
            parameter.remove(key.toUpperCase());
        }
    }

    public String getCategory() {
        return category;
    }

    public String getCommandExecutorName() {
        return cmdExecutorName;
    }

    public String getCommandGroupId() {
        return commandGroupId;
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

    public ExecutionModeValues getExecutionMode() {
        return executionMode;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Object getParameter(String key) {
        if (key == null) {
            return null;
        }
        return parameter.get(key.toUpperCase());
    }

    public String getParameterAsString(String key) {
        if (key == null) {
            return null;
        }
        return (String) parameter.get(key.toUpperCase());
    }

    public Set<String> getParameterKeySet() {
        return parameter.keySet();
    }

    public List<String> getParameterMultivalue(String key) {
        if (key == null) {
            return null;
        }

        Object obj = parameter.get(key.toUpperCase());
        if (obj instanceof String) {
            ArrayList<String> list = new ArrayList<String>(0);
            list.add((String) obj);
            return list;
        }
        return (List<String>) obj;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public long getQueueId() {
        return queueId;
    }

    public CommandResult getResult() {
        if (result == null) {
            result = new CommandResult();
        }

        return result;
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

    public String getUserId() {
        return userId;
    }

    public boolean isAsynchronous() {
        return isAsynchronous;
    }

    public boolean isInternal() {
        return !containsParameterKey("REMOTE_REQUEST_ADDRESS");
    }

    public boolean isScheduled() {
        return (status == SCHEDULED || status == RESCHEDULED);
    }

    public boolean isSuccessfull() {
        return status == EXECUTED_OK;
    }

    private Calendar readCalendar(ObjectInput in) throws IOException {
        long ts = in.readLong();
        if (ts == 0)
            return null;
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(ts);
        return result;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setCategory(in.readUTF());
        cmdExecutorName = in.readUTF();
        userId = in.readUTF();
        setName(in.readUTF());
        setStatus(in.readInt());
        setPriorityLevel(in.readInt());
        String executionModeRead = in.readUTF();
        if ("S".equals(executionModeRead)) {
            this.setExecutionMode(ExecutionModeValues.SERIAL);
        } else if ("P".equals(executionModeRead)) {
            this.setExecutionMode(ExecutionModeValues.PARALLEL);
        } else {
            throw new IOException("ExecutionMode parameter is not correct: " + executionModeRead);
        }
        setCommandGroupId(in.readUTF());
        setFailureReason(in.readUTF());
        setCreationTime(readCalendar(in));
        setStartSchedulingTime(readCalendar(in));
        setStartProcessingTime(readCalendar(in));
        setExecutionCompletedTime(readCalendar(in));
        isAsynchronous = in.readBoolean();

        int htSize = in.readInt();
        parameter.clear();
        if (htSize >= 0) {
            for (int k = 0; k < htSize; k++) {
                parameter.put(in.readUTF(), in.readObject());
            }
        }
    }

    public synchronized void setAsynchronous(boolean b) {
        isAsynchronous = b;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCommandExecutorName(String name) {
        cmdExecutorName = name;
    }

    public void setCommandGroupId(String commandGroupId) {
        this.commandGroupId = commandGroupId;
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

    public void setExecutionMode(ExecutionModeValues executionMode) {
        this.executionMode = executionMode;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public void setQueueId(long queueId) {
        this.queueId = queueId;
    }

    public void setResult(CommandResult result) {
        this.result = result;
    }

    public void setStartProcessingTime(Calendar startProcessingTime) {
        this.startProcessingTime = startProcessingTime;
    }

    public void setStartSchedulingTime(Calendar startSchedulingTime) {
        this.startSchedulingTime = startSchedulingTime;
    }

    public void setStatus(int newStatus) {
        if (newStatus < 0 || newStatus >= statusName.length) {
            return;
        }

        status = newStatus;

        switch (status) {
        case SCHEDULED:
            startSchedulingTime = Calendar.getInstance();
            startProcessingTime = null;
            executionCompletedTime = null;
            break;
        case EXECUTING:
            startProcessingTime = Calendar.getInstance();
            executionCompletedTime = null;
            break;
        case ERROR:
        case EXECUTED_OK:
        case EXECUTED_ERROR:
            executionCompletedTime = Calendar.getInstance();
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toString() {
        StringBuffer info = new StringBuffer();
        if (id != -1) {
            info.append("ID=").append(id).append("; ");
        }

        info.append("NAME=\"").append(name).append("\"");
        info.append("; PRIORITY_LEVEL=").append(priorityLevel);
        info.append("; IS_ASYNCHRONOUS=").append(isAsynchronous);
        info.append("; STATUS=").append(statusName[status]);
        
        if (failureReason != null) {
            info.append("; FAILURE_REASON=\"").append(failureReason).append("\"");
        }
        
        if (category != null) {
            info.append("; CATEGORY=\"").append(category).append("\"");
        }

        if (cmdExecutorName != null) {
            info.append("; EXECUTOR_NAME=\"").append(cmdExecutorName).append("\"");
        }

        if (userId != null) {
            info.append("; USER_ID=\"").append(userId).append("\"");
        }
        
        if (description != null) {
            info.append("; DESCRIPTION=\"").append(description).append("\"");
        }

        if (creationTime != null) {
            info.append("; CREATION_TIME=\"").append(creationTime.getTime()).append("\"");
        }

        if (startProcessingTime != null) {
            info.append("; START_PROCESSING_TIME=\"").append(startProcessingTime.getTime()).append("\"");
        }

        if (executionCompletedTime != null) {
            info.append("; EXECUTION_COMPLETED_TIME=\"").append(executionCompletedTime.getTime()).append("\"");
        }

        Object parameterValue = null;
        
        for (String key : parameter.keySet()) {
            if (key.startsWith("DELEGATION_") && !key.endsWith("_ID")) {
                continue;
            }

            parameterValue = parameter.get(key);
            
            if (parameterValue instanceof String) {                
                if (((String)parameterValue).length() < 100) {
                    info.append("; ").append(key).append("=\"").append(parameterValue).append("\"");
                }
            } else if (parameterValue instanceof List<?>) {
                List<?> list = (List<?>)parameterValue;

                if (list.size() > 0 && list.get(0) instanceof String) {
                    info.append("; ").append(key).append("={ ");
                    for (String value : (List<String>)list) {
                        info.append(value).append("; ");
                    }
                    info.replace(info.length()-2, info.length(), " }");
                }
            }
        }

        return info.toString();
    }

    private void writeCalendar(ObjectOutput out, Calendar cal) throws IOException {
        out.writeLong(cal != null ? cal.getTimeInMillis() : 0);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        writeString(out, getCategory());
        writeString(out, cmdExecutorName);
        writeString(out, userId);
        writeString(out, getName());
        out.writeInt(status);
        out.writeInt(getPriorityLevel());
        writeString(out, getExecutionMode().getStringValue());
        writeString(out, getCommandGroupId());
        writeString(out, getFailureReason());
        writeCalendar(out, getCreationTime());
        writeCalendar(out, getStartSchedulingTime());
        writeCalendar(out, getStartProcessingTime());
        writeCalendar(out, getExecutionCompletedTime());
        out.writeBoolean(isAsynchronous);

        if (parameter != null) {
            out.writeInt(parameter.size());
            for (String key : parameter.keySet()) {
                out.writeUTF(key);
                out.writeObject(parameter.get(key));
            }
        } else {
            out.writeInt(-1);
        }
    }

    private void writeString(ObjectOutput out, String s) throws IOException {
        out.writeUTF(s != null ? s : "");
    }
}
