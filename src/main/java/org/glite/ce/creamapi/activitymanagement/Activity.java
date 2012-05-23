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
 * Authors: L. Zangrando, <zangrando@pd.infn.it>
 *
 */

package org.glite.ce.creamapi.activitymanagement;

import java.util.Hashtable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.glite.ce.creamapi.activitymanagement.wrapper.adl.ActivityDescription;

public class Activity extends ActivityDescription {
    public static final String ACTIVITY_DESCRIPTION = "ACTIVITY_DESCRIPTION";
    public static final String ACTIVITY_ID = "ACTIVITY_ID";
    public static final String ACTIVITY_MANAGER_URI = "ACTIVITY_MANAGER_URI";
    public static final String ACTIVITY_STATUS = "ACTIVITY_STATUS";
    public static final String ACTIVITY_STATUS_ATTRIBUTE = "ACTIVITY_STATUS_ATTRIBUTE";
    public static final String ACTIVITY_STATUS_DESCRIPTION = "ACTIVITY_STATUS_DESCRIPTION";
    public static final String ACTIVITY_STATUS_TIMESTAMP = "ACTIVITY_STATUS_TIMESTAMP";
    public static final String ACTIVITY_WRAPPER_NOTIFICATION_STATUS_URI = "ACTIVITY_WRAPPER_NOTIFICATION_STATUS_URI";
    public static final String CE_HOSTNAME = "CE_HOSTNAME";
    public static final String CE_ID = "CE_ID";
    public static final String COPY_PROXY_MIN_RETRY_WAIT = "COPY_PROXY_MIN_RETRY_WAIT";
    public static final String COPY_RETRY_COUNT_ISB = "COPY_RETRY_COUNT_ISB";
    public static final String COPY_RETRY_COUNT_OSB = "COPY_RETRY_COUNT_OSB";
    public static final String COPY_RETRY_FIRST_WAIT_ISB = "COPY_RETRY_FIRST_WAIT_ISB";
    public static final String COPY_RETRY_FIRST_WAIT_OSB = "COPY_RETRY_FIRST_WAIT_OSB";
    public static final String DELEGATION_FILE_NAME_SUFFIX = "DELEGATION_FILE_NAME_SUFFIX";
    public static final String DELEGATION_SANDBOX_PATH = "DELEGATION_SANDBOX_PATH";
    public static final String DELEGATION_SANDBOX_URI = "DELEGATION_SANDBOX_URI";
    public static final String DELEGATION_TIME_SLOT = "DELEGATION_TIME_SLOT";
    public static final String EXIT_CODE = "EXIT_CODE";
    public static final String GSI_URL = "GSI_URL";
    public static final String LOCAL_USER = "LOCAL_USER";
    public static final String LOCAL_USER_GROUP = "LOCAL_USER_GROUP";
    public static final String LRMS_ABS_LAYER_ID = "LRMS_ABS_LAYER_ID";
    public static final String NOTIFY_MESSAGE = "NOTIFY_MESSAGE";
    public static final String SANDBOX_PATH = "SANDBOX_PATH";
    public static final String SERVICE_URL = "SERVICE_URL";
    public static final String STAGE_IN_URI = "STAGE_IN_URI";
    public static final String STAGE_OUT_URI = "STAGE_OUT_URI";
    public static final String TEMPLATE_PATH = "TEMPLATE_PATH";
    public static final String TRANSFER_INPUT = "TRANSFER_INPUT";
    public static final String TRANSFER_OUTPUT = "TRANSFER_OUTPUT";
    public static final String TRANSFER_OUTPUT_REMAPS = "TRANSFER_OUTPUT_REMAPS";
    public static final String USER_DN = "USER_DN";
    public static final String USER_FQAN = "USER_FQAN";
    public static final String VIRTUAL_ORGANISATION = "VIRTUAL_ORGANISATION";
    public static final String WORKER_NODE = "WORKER_NODE";

    private String id = null;
    private String userId = null;
    private SortedSet<ActivityStatus> states = null;
    private SortedSet<ActivityCommand> commands = null;
    private Hashtable<String, String> properties = null;
    private Hashtable<String, String> volatileProperties = null;

    public Activity() {
        this(null);
    }

    public Activity(String id) {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Hashtable<String, String> getProperties() {
        if (properties == null) {
            properties = new Hashtable<String, String>(0);
        }
        return properties;
    }

    public SortedSet<ActivityStatus> getStates() {
        if (states == null) {
            states = new TreeSet<ActivityStatus>();
        }
        return states;
    }

    public SortedSet<ActivityCommand> getCommands() {
        if (commands == null) {
            commands = new TreeSet<ActivityCommand>();
        }
        return commands;
    }

    public String getUserId() {
        return userId;
    }

    public Hashtable<String, String> getVolatileProperties() {
        if (volatileProperties == null) {
            volatileProperties = new Hashtable<String, String>(0);
        }
        return volatileProperties;
    }

    public void setCommands(List<ActivityCommand> commandList) {
        getCommands().clear();
        getCommands().addAll(commandList);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProperties(Hashtable<String, String> properties) {
        this.properties = properties;
    }

    public void setStates(List<ActivityStatus> statusList) {
        getStates().clear();
        getStates().addAll(statusList);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVolatileProperties(Hashtable<String, String> volatileProperties) {
        this.volatileProperties = volatileProperties;
    }
}
