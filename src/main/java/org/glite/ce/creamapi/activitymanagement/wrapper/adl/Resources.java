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

import org.glite.ce.creamapi.ws.es.adl.Resources_type0;

public class Resources {
    protected String queueName;

    protected ParallelEnvironment parallelEnvironment = null;

    protected SlotRequirement slotRequirement = null;
    public Resources() {
    }
    public Resources(Resources_type0 resources_type0) {
        if (resources_type0 != null) {
            queueName = resources_type0.getQueueName();
            
            if (resources_type0.isParallelEnvironmentSpecified()) {
                parallelEnvironment = new ParallelEnvironment(resources_type0.getParallelEnvironment());                
            }
            
            if (resources_type0.isSlotRequirementSpecified()) {
                slotRequirement = new SlotRequirement(resources_type0.getSlotRequirement());
            }
        }
    }
    
    public ParallelEnvironment getParallelEnvironment() {
        return parallelEnvironment;
    }
    
    public String getQueueName() {
        return queueName;
    }
    
    public SlotRequirement getSlotRequirement() {
        return slotRequirement;
    }

    public void setParallelEnvironment(ParallelEnvironment parallelEnvironment) {
        this.parallelEnvironment = parallelEnvironment;
    }

    public void setQueueName(String value) {
        this.queueName = value;
    }

    public void setSlotRequirement(SlotRequirement slotRequirement) {
        this.slotRequirement = slotRequirement;
    }
}
