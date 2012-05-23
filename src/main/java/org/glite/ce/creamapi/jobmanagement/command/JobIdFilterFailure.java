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

package org.glite.ce.creamapi.jobmanagement.command;

public class JobIdFilterFailure {
   public static final int OK_ERRORCODE           = 0;
   public static final int JOBID_ERRORCODE        = 1;
   public static final int STATUS_ERRORCODE       = 2;
   public static final int LEASEID_ERRORCODE      = 3;
   public static final int DELEGATIONID_ERRORCODE = 4;
   public static final int DATE_ERRORCODE         = 5;
   
   
   public static final String[] failureReason = new String[] { null,
	                                                           "JobId unknown",
	                                                           "Status not compatible",
	                                                           "LeaseId Mismatch",
	                                                           "DelegationId Mismatch",
	                                                           "Date Mismatch"
                                                             };

}
