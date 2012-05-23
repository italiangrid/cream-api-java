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

package org.glite.ce.creamapi.activitymanagement.db.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.glite.ce.creamapi.activitymanagement.ActivityStatus;
import org.glite.ce.creamapi.activitymanagement.ListActivitiesResult;
import org.glite.ce.creamapi.activitymanagement.ActivityStatus.StatusAttributeName;
import org.glite.ce.creamapi.activitymanagement.ActivityStatus.StatusName;

public interface ActivityStatusTableInterface {
    public static final String ID_LABEL = "id";
    public static final String ACTIVITY_ID_LABEL = "activityId";
    public static final String STATUS_LABEL = "status";
    public static final String TIMESTAMP_LABEL = "timestamp";
    public static final String DESCRIPTION_LABEL = "description";
    public static final String ISTRANSIENT_LABEL = "is_transient";
    
    public long executeInsert(String activityId, ActivityStatus activityStatus, Connection connection) throws SQLException;  
    public List<ActivityStatus> executeSelect(String activityId, String userId, Connection connection) throws SQLException;
    public void executeUpdate(ActivityStatus activityStatus, Connection connection) throws SQLException;
    public ListActivitiesResult executeListActivities(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, List<StatusName> statusList, List<StatusAttributeName> statusAttributeNameList, int limit, String userId,  Connection connection) throws SQLException;
    public String executeRetrieveOlderActivityId(List<StatusName> statusList, String userId, Connection connection) throws SQLException;
    public List<String> executeListActivitiesForStatus(List<StatusName> statusList, String userId, int dateValue, Connection connection) throws SQLException;
}
