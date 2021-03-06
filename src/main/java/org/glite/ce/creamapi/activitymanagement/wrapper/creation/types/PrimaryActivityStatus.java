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

package org.glite.ce.creamapi.activitymanagement.wrapper.creation.types;

public enum PrimaryActivityStatus {

    ACCEPTED("ACCEPTED"),
    PREPROCESSING("PREPROCESSING"),
    PROCESSING("PROCESSING"),
    PROCESSING_ACCEPTING("PROCESSING-ACCEPTING"),
    PROCESSING_QUEUED("PROCESSING-QUEUED"),
    PROCESSING_RUNNING("PROCESSING-RUNNING"),
    POSTPROCESSING("POSTPROCESSING"),
    TERMINAL("TERMINAL");
    private final String value;

    PrimaryActivityStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PrimaryActivityStatus fromValue(String v) {
        for (PrimaryActivityStatus c: PrimaryActivityStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

