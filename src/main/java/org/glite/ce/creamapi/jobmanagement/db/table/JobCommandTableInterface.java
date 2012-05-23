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

import org.glite.ce.creamapi.jobmanagement.command.JobCommand;


public interface JobCommandTableInterface {
	public int executeInsert(List<JobCommand> jobStatusList, Connection connection) throws SQLException;
	public JobCommand executeSelectLastJobCommmand(String jobId, Connection connection) throws SQLException;
	public List<String> executeSelectToRetrieveJobIdByDate(List<String> jobId, String userId, Calendar startDate, Calendar endDate, Connection connection) throws SQLException;
	public List<JobCommand> executeSelectJobCommandHistory(String jobId, Connection connection) throws SQLException;
	public void executeUpdateCommandHistory(String jobId, List<JobCommand> jobStatusList, Connection connection) throws SQLException;
	public void executeUpdateJobCommand(JobCommand jobCommand, Connection connection) throws SQLException;
    public int executeUpdateAllUnterminatedJobCommandQuery(int newStatus, int[] oldStatus, String failureReason, Calendar executionCompletedTime, Connection connection) throws SQLException;
	public int executeDelete(String jobId, Connection connection) throws SQLException;
}
