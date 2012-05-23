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
 *          Luigi Zangrando <zangrando@pd.infn.it>
 *          Silvano Squizzato <silvano.squizzato@pd.infn.it>
 *
 */

package org.glite.ce.creamapi.jobmanagement.db;

import java.util.Calendar;
import java.util.List;

import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.creamapi.eventmanagement.Event;
import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.ce.creamapi.jobmanagement.JobStatus;
import org.glite.ce.creamapi.jobmanagement.Lease;
import org.glite.ce.creamapi.jobmanagement.command.JobCommand;

public interface JobDBInterface {
    public static final String JOB_DATASOURCE_NAME = "datasource_creamdb";
     
    /**
     * Inserts all the related information about a given job into the dB.
     * @param job The job object to be inserted.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void insert(Job job) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Deletes a job identified by a given <code>jobId</code> if it exists.
     * The operation of deletion is performed only if the specified <code>userId</code> corresponds to the user persistently associated to the affected job in the database.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * @param jobId The job identifier.
     * @param userId The user identifier for the job being deleted.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void delete(String jobId, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Updates all the related information about a given job into the dB.
     * @param job The job object to be updated.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void update(Job job) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of job identifiers for jobs belonging to a given <code>userId</code>.
     * If no matching occurs in the database an empty list is returned.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs belonging to a given <code>userId</code>. If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<String> retrieveJobId(String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of job identifiers for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers.
     * The list of identifiers returned can only be a subset of the <code>jobId</code> list passed as a parameter.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobId The list of specified job identifiers.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers.
     * The list of identifiers returned can only be a subset of the <code>jobId</code> list passed as a parameter.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<String> retrieveJobId(List<String> jobId, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of job identifiers for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers
     * in one of the states described in the array <code>jobStatusType</code>.
     * The list of identifiers returned can only be a subset of the <code>jobId</code> list passed as a parameter.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobId The list of specified job identifiers.
     * @param userId The user identifier.
     * @param jobStatusType The array of status types.
     * @param startStatusDate It refers to the JobStatus.timestamp
     * @param endStatusDate It refers to the JobStatus.timestamp
     * @return The list of job identifiers for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public List<String> retrieveJobId(List<String> jobId, String userId, int[] jobStatusType,
    		Calendar startStatusDate, Calendar endStatusDate) throws DatabaseException, IllegalArgumentException;
    

    /**
     * 
     * @param userId The user identifier.
     * @param delegationId The delegation identifier.
     * @param jobStatusType The array of status types.
     * @param leaseId The lease identifier.
     * @param startStatusDate It refers to the JobStatus.timestamp
     * @param endStatusDate It refers to the JobStatus.timestamp
     * @param registerCommandStartDate It refers to the JobCommandTable.CREATION_TIME_FIELD for JobCommandTable.TYPE_FIELD=0
     * @param registerCommandEndDate It refers to the JobCommandTable.CREATION_TIME_FIELD for JobCommandTable.TYPE_FIELD=0
     * @return The list of job identifiers
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<String> retrieveJobId(String userId, String delegationId, int[] jobStatusType, 
    		Calendar startStatusDate, Calendar endStatusDate, String leaseId, 
    		Calendar registerCommandStartDate, Calendar registerCommandEndDate) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of job identifiers for jobs characterized by a given <code>delegId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param delegId The delegation identifier.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs characterized by a given <code>delegId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */       
    public List<String> retrieveJobId(String delegId, String userId) throws DatabaseException, IllegalArgumentException;
 
    /**
     * Retrieves a list of job identifiers for jobs belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobStatusType The array of status types.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */ 
    public List<String> retrieveJobId(int[] jobStatusType, String queueName, String batchSystem, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of job identifiers for jobs characterized by a given <code>delegId</code> and belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param delegId The delegation identifier.
     * @param jobStatusType The array of status types.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */     
    public List<String> retrieveJobId(String delegId, int[] jobStatusType, String userId) throws DatabaseException, IllegalArgumentException;

    
    public List<String> retrieveJobId(List<String> jobId, String delegId, String leaseId, String userId) throws DatabaseException, IllegalArgumentException;

    
    /**
     * Retrieves a list of job identifiers for jobs characterized one or more <code>gridJobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param delegId The delegation identifier.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs characterized one or more <code>gridJobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public List<String> retrieveJobIdByGridJobId(List<String> gridJobId, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Retrieves one object containing all the related information for a job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database <code>null</code> is returned.
     * @param jobId The job identifier.
     * @param userId The user identifier.
     * @return The one object containing all the related information for a job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database <code>null</code> is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public Job retrieveJob(String jobId, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Retrieves a list of job commands for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobId The job identifier.
     * @param userId The user identifier.
     * @return The list of job commands for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public List<JobCommand> retrieveCommandHistory(String jobId, String userId) throws DatabaseException, IllegalArgumentException;
    

    /**
     * Retrieves last job command for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs <code>null</code> is returned.
     * @param jobId The job identifier.
     * @param userId The user identifier.
     * @return The last job command for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs <code>null</code> is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public JobCommand retrieveLastCommand(String jobId, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Retrieves a list of job statuses for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobId The job identifier.
     * @param userId The user identifier.
     * @return The list of job statuses for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public List<JobStatus> retrieveJobStatusHistory(String jobId, String userId) throws DatabaseException, IllegalArgumentException;
 
    /**
     * Retrieves last job status for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs <code>null</code> is returned.
     * @param jobId The job identifier.
     * @param userId The user identifier.
     * @return The last job status for one job identified by a given <code>jobId</code> and belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs <code>null</code> is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public JobStatus retrieveLastJobStatus(String jobId, String userId) throws DatabaseException, IllegalArgumentException;
    
    /** Retrieves a list of job statuses for jobs belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param fromJobStatusId Inferior id for the jobStatus identifier.
     * @param toJobStatusId Superior id for the jobStatus identifier.
     * @param fromDate Inferior time date for job status creation time.
     * @param toDate Superior time date for job status creation time.
     * @param maxElements maximum number of jobStatus objects retrieved.
     * @param userId The user identifier.
     * @return Retrieves a list of job statuses for jobs belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<JobStatus> retrieveJobStatus(String fromJobStatusId, String toJobStatusId, Calendar fromDate, Calendar toDate, int maxElements, String userId) throws DatabaseException, IllegalArgumentException;
    
    /** Retrieves a list of Event objects for jobs belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param fromJobStatusId Inferior id for the jobStatus identifier.
     * @param toJobStatusId Superior id for the jobStatus identifier.
     * @param fromDate Inferior time date for job status creation time.
     * @param toDate Superior time date for job status creation time.
     * @param maxElements maximum number of jobStatus objects retrieved.
     * @param userId The user identifier.
     * @return Retrieves a list of Event objects for jobs belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<Event> retrieveJobStatusAsEvent(String fromJobStatusId, String toJobStatusId, Calendar fromDate, Calendar toDate, int[] jobStatusType, int maxElements, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of job statuses for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobId The list of specified job identifiers.
     * @param userId The user identifier.
     * @return The list of job statuses for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public List<JobStatus> retrieveLastJobStatus(List<String> jobId, String userId) throws DatabaseException, IllegalArgumentException;
 
    /**
     * Retrieves a list of job identifiers for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers
     * with a creation timestamp in the interval delimited by <code>startDate</code> and <code>endDate</code>.
     * The list of identifiers returned can only be a subset of the <code>jobId</code> list passed as a parameter.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param jobId The list of specified job identifiers.
     * @param userId The user identifier.
     * @param startDate Inferior time date for job creation time.
     * @param endDate Superior time date for job creation time.
     * @return The list of job identifiers for jobs belonging to a given <code>userId</code> out of a list <code>jobId</code> of specified job identifiers
     * with a creation timestamp in the interval delimited by <code>startDate</code> and <code>endDate</code>.
     * The list of identifiers returned can only be a subset of the <code>jobId</code> list passed as a parameter.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */  
    public List<String> retrieveByDate(List<String> jobId, String userId,
                                  Calendar startDate, Calendar endDate) throws DatabaseException, IllegalArgumentException;
 
    /**
     * Inserts a job status in the dB for a job belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * @param status The job status.
     * @param userId The user identifier.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */ 
    public void insertStatus(JobStatus status, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Updates a job status in the dB for a job belonging to a given <code>userId</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * @param status The job status.
     * @param userId The user identifier.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */    
    public void updateStatus(JobStatus status, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Inserts a job command in the dB.
     * @param jobCommand The job command.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void insertJobCommand(JobCommand jobCommand) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Inserts a job command in the dB.
     * @param jobCommand The job command.
     * @param delegationId The delegationId.
     * @param jobStatusType The array of status types.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void insertJobCommand(JobCommand jobCommand, String delegationId, int[] jobStatusType) throws DatabaseException, IllegalArgumentException;

    /**
     * Updates last occurrence of a job command in the dB.
     * @param jobCommand The job command.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void updateJobCommand(JobCommand jobCommand) throws DatabaseException, IllegalArgumentException;

    /**
     * Retrieves a list of job identifiers for jobs characterized by a given <code>leaseId</code> and belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param leaseId The lease identifier.
     * @param jobStatusType The array of status types.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs characterized by a given <code>leaseId</code> and belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */     
    public List<String> retrieveJobIdByLease(int[] jobStatusType, String leaseId, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Retrieves a list of job identifiers for jobs characterized by a maximum lease time <code>maxLeaseTime</code> and belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @param maxLeaseTime The maximum lease time.
     * @param jobStatusType The array of status types.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs characterized by a maximum lease time <code>maxLeaseTime</code> and belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * A <code>userId</code> <code>null</code> means that the operation is being invoked by an administrator, so that the operation is anyway performed.
     * If no matching occurs in the database an empty list is returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */     
    public List<String> retrieveJobIdByLease(int[] jobStatusType, Calendar maxLeaseTime, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * 
     * @param jobStatusType The array of status types.
     * @param delegationId The delegation identifier.
     * @param userId The user identifier.
     * @return The list of job identifiers for jobs characterized by leaseTime not null and belonging to a given <code>userId</code>
     * in one of the states described in the array <code>jobStatusType</code>.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<String> retrieveJobIdLeaseTimeExpired(int[] jobStatusType, String delegationId, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Retrieves a lease identified by a given <code>leaseId</code> for jobs belonging to a given <code>userId</code>.
     * <code>leaseId</code> must not be <code>null</code>.
     *  <code>userId</code> must not be <code>null</code>.
     * If no matching occurs <code>null</code> returned.
     * @param leaseId The lease identifier.
     * @param userId The user identifier.
     * @return The lease identified by a given <code>leaseId</code> for jobs belonging to a given <code>userId</code>.
     * If no matching occurs <code>null</code> returned.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */  
    public Lease retrieveJobLease(String leaseId, String userId) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Retrieves a list of leases for jobs belonging to a given <code>userId</code>.
     * @param userId The user identifier.
     * @return The list of leases for jobs belonging to a given <code>userId</code>.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */ 
    public List<Lease> retrieveJobLease(String userId) throws DatabaseException, IllegalArgumentException;
 
    /**
     * Retrieves a list of leases for jobs belonging to a given <code>userId</code> with a maximum lease time <code>maxLeaseTime</code>.
     * <code>userId</code> must not be <code>null</code>.
     * @param maxLeaseTime The maximum lease time.
     * @param userId The user identifier.
     * @return The list of leases for jobs belonging to a given <code>userId</code> with a maximum lease time <code>maxLeaseTime</code>.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public List<Lease> retrieveJobLease(Calendar maxLeaseTime, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * Inserts a lease into the dB.
     * @param jobLease The lease object to be inserted.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void insertJobLease(Lease jobLease) throws DatabaseException, IllegalArgumentException;
    
    /**
     * Updates a lease into the dB.
     * @param jobLease The lease object to be updated.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void updateJobLease(Lease jobLease) throws DatabaseException, IllegalArgumentException;


    /**
     * Deletes a lease for jobs belonging to a given <code>userId</code> identified by a given <code>userId</code>.
     * <code>leaseId</code> and <code>userId</code> must not be <code>null</code>.
     * @param leaseId The lease identifier.
     * @param userId The user identifier.
     * @throws DatabaseException
     * @throws IllegalArgumentException
     */
    public void deleteJobLease(String leaseId, String userId) throws DatabaseException, IllegalArgumentException;
   
   /**
    * Sets leaseId field in job identified by jobId and userId.
    * @param leaseId The lease identifier.
    * @param jobId The job identifier.
    * @param userId The user identifier.
    * @throws DatabaseException if job doesn't exist or job is expired.
    * @throws IllegalArgumentException if parameters aren't correct (i.e. null).
    */
    public void setLeaseId(String leaseId, String jobId, String userId) throws DatabaseException, IllegalArgumentException;
   
    /**
     * Sets job (i.e. leaseTime field in JobTable) as expired. 
     * 
     * @param lease The lease object expired.
     * @throws DatabaseException
     * @throws IllegalArgumentException if parameters aren't correct (i.e. null).
     */
    public void setLeaseExpired(Lease lease) throws DatabaseException, IllegalArgumentException;

    /**
     * Sets the job identified by jobId as expired. 
     *
     * @param jobId The job identifier.
     * @param lease The lease object expired.
     * @throws DatabaseException
     * @throws IllegalArgumentException if parameters aren't correct (i.e. null).
     */
    public void setLeaseExpired(String jobId, Lease lease) throws DatabaseException, IllegalArgumentException;

    /**
     * 
     * @param jobStatusType The array of status types.
     * @param userId The user identifier.
     * @return The number of job having status in jobStatusType array.
     * @throws DatabaseException
     * @throws IllegalArgumentException if parameters aren't correct
     */
    public long jobCountByStatus(int[] jobStatusType, String userId) throws DatabaseException, IllegalArgumentException;

    /**
     * 
     * @param jobStatusType The array of status types.
     * @param batchSystem The batch System (for example: pbs)
     * @param userId The user identifier.
     * @return The older of job having status in jobStatusType array.
     * @throws DatabaseException
     * @throws IllegalArgumentException if parameters aren't correct
     */
    public String retrieveOlderJobId(int[] jobStatusType, String batchSystem, String userId) throws DatabaseException, IllegalArgumentException;
    
    public int updateAllUnterminatedJobCommand() throws DatabaseException;
    
    public void updateDelegationProxyInfo(String delegationId, String delegationProxyInfo, String userId) throws DatabaseException, IllegalArgumentException;
}
