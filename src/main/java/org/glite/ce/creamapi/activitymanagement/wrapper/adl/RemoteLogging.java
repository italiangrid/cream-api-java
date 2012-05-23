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

import org.glite.ce.creamapi.ws.es.adl.RemoteLogging_type0;

public class RemoteLogging {
    protected String serviceType;
    protected String url;
    protected Boolean optional;

    
    public RemoteLogging() {
    }
    
    public RemoteLogging(RemoteLogging_type0 remoteLogging_type0) {
        if (remoteLogging_type0 != null) {
            optional = remoteLogging_type0.getOptional();
            serviceType = remoteLogging_type0.getServiceType();
            if (remoteLogging_type0.getURL() != null) {
                url = remoteLogging_type0.getURL().toString();
            } else {
                url = null;
            }
        }
    }
    
    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */

    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    
    public void setServiceType(String value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    
    public String getURL() {
        return url;
      }

      /**
       * Sets the value of the url property.
       * 
       * @param value
       *     allowed object is
       *     {@link String }
       *     
       */
    
    public void setURL(String value) {
          this.url = value;
      }

      /**
       * Gets the value of the optional property.
       * 
       * @return
       *     possible object is
       *     {@link Boolean }
       *     
       */
    
    public boolean isOptional() {
          if (optional == null) {
              return false;
          } else {
              return optional;
          }
      }

      /**
       * Sets the value of the optional property.
       * 
       * @param value
       *     allowed object is
       *     {@link Boolean }
       *     
       */
    
    public void setOptional(Boolean value) {
          this.optional = value;
      }

  }
