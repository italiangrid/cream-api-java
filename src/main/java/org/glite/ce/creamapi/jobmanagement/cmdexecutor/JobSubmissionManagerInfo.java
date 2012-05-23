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

import java.util.Calendar;

public class JobSubmissionManagerInfo implements Cloneable {
    private Calendar executionTimestamp = null;
    private String testErrorMessage = null;
    private boolean acceptNewJobs = true;
    private String showMessage = null;
    
    public String getShowMessage() {
       return showMessage;
    }
    
    public void setShowMessage(String showMessage) {
       this.showMessage = showMessage;
    }
    
    public boolean isAcceptNewJobs() {
        return acceptNewJobs;
    }
    
    public void setAcceptNewJobs(boolean acceptNewJobs) {
        this.acceptNewJobs = acceptNewJobs;
    }
    
    public Calendar getExecutionTimestamp() {
        return executionTimestamp;
    }
    
    public void setExecutionTimestamp(Calendar executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }
    
    public String getTestErrorMessage() {
        return testErrorMessage;
    }
    
    public void setTestErrorMessage(String testErrorMessage) {
        this.testErrorMessage = testErrorMessage;
    }
    
    public Object clone() {
        JobSubmissionManagerInfo clone = new JobSubmissionManagerInfo();
        clone.setExecutionTimestamp(executionTimestamp);
        clone.setTestErrorMessage(testErrorMessage);
        clone.setAcceptNewJobs(acceptNewJobs);
        clone.setShowMessage(showMessage);
        return clone;
    }
}
