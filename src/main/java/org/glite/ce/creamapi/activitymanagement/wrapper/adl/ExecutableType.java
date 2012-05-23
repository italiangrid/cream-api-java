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

package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.glite.ce.creamapi.ws.es.adl.Executable_Type;

public class ExecutableType {
    protected String path;
    protected List<String> argument;
    protected BigInteger failIfExitCodeNotEqualTo;

    public ExecutableType() {
    }
    
    public ExecutableType(Executable_Type executable_Type) {
        if (executable_Type !=null) {
            path = executable_Type.getPath();
            
            if ((executable_Type.getArgument() != null) && (executable_Type.getArgument().length > 0)) {
                argument =  getArgument();
                for (int i=0; i<executable_Type.getArgument().length; i++) {
                    argument.add(executable_Type.getArgument()[i]);
                }
            }
            failIfExitCodeNotEqualTo = executable_Type.getFailIfExitCodeNotEqualTo();
        }
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        this.path = value;
    }

    public List<String> getArgument() {
        if (argument == null) {
            argument = new ArrayList<String>();
        }
        return this.argument;
    }

    public BigInteger getFailIfExitCodeNotEqualTo() {
        return failIfExitCodeNotEqualTo;
    }

    public void setFailIfExitCodeNotEqualTo(BigInteger value) {
        this.failIfExitCodeNotEqualTo = value;
    }

}

