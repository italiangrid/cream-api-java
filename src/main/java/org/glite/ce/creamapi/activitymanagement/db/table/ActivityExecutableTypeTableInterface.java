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

public interface ActivityExecutableTypeTableInterface {
    public final static String EXECUTABLE = "EXEC";
    public final static String PRE_EXECUTABLE = "PRE";
    public final static String POST_EXECUTABLE = "POST";
    
    public static final String ACTIVITY_ID_LABEL = "activityId";
    public static final String EXECUTABLETYPE_ID_LABEL = "executable_type_id";
    public static final String TYPE_LABEL = "type";
    
    public void executeInsert(String activityId, long executableTypeId, String type, Connection connection) throws SQLException;  
    public List<Long> executeSelect(String activityId, String type, Connection connection) throws SQLException;
}
