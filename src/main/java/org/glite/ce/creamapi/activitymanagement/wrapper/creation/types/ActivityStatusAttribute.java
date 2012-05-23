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

public enum ActivityStatusAttribute {

    VALIDATING("VALIDATING"),
    SERVER_PAUSED("SERVER-PAUSED"),
    CLIENT_PAUSED("CLIENT-PAUSED"),
    CLIENT_STAGEIN_POSSIBLE("CLIENT-STAGEIN-POSSIBLE"),
    CLIENT_STAGEOUT_POSSIBLE("CLIENT-STAGEOUT-POSSIBLE"),
    PROVISIONING("PROVISIONING"),
    DEPROVISIONING("DEPROVISIONING"),
    SERVER_STAGEIN("SERVER-STAGEIN"),
    SERVER_STAGEOUT("SERVER-STAGEOUT"),
    BATCH_SUSPEND("BATCH-SUSPEND"),
    APP_RUNNING("APP-RUNNING"),
    PREPROCESSING_CANCEL("PREPROCESSING-CANCEL"),
    PROCESSING_CANCEL("PROCESSING-CANCEL"),
    POSTPROCESSING_CANCEL("POSTPROCESSING-CANCEL"),
    VALIDATION_FAILURE("VALIDATION-FAILURE"),
    PREPROCESSING_FAILURE("PREPROCESSING-FAILURE"),
    PROCESSING_FAILURE("PROCESSING-FAILURE"),
    POSTPROCESSING_FAILURE("POSTPROCESSING-FAILURE"),
    APP_FAILURE("APP-FAILURE"),
    EXPIRED("EXPIRED");

    private final String value;

    ActivityStatusAttribute(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    public static ActivityStatusAttribute fromValue(String v) {
        for (ActivityStatusAttribute c: ActivityStatusAttribute.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

