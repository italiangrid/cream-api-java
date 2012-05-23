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

import org.glite.ce.creamapi.activitymanagement.wrapper.adl.InputFile;

public interface InputFileTableInterface {
    
    public static final String ID_LABEL = "id";
    public static final String ACTIVITY_ID_LABEL = "activityId";
    public static final String NAME_LABEL = "name";
    public static final String ISEXECUTABLE_LABEL = "is_executable";
    public static final String URI_LABEL = "uri";
    public static final String DELEGATION_ID_LABEL = "delegation_id";
    public static final String OPTIONTYPE_INPUTFILE_NAME_LABEL = "name";
    public static final String OPTIONTYPE_INPUTFILE_VALUE_LABEL = "value";
    
    public void executeInsert(String activityId, InputFile inputFile, Connection connection) throws SQLException;  
    public List<InputFile> executeSelect(String activityId, Connection connection) throws SQLException;
}
