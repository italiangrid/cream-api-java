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

package org.glite.ce.creamapi.jobmanagement.db.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.glite.ce.creamapi.eventmanagement.Event;
import org.glite.ce.creamapi.jobmanagement.JobStatus;

public interface JobStatusTableInterface {

	public int executeInsert(List<JobStatus> jobStatusList, Connection connection) throws SQLException;
	public JobStatus executeSelectLastJobStatus(String jobId, Connection connection) throws SQLException;
	public List<JobStatus> executeSelectJobStatusHistory(String jobId, Connection connection) throws SQLException;
	public List<JobStatus> executeSelectToRetrieveJobStatus(String fromJobStatusId, String toJobStatusId, Calendar fromDate, Calendar toDate, int maxElements, String iceId, String userId,  Connection connection) throws SQLException;
	public List<Event> executeSelectToRetrieveJobStatusAsEvent(String fromJobStatusId, String toJobStatusId, Calendar fromDate, Calendar toDate, int[] jobStatusType, int maxElements, String iceId, String userId, Connection connection) throws SQLException;
	public void executeUpdateStatusHistory(String jobId, List<JobStatus> jobStatusList, Connection connection) throws SQLException;
	public void executeUpdateJobStatus(JobStatus jobStatus, Connection connection) throws SQLException;
	public int executeDelete(String jobId, Connection connection) throws SQLException;
}
