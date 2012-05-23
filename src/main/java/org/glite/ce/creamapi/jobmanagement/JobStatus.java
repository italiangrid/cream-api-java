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
 
package org.glite.ce.creamapi.jobmanagement;

import java.util.Calendar;

public final class JobStatus {
    public static final int REGISTERED = 0;
    public static final int PENDING = 1;
    public static final int IDLE = 2;
    public static final int RUNNING = 3;
    public static final int REALLY_RUNNING = 4;
    public static final int CANCELLED = 5;
    public static final int HELD = 6;
    public static final int DONE_OK = 7;
    public static final int DONE_FAILED = 8;
    public static final int PURGED = 9;
    public static final int ABORTED = 10;

    public static final String[] statusName = new String[] { "REGISTERED", "PENDING", "IDLE", "RUNNING", "REALLY-RUNNING", "CANCELLED", "HELD", "DONE-OK", "DONE-FAILED", "PURGED", "ABORTED" };

    public static String getNameByType(int type) {
        if (type < 0 || type >= statusName.length) {
            return null;
        }
        return statusName[type];
    }

    private String exitCode, failureReason, description, jobId;

    private int type = 0;

    private Calendar timestamp;

    private long id = -1;

    public JobStatus(int type) {
        this(type, null);
    }

    public JobStatus(int type, String jobId) {
        this(type, jobId, null);
    }

    public JobStatus(int type, String jobId, Calendar time) {
        this.type = type;
        this.jobId = jobId;
        
        if (time == null) {
            timestamp = Calendar.getInstance();
            timestamp.set(Calendar.MILLISECOND, 0);
        } else {
            this.timestamp = time;            
        }
    }

    public String getDescription() {
        return description;
    }

    public String getExitCode() {
        return exitCode;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public long getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public String getName() {
        if (type < 0 || type >= statusName.length) {
            return null;
        }
        return statusName[type];
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(int type) {
        this.type = type;
    }
}
