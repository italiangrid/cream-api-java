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

import org.glite.ce.creamapi.ws.es.adl.ActivityDescriptionSequence_type1;

public class ActivityDescription {
    protected ActivityIdentification activityIdentification;
    protected Application application;
    protected Resources resources;
    protected DataStaging dataStaging;

    public ActivityDescription() {
    }
    
    public ActivityDescription(ActivityDescriptionSequence_type1 activityDescriptionSequence_type1) {
        if (activityDescriptionSequence_type1 != null) {
            if ((activityDescriptionSequence_type1.getActivityIdentification() != null) &&
                    (activityDescriptionSequence_type1.getActivityIdentification().getActivityIdentificationSequence_type0() != null)){
                activityIdentification = new ActivityIdentification(activityDescriptionSequence_type1.getActivityIdentification().getActivityIdentificationSequence_type0());
            }
            if (activityDescriptionSequence_type1.getApplication() != null) {
                application = new Application(activityDescriptionSequence_type1.getApplication());
            }
            
            if (activityDescriptionSequence_type1.getResources() != null) {
                resources = new Resources(activityDescriptionSequence_type1.getResources());
            }
            if (activityDescriptionSequence_type1.getDataStaging() != null) {
                dataStaging = new DataStaging(activityDescriptionSequence_type1.getDataStaging());
            }
        }
    }
    
    public ActivityIdentification getActivityIdentification() {
        return activityIdentification;
    }

    public void setActivityIdentification(ActivityIdentification value) {
        this.activityIdentification = value;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application value) {
        this.application = value;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources value) {
        this.resources = value;
    }

    public DataStaging getDataStaging() {
        return dataStaging;
    }

    public void setDataStaging(DataStaging value) {
        this.dataStaging = value;
    }

}

