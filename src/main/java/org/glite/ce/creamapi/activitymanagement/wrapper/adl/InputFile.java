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

import org.glite.ce.creamapi.ws.es.adl.InputFile_type0;
import org.glite.ce.creamapi.ws.es.adl.Source_type0;

public class InputFile {
    protected String name;
    protected List<Source> source;
    protected Boolean isExecutable;

    public InputFile() {
    }

    public InputFile(InputFile_type0 inputFile_type0) {
        if (inputFile_type0 != null) {
            name = inputFile_type0.getName();
            isExecutable = inputFile_type0.getIsExecutable();
            Source_type0[] sourceType0Array = inputFile_type0.getSource();
            if ((sourceType0Array != null) && (sourceType0Array.length > 0)) {
                source = getSource();
                Source sourceObj = null;
                OptionType optionType = null;
                for (int i=0; i<sourceType0Array.length; i++) {
                    sourceObj = new Source();  
                    sourceObj.setDelegationID(sourceType0Array[i].getDelegationID());
                    if (sourceType0Array[i].getURI() != null) {
                        sourceObj.setURI(sourceType0Array[i].getURI().toString());
                    }
                    if ((sourceType0Array[i].getOption() != null) && (sourceType0Array[i].getOption().length > 0)){
                        for (int j=0; j<sourceType0Array[i].getOption().length; j++) {
                            optionType = new OptionType();
                            optionType.setName(sourceType0Array[i].getOption()[j].getName());
                            optionType.setValue(sourceType0Array[i].getOption()[j].getValue());
                            sourceObj.getOption().add(optionType);
                        }
                    }
                    source.add(sourceObj);
                }
            }
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public List<Source> getSource() {
        if (source == null) {
            source = new ArrayList<Source>();
        }
        return this.source;
    }

    public Boolean isIsExecutable() {
        return isExecutable;
    }

    public void setIsExecutable(Boolean value) {
        this.isExecutable = value;
    }

}
