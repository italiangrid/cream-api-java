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

import org.glite.ce.creamapi.activitymanagement.wrapper.creation.types.PrimaryActivityStatus;
import org.glite.ce.creamapi.ws.es.adl.Notification_type0;

public class Notification {
    protected ProtocolTypeEnumeration protocol;
    protected List<String> recipient;
    protected List<PrimaryActivityStatus> onState;
    protected Boolean optional;

    public Notification() {
    }
    
    public Notification(Notification_type0 notification_type0) {
        if (notification_type0 != null) {
            optional = notification_type0.getOptional();    
            if (notification_type0.getProtocol() != null) {
                protocol = ProtocolTypeEnumeration.fromValue(notification_type0.getProtocol().getValue());
            }
            
            if ((notification_type0.getOnState() != null) && (notification_type0.getOnState().length > 0)) {
                onState = getOnState();
                for (int i=0; i<notification_type0.getOnState().length; i++) {
                    onState.add(PrimaryActivityStatus.fromValue(notification_type0.getOnState()[i].getValue()));
                }
            }
            
            if ((notification_type0.getRecipient() != null) && (notification_type0.getRecipient().length > 0)) {
                recipient = getRecipient();
                for (int i=0; i<notification_type0.getRecipient().length; i++) {
                    recipient.add(notification_type0.getRecipient()[i]);
                }
            }
        }
    }
    
    public ProtocolTypeEnumeration getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolTypeEnumeration value) {
        this.protocol = value;
    }

    public List<String> getRecipient() {
        if (recipient == null) {
            recipient = new ArrayList<String>();
        }
        return this.recipient;
    }

    public List<PrimaryActivityStatus> getOnState() {
        if (onState == null) {
            onState = new ArrayList<PrimaryActivityStatus>();
        }
        return this.onState;
    }

    public Boolean isOptional() {
        return optional;
    }

    public void setOptional(Boolean value) {
        this.optional = value;
    }

}

