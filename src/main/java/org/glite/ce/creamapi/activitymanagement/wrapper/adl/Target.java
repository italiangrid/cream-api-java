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

import org.glite.ce.creamapi.ws.es.adl.Target_type0;

public class Target {
    protected String uri;
    protected String delegationID;
    protected List<OptionType> option;
    protected Boolean mandatory;
    protected CreationFlagEnumeration creationFlag;
    protected Boolean useIfFailure;
    protected Boolean useIfCancel;
    protected Boolean useIfSuccess;

    public Target() {
    }

    public Target(Target_type0 target) {
        if ((target != null)) {
            delegationID = target.getDelegationID();
            if ((target.getOption() != null) && (target.getOption().length > 0)) {
                option = getOption();
                OptionType optionType = null;
                for (int i=0; i<target.getOption().length; i++) {
                    optionType = new OptionType();
                    optionType.setName(target.getOption()[i].getName());
                    optionType.setValue(target.getOption()[i].getValue());
                    option.add(optionType);
                }
            }
            mandatory = (target.isMandatorySpecified() ? target.getMandatory() : false);

            if (target.getURI() != null) {
                uri = target.getURI().toString();
            }
            useIfCancel = (target.isUseIfCancelSpecified() ? target.getUseIfCancel() : false);
            useIfFailure = (target.isUseIfFailureSpecified() ? target.getUseIfFailure() : false);
            useIfSuccess = (target.isUseIfSuccessSpecified() ? target.getUseIfSuccess() : true);
            creationFlag = (target.isCreationFlagSpecified() ? CreationFlagEnumeration.fromValue(target.getCreationFlag().getValue()) : CreationFlagEnumeration.OVERWRITE);
        }
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }

    public String getDelegationID() {
        return delegationID;
    }

    public void setDelegationID(String value) {
        this.delegationID = value;
    }

    public List<OptionType> getOption() {
        if (option == null) {
            option = new ArrayList<OptionType>();
        }
        return this.option;
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean value) {
        this.mandatory = value;
    }

    public CreationFlagEnumeration getCreationFlag() {
        return creationFlag;
    }

    public void setCreationFlag(CreationFlagEnumeration value) {
        this.creationFlag = value;
    }

    public Boolean isUseIfFailure() {
        return useIfFailure;
    }

    public void setUseIfFailure(Boolean value) {
        this.useIfFailure = value;
    }

    public Boolean isUseIfCancel() {
        return useIfCancel;
    }

    public void setUseIfCancel(Boolean value) {
        this.useIfCancel = value;
    }

    public Boolean isUseIfSuccess() {
        return useIfSuccess;
    }

    public void setUseIfSuccess(Boolean value) {
        this.useIfSuccess = value;
    }
}
