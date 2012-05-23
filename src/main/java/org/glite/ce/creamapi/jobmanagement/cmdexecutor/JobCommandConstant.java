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

package org.glite.ce.creamapi.jobmanagement.cmdexecutor;

import java.util.ArrayList;
import java.util.List;

public class JobCommandConstant {
    public static final String JOB_MANAGEMENT = "JOB_MANAGEMENT";
    
//    public static final int JOB_REGISTER         = 0;
//    public static final int JOB_START            = 1;
//    public static final int JOB_CANCEL           = 2;
//    public static final int JOB_STATUS           = 3;
//    public static final int JOB_SUSPEND          = 4;
//    public static final int JOB_RESUME           = 5;
//    public static final int JOB_PURGE            = 6;
//    public static final int JOB_LIST             = 7;
//    public static final int JOB_INFO             = 8;
//    public static final int JOB_SET_LEASEID      = 9;
//    public static final int SET_LEASE            = 10;
//    public static final int GET_LEASE            = 11;
//    public static final int DELETE_LEASE         = 12;
//    public static final int PROXY_RENEW          = 13;
//    public static final int SET_ACCEPT_NEW_JOBS  = 14;
//    public static final int GET_SERVICE_INFO     = 15;
//    public static final int SET_JOB_STATUS       = 16;
//    public static final int QUERY_EVENT          = 17;
//    public static final int COPY_NEW_PROXY_TO_SANDBOX = 18;
//    public static final int DELETE_PROXY_FROM_SANDBOX = 19;
//    //public static final int UPDATE_PROXY_TO_SANDBOX = 19;
    
    public static final String JOB_REGISTER         = "JOB_REGISTER";   
    public static final String JOB_START            = "JOB_START";
    public static final String JOB_CANCEL           = "JOB_CANCEL";
    public static final String JOB_STATUS           = "JOB_STATUS";
    public static final String JOB_SUSPEND          = "JOB_SUSPEND";
    public static final String JOB_RESUME           = "JOB_RESUME";
    public static final String JOB_PURGE            = "JOB_PURGE";
    public static final String JOB_LIST             = "JOB_LIST";
    public static final String JOB_INFO             = "JOB_INFO";
    public static final String JOB_SET_LEASEID      = "JOB_SET_LEASEID";
    public static final String SET_LEASE            = "SET_LEASE";
    public static final String GET_LEASE            = "GET_LEASE";
    public static final String DELETE_LEASE         = "DELETE_LEASE";
    public static final String PROXY_RENEW          = "PROXY_RENEW";
    public static final String SET_ACCEPT_NEW_JOBS  = "SET_ACCEPT_NEW_JOBS";
    public static final String GET_SERVICE_INFO     = "GET_SERVICE_INFO";
    public static final String SET_JOB_STATUS       = "SET_JOB_STATUS";
    public static final String QUERY_EVENT          = "QUERY_EVENT";
    public static final String COPY_NEW_PROXY_TO_SANDBOX = "COPY_NEW_PROXY_TO_SANDBOX";
    public static final String DELETE_PROXY_FROM_SANDBOX = "DELETE_PROXY_FROM_SANDBOX";


    public static List<String> commandNameList;

    static {
        commandNameList = new ArrayList<String>(20);
        commandNameList.add(JOB_REGISTER);
        commandNameList.add(JOB_START);
        commandNameList.add(JOB_CANCEL);
        commandNameList.add(JOB_STATUS);
        commandNameList.add(JOB_SUSPEND);
        commandNameList.add(JOB_RESUME);
        commandNameList.add(JOB_PURGE);
        commandNameList.add(JOB_LIST);
        commandNameList.add(JOB_INFO);
        commandNameList.add(JOB_SET_LEASEID);
        commandNameList.add(SET_LEASE);
        commandNameList.add(GET_LEASE);
        commandNameList.add(DELETE_LEASE);
        commandNameList.add(PROXY_RENEW);
        commandNameList.add(SET_ACCEPT_NEW_JOBS);
        commandNameList.add(GET_SERVICE_INFO);
        commandNameList.add(SET_JOB_STATUS);
        commandNameList.add(QUERY_EVENT);
        commandNameList.add(COPY_NEW_PROXY_TO_SANDBOX);
        commandNameList.add(DELETE_PROXY_FROM_SANDBOX);

    }
}

