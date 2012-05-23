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

import org.glite.ce.creamapi.ws.es.adl.DataStaging_type0;

public class DataStaging {
    protected Boolean clientDataPush;
    protected List<InputFile> inputFile;
    protected List<OutputFile> outputFile;

    public DataStaging() {
    }
    
    public DataStaging(DataStaging_type0 dataStaging_type_0) {
        if (dataStaging_type_0 != null) {
            clientDataPush = dataStaging_type_0.getClientDataPush();

            if ((dataStaging_type_0.getInputFile() != null) && (dataStaging_type_0.getInputFile().length > 0)) {
                inputFile = getInputFile();
                for (int i=0; i<dataStaging_type_0.getInputFile().length; i++) {
                    inputFile.add(new InputFile(dataStaging_type_0.getInputFile()[i]));
                }
            }
            
            if ((dataStaging_type_0.getOutputFile() != null) && (dataStaging_type_0.getOutputFile().length > 0)) {
                outputFile = getOutputFile();
                for (int i=0; i<dataStaging_type_0.getOutputFile().length; i++) {
                    outputFile.add(new OutputFile(dataStaging_type_0.getOutputFile()[i]));
                }
            }
        }
    }

    public Boolean isClientDataPush() {
        return clientDataPush;
    }

    public void setClientDataPush(Boolean value) {
        this.clientDataPush = value;
    }

    public List<InputFile> getInputFile() {
        if (inputFile == null) {
            inputFile = new ArrayList<InputFile>();
        }
        return this.inputFile;
    }

    public List<OutputFile> getOutputFile() {
        if (outputFile == null) {
            outputFile = new ArrayList<OutputFile>();
        }
        return this.outputFile;
    }

}

