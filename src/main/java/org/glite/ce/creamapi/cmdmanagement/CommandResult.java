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

package org.glite.ce.creamapi.cmdmanagement;

import java.util.Hashtable;
import java.util.Set;

public class CommandResult {
    private Hashtable<String, Object> parameter = null;

    public CommandResult() {
        parameter = new Hashtable<String, Object>(0);
    }

    public void addParameter(String key, Object value) {
        if (key != null && value != null) {
            parameter.put(key, value);
        }
    }

    public Object getParameter(String key) {
        if (key == null) {
            return null;
        }
        return parameter.get(key);
    }

    public String getParameterAsString(String key) {
        if (key == null) {
            return null;
        }
        return (String) parameter.get(key);
    }

    public Set getParameterKeySet() {
        return parameter.keySet();
    }
}
