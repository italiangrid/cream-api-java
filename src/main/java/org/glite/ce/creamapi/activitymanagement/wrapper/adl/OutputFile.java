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

import java.util.ArrayList;
import java.util.List;

import org.glite.ce.creamapi.ws.es.adl.OutputFile_type0;

public class OutputFile {
    protected String name;
    protected List<Target> target;

    public OutputFile() {
    }
    
    public OutputFile(OutputFile_type0 outputFile_type0) {
        if (outputFile_type0 != null) {
            name = outputFile_type0.getName();  
            if ((outputFile_type0.getTarget() != null) && (outputFile_type0.getTarget().length > 0)) {
                target = getTarget();
                for (int i=0; i<outputFile_type0.getTarget().length; i++) {
                    target.add(new Target(outputFile_type0.getTarget()[i]));
                }
            }
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public List<Target> getTarget() {
        if (target == null) {
            target = new ArrayList<Target>();
        }
        return this.target;
    }

}                                                    
