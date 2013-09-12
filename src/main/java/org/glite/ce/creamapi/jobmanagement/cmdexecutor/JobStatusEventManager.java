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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.creamapi.eventmanagement.Event;
import org.glite.ce.creamapi.eventmanagement.EventManagerException;
import org.glite.ce.creamapi.jobmanagement.db.JobDBInterface;

public class JobStatusEventManager implements JobStatusEventManagerInterface {

    public final static String TYPE_PROPERTYNAME = "type";
    public final static String EXIT_CODE_PROPERTYNAME = "exitCode";
    public final static String FAILURE_REASON_PROPERTYNAME = "failureReason";
    public final static String DESCRIPTION_PROPERTYNAME = "description";
    public final static String JOB_ID_PROPERTYNAME = "jobId";
    public final static String GRID_JOB_ID_PROPERTYNAME = "gridJobId";
    public final static String WORKER_NODE_PROPERTYNAME = "workerNode";

    public static final String MANAGER_TYPE = "JOBSTATUS";

    private static final Logger logger = Logger.getLogger(JobStatusEventManager.class.getName());

    private JobDBInterface jobDB = null;
    private int maxEvents = 1;

    public JobStatusEventManager(JobDBInterface jobDB, int maxEvents) {
        if (jobDB == null) {
            throw new IllegalArgumentException("JobStatusEventManager: parameter must be not null!");
        }
        this.jobDB = jobDB;
        this.maxEvents = maxEvents;
        logger.debug("maxEvents = " + maxEvents);
    }

    public void deleteEvent(String eventId, String userId) throws IllegalArgumentException, EventManagerException {
        logger.warn("Method not implemented!");
        throw new EventManagerException("Method not implemented!");
    }

    public void deleteEvents(String userId) throws IllegalArgumentException, EventManagerException {
        logger.warn("Method not implemented!");
        throw new EventManagerException("Method not implemented!");
    }

    public Event getEvent(String eventId, String userId) throws IllegalArgumentException, EventManagerException {
        Event event = null;
        if (eventId == null) {
            throw new IllegalArgumentException("paramenter eventId must be not null!");
        }
        List<Event> eventList = null;
        try {
            eventList = jobDB.retrieveJobStatusAsEvent(eventId, null, null, null, null, 1, userId);
        } catch (DatabaseException de) {
            logger.error("Error retrieving events from database: " + de.getMessage());
            throw new EventManagerException("Error retrieving events from database");
        }
        if (eventList.size() != 0) {
            event = eventList.get(0);
        }
        return event;
    }

    public List<Event> getEvents(String fromEventId, String toEventId, Calendar fromDate, Calendar toDate, int maxEvents, String userId) throws IllegalArgumentException, EventManagerException {
        return getEvents(fromEventId, toEventId, fromDate, toDate, null, Math.min(maxEvents, this.maxEvents), userId);      
    }

    public List<Event> getEvents(String fromEventId, String toEventId, Calendar fromDate, Calendar toDate, int[] jobStatusType, int maxEvents, String userId) throws IllegalArgumentException, EventManagerException {
        List<Event> eventList = new ArrayList<Event>(0);
        try {
            eventList = jobDB.retrieveJobStatusAsEvent(fromEventId, toEventId, fromDate, toDate, jobStatusType, Math.min(maxEvents, this.maxEvents), userId);
        } catch (DatabaseException de) {
            logger.error("Error retrieving events from database: " + de.getMessage());
            throw new EventManagerException("Error retrieving events from database");
        }
        return eventList;
    }

    public void insertEvent(Event event, String userId) throws IllegalArgumentException, EventManagerException {
        logger.warn("Method not implemented!");
        throw new EventManagerException("Method not implemented!");
    }
}
