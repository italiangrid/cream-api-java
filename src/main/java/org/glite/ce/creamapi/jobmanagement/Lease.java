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

package org.glite.ce.creamapi.jobmanagement;

import java.util.Calendar;

public class Lease {

	private String    leaseId   = null;
	private Calendar  leaseTime = null;
	private String    userId    = null;

    public Lease() {
    }
    
	public Lease(String leaseId, String userId, Calendar leaseTime) {
	    this.leaseId = leaseId;
	    this.userId = userId;
	    this.leaseTime = leaseTime;
	}

	public String getLeaseId() {
		return leaseId;
	}
	public void setLeaseId(String leaseId) {
		this.leaseId = leaseId;
	}
	public Calendar getLeaseTime() {
		return leaseTime;
	}
	public void setLeaseTime(Calendar leaseTime) {
		this.leaseTime = leaseTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public boolean isExpired(){
		boolean leaseTimeExpired = false;
        Calendar now = Calendar.getInstance();

        if ((leaseTime != null) && (leaseTime.before(now))) {
        	leaseTimeExpired = true;
        }
        return leaseTimeExpired;
	}
}
