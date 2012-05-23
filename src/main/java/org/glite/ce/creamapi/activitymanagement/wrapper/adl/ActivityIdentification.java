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

import org.glite.ce.creamapi.ws.es.adl.ActivityIdentificationSequence_type0;

public class ActivityIdentification {
    protected String name = null;
    protected String description = null;
    protected ActivityTypeEnumeration type = null;
    protected List<String> annotation = null;

    public ActivityIdentification() {
    }
    
    public ActivityIdentification(ActivityIdentificationSequence_type0 activityIdentificationSequence_type0) {
        name = activityIdentificationSequence_type0.getName();
        description = activityIdentificationSequence_type0.getDescription();
        if (activityIdentificationSequence_type0.getType() != null) {
            type = ActivityTypeEnumeration.fromValue(activityIdentificationSequence_type0.getType().getValue());
        }
        if ((activityIdentificationSequence_type0.getAnnotation() != null) && (activityIdentificationSequence_type0.getAnnotation().length > 0)) {
            annotation = getAnnotation();
            for (int i=0; i<activityIdentificationSequence_type0.getAnnotation().length; i++) {
                annotation.add(activityIdentificationSequence_type0.getAnnotation()[i]);
            }
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public ActivityTypeEnumeration getType() {
        return type;
    }

    public void setType(ActivityTypeEnumeration value) {
        this.type = value;
    }

    public List<String> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<String>();
        }
        return this.annotation;
    }

}
                   