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
import java.util.List;

import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.ce.creamapi.jobmanagement.Lease;

public interface JobTableInterface {
    public int executeDelete(String jobId, Connection connection) throws SQLException;

    public int executeInsert(Job job, Connection connection) throws SQLException;

    public Job executeSelectJobTable(String jobId, String userId, Connection connection) throws SQLException;

    public List<String> executeSelectToRetrieveJobId(String userId, List<String> jobId, String leaseId, String delegationId, List<String> gridJobId, Connection connection) throws SQLException;

    public int executeUpdate(Job job, Connection connection) throws SQLException;

    public String getLeaseId(String jobId, String userId, Connection connection) throws SQLException;

    public String getReasonFaultSetLeaseId(String jobId, String userId, Connection connection) throws SQLException;

    public boolean isUserEnable(String jobId, String userId, Connection connection);

    public void setLeaseExpired(String jobId, Lease jobLease, Connection connection) throws SQLException;;
}
