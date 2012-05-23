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

import org.glite.ce.creamapi.activitymanagement.wrapper.adl.OutputFile;

public interface OutputFileTableInterface {
    
    public static final String ID_LABEL = "id";
    public static final String ACTIVITY_ID_LABEL = "activityId";
    public static final String NAME_LABEL = "name";
    public static final String URI_LABEL = "uri";
    public static final String DELEGATION_ID_LABEL = "delegation_id";
    public static final String MANDATORY_LABEL = "mandatory";    
    public static final String CREATION_FLAG_LABEL = "creation_flag";
    public static final String USE_IF_FAILURE_LABEL = "use_if_failure";
    public static final String USE_IF_CANCEL_LABEL = "use_if_cancel";
    public static final String USE_IF_SUCCESS_LABEL = "use_if_success";
    public static final String OPTIONTYPE_OUTPUTFILE_NAME_LABEL = "name";
    public static final String OPTIONTYPE_OUTPUTFILE_VALUE_LABEL = "value";
    
    public void executeInsert(String activityId, OutputFile outputFile, Connection connection) throws SQLException;  
    public List<OutputFile> executeSelect(String activityId, Connection connection) throws SQLException;
}
