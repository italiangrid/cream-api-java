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

import org.glite.ce.creamapi.activitymanagement.Activity;
import org.glite.ce.creamapi.activitymanagement.db.ActivityDB;

public interface ActivityTableInterface {
    
    public static final String ACTIVITY_ID_LABEL = "activityId";
    public static final String USERID_LABEL = "userId";
    public static final String NAME_LABEL = "name";
    public static final String DESCRIPTION_LABEL = "description";
    public static final String TYPE_LABEL = "type";
    public static final String INPUT_LABEL = "input";
    public static final String OUTPUT_LABEL = "output";
    public static final String ERROR_LABEL = "error";
    public static final String EXPIRATIONTIME_DATE_LABEL = "expiration_time_date";
    public static final String EXPIRATIONTIME_OPTIONAL_LABEL = "expiration_time_optional";
    public static final String WIPETIME_DURATION_LABEL = "wipe_time_duration";
    public static final String WIPETIME_OPTIONAL_LABEL = "wipe_time_optional";
    public static final String CLIENT_DATA_PUSH_LABEL = "client_data_push";
    public static final String QUEUE_NAME_LABEL = "queue_name";
    
    public static enum UserEnabledForActivity {YES, NO, NO_FOR_ACTIVITY_NOT_FOUND};
    
    public String executeInsert(Activity activity, String userId, Connection connection) throws SQLException;
    public ActivityDB executeSelect(String activityId, String userId, Connection connection) throws SQLException;  
    public void executeDelete(String activityId, String userId, Connection connection) throws SQLException;
    
    public UserEnabledForActivity isUserEnabled(String activityId, String userId, Connection connection);

}
