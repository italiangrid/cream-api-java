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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.creamapi.jobmanagement.db.JobDBInterface;
import org.glite.ce.creamapi.jobmanagement.JobManagementException;

            
public class JobArrayList {
    private static Logger logger = Logger.getLogger(JobArrayList.class.getName());

    private List<String> jobIdList = null;
    private JobDBInterface db      = null;

    public JobArrayList() {
	    this.jobIdList = new ArrayList<String>(0);
    }
    
    public JobArrayList(JobDBInterface db) {
	    this.jobIdList = new ArrayList<String>(0);
        this.db = db;
    }
    
    public JobArrayList(final List<String> jobIdList, JobDBInterface db) {
	    this.jobIdList = (jobIdList == null? new ArrayList<String>(0): jobIdList);	
        this.db = db;
    }
        
    public List<String> getJobIdList() {
	    return jobIdList;
    }

    public JobDBInterface getJobDB() {
        return db;
    }

    public void setJobDB(JobDBInterface jobdb) {
        db = jobdb;
    }
        
//    public void addAll(JobArrayList list) {
//	    if(list != null) {
//	        jobIdList.addAll(list.getJobIdList());
//	    }
//    }
//               
//    public void addJob(Job job) {
//	    if(job != null) {
//	        jobIdList.add(job.getId() + "$" + job.getUserDN_RFC2253());
//	    }
//    }
//          
//    public void addJob(Job[] job) {
//        if(job == null) {
//            return;
//        }
//
//        for (int i = 0; i < job.length; i++) {
//	        jobIdList.add(job[i].getId() + "$" + job[i].getUserDN_RFC2253());
//        }
//    }

//    public Job getJob(int index) {
//        return getJob(index, false);
//    }
//    
//    public Job getJob(int index, boolean full) {
//	    if(index < 0 || index > jobIdList.size()-1) {
//	        return null;
//	    }
//        try {
//            Job job = (Job) db.retrieveJob((String)jobIdList.get(index));
//            if(full) {
//                job.setCommandHistory(db.retrieveCommands((String)jobIdList.get(index)));
//            }
//            return job;
//        } catch (DatabaseException e) {
//            return null;
//        }
//    }
//    
    
    public Job getJob(int index) throws JobManagementException{
    	Job job = null;
        if(index < 0 || index > jobIdList.size()-1) {
            return null;
        }
        try {
            job = db.retrieveJob((String)jobIdList.get(index), null);
        } catch (DatabaseException e) {
        	logger.error("DatabaseException: " + e.getMessage());
            throw new JobManagementException(e.getMessage());
           
        }
        return job;
    }
        
    public String getJobId(int index) {
	    if(index < 0 || index > jobIdList.size()-1) {
	        return null;
	    }
	        
	    return (String)jobIdList.get(index);
    }
        
    
//    public Job removeJob(int index) {
//	    if(index >= 0 || index < jobIdList.size()) {
//	        String id = (String)jobIdList.remove(index);
//	        
//    	    int i = id.indexOf("$");
//	    
//	        return db.lookupJobByID(id.substring(0, i), id.substring(i+1));
//	    }
//	    
//	    return null;
//    }
    
    public int size() {
        if(jobIdList == null) {
            return 0;
        }
        
	    return jobIdList.size();
    }
    
    public boolean isEmpty() {
        return jobIdList.isEmpty();
    }
        
    public void clear() {
       jobIdList.clear();
    }
    
}
