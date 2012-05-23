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

package org.glite.ce.creamapi.activitymanagement.db;

import java.util.Calendar;

import javax.xml.datatype.Duration;

public class ActivityDB {
    private String activityId = null;
    private String userId = null;
    private String name = null;
    private String description = null;
    private String type = null;
    private String input = null;
    private String output = null;
    private String error = null;
    private Calendar expirationTimeDate = null;
    private boolean expirationTimeOptional = false;
    private Duration wipeTimeDuration = null;
    private boolean wipeTimeOptional = false;
    private boolean clientDataPush = false;
    private String queueName = null;
    
    public String getActivityId() {
        return activityId;
    }
    
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getInput() {
        return input;
    }
    
    public void setInput(String input) {
        this.input = input;
    }
    
    public String getOutput() {
        return output;
    }
    
    public void setOutput(String output) {
        this.output = output;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Calendar getExpirationTimeDate() {
        return expirationTimeDate;
    }
    
    public void setExpirationTimeDate(Calendar expirationTimeDate) {
        this.expirationTimeDate = expirationTimeDate;
    }
    
    public boolean isExpirationTimeOptional() {
        return expirationTimeOptional;
    }
    
    public void setExpirationTimeOptional(boolean expirationTimeOptional) {
        this.expirationTimeOptional = expirationTimeOptional;
    }
    
    public Duration getWipeTimeDuration() {
        return wipeTimeDuration;
    }
    
    public void setWipeTimeDuration(Duration wipeTimeDuration) {
        this.wipeTimeDuration = wipeTimeDuration;
    }
    
    public boolean isWipeTimeOptional() {
        return wipeTimeOptional;
    }
    
    public void setWipeTimeOptional(boolean wipeTimeOptional) {
        this.wipeTimeOptional = wipeTimeOptional;
    }

    public boolean isClientDataPush() {
        return clientDataPush;
    }

    public void setClientDataPush(boolean clientDataPush) {
        this.clientDataPush = clientDataPush;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
}
