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

package org.glite.ce.creamapi.activitymanagement.db;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.creamapi.activitymanagement.Activity;
import org.glite.ce.creamapi.activitymanagement.ActivityCommand;
import org.glite.ce.creamapi.activitymanagement.ActivityStatus;
import org.glite.ce.creamapi.activitymanagement.ActivityStatus.StatusName;
import org.glite.ce.creamapi.activitymanagement.ListActivitiesResult;

public interface ActivityDBInterface {    
    public static final String ACTIVITY_DATASOURCE_NAME = "datasource_esdb";

    public void deleteActivity(String activityId, String userId) throws DatabaseException, IllegalArgumentException;

    public Activity getActivity(String activityId, String userId) throws DatabaseException, IllegalArgumentException;

    public String insertActivity(Activity activity) throws DatabaseException, IllegalArgumentException;

    public void insertActivityCommand(String activityId, ActivityCommand activityCommand) throws DatabaseException, IllegalArgumentException;

    public void insertActivityStatus(String activityId, ActivityStatus activityStatus) throws DatabaseException, IllegalArgumentException;

    public ListActivitiesResult listActivities(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, List<ActivityStatus> statusList, int limit, String userId) throws DatabaseException, IllegalArgumentException; 

    public void updateActivity(Activity activity) throws DatabaseException, IllegalArgumentException;
    
    public void updateActivityCommand(ActivityCommand activityCommand) throws DatabaseException, IllegalArgumentException;

    public void updateActivityStatus(ActivityStatus activityStatus) throws DatabaseException, IllegalArgumentException;
    
    public String retrieveOlderActivityId(List<StatusName> statusList, String userId) throws DatabaseException, IllegalArgumentException;
    
    public List<String> listActivitiesForStatus(List<StatusName> statusList, String userId, int dateValue) throws DatabaseException, IllegalArgumentException;
}
