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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.glite.ce.creamapi.ws.es.adl.Application_type0;

public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    
    protected ExecutableType executable;
    protected String input;
    protected String output;
    protected String error;
    protected List<OptionType> environment;
    protected List<ExecutableType> preExecutable;
    protected List<ExecutableType> postExecutable;
    protected List<RemoteLogging> remoteLogging;
    protected OptionalTime expirationTime;
    protected OptionalDuration wipeTime;
    protected List<Notification> notification;

    public Application() {
    }
    
    public Application(Application_type0 application_type0) {
        if (application_type0.getExecutable() != null) {
            executable = new ExecutableType(application_type0.getExecutable());
        }
        input = application_type0.getInput();
        output = application_type0.getOutput();
        error = application_type0.getError();
        if ((application_type0.getEnvironment() != null) && (application_type0.getEnvironment().length > 0)) {
            environment = getEnvironment();
            OptionType optionType = null;
            for (int i=0; i<application_type0.getEnvironment().length; i++){
                if (application_type0.getEnvironment()[i] != null) {
                    optionType = new OptionType();
                    optionType.setName(application_type0.getEnvironment()[i].getName());
                    optionType.setValue(application_type0.getEnvironment()[i].getValue());
                    environment.add(optionType); 
                }
            }
        }

        if ((application_type0.getPreExecutable() != null) && (application_type0.getPreExecutable().length > 0)) {
            preExecutable = getPreExecutable();
            for (int i=0; i<application_type0.getPreExecutable().length; i++){
                if (application_type0.getPreExecutable()[i] != null) {
                    preExecutable.add(new ExecutableType(application_type0.getPreExecutable()[i]));
                }
            }
        }
        
        if ((application_type0.getPostExecutable() != null) && (application_type0.getPostExecutable().length > 0)) {
            postExecutable = getPostExecutable();
            for (int i=0; i<application_type0.getPostExecutable().length; i++){
                if (application_type0.getPostExecutable()[i] != null) {
                    postExecutable.add(new ExecutableType(application_type0.getPostExecutable()[i]));
                }
            }
        }
            
        if ((application_type0.getRemoteLogging() != null) && (application_type0.getRemoteLogging().length > 0)) {
            remoteLogging = getRemoteLogging();
            for (int i=0; i<application_type0.getRemoteLogging().length; i++){
                if (application_type0.getRemoteLogging()[i] != null) {
                    remoteLogging.add(new RemoteLogging(application_type0.getRemoteLogging()[i]));
                }
            }
        }
        
        if (application_type0.getExpirationTime() != null) {
            expirationTime =  new OptionalTime();
            expirationTime.setOptional(application_type0.getExpirationTime().getOptional());
            expirationTime.setValue(getXMLGregorianCalendar(application_type0.getExpirationTime().getDateTime()));
        }
        
        if (application_type0.getWipeTime() != null) {
            wipeTime =  new OptionalDuration();
            wipeTime.setOptional(application_type0.getWipeTime().getOptional());
            
            if (application_type0.getWipeTime().getDateTime() != null) {
                try {
                    wipeTime.setValue(DatatypeFactory.newInstance().newDurationDayTime(application_type0.getWipeTime().getDateTime().getTimeInMillis()));
                } catch (DatatypeConfigurationException e) {
                    logger.warn("Error setting duration for wipeTime.");
                }
            }
        }
        
        if ((application_type0.getNotification() != null) && (application_type0.getNotification().length > 0)) {
            notification = getNotification();
            for (int i=0; i<application_type0.getNotification().length; i++){
                if (application_type0.getNotification()[i] != null) {
                    notification.add(new Notification(application_type0.getNotification()[i]));
                }
            }
        }
    }
    
    public ExecutableType getExecutable() {
        return executable;
    }
    
    public void setExecutable(ExecutableType value) {
        this.executable = value;
    }
    
    public String getInput() {
        return input;
    }

    public void setInput(String value) {
        this.input = value;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String value) {
        this.output = value;
    }

    public String getError() {
        return error;
    }

    public void setError(String value) {
        this.error = value;
    }

    public List<OptionType> getEnvironment() {
        if (environment == null) {
            environment = new ArrayList<OptionType>();
        }
        return this.environment;
    }

                                                                      
    public List<ExecutableType> getPreExecutable() {
        if (preExecutable == null) {
            preExecutable = new ArrayList<ExecutableType>();
        }
        return this.preExecutable;
    }

    public List<ExecutableType> getPostExecutable() {
        if (postExecutable == null) {
            postExecutable = new ArrayList<ExecutableType>();
        }
        return this.postExecutable;
    }
    
    public List<RemoteLogging> getRemoteLogging() {
        if (remoteLogging == null) {
            remoteLogging = new ArrayList<RemoteLogging>();
        }
        return this.remoteLogging;
    }

    public OptionalTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(OptionalTime value) {
        this.expirationTime = value;
    }

    public OptionalDuration getWipeTime() {
        return wipeTime;
    }
    
    public void setWipeTime(OptionalDuration value) {
        this.wipeTime = value;
    }

    public List<Notification> getNotification() {
        if (notification == null) {
            notification = new ArrayList<Notification>();
        }
        return this.notification;
    }
    
    private static XMLGregorianCalendar getXMLGregorianCalendar(Calendar calendar) {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        if (calendar != null) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTimeInMillis(calendar.getTimeInMillis());
            try {
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            } catch (DatatypeConfigurationException ex) {
              logger.warn(ex.getMessage());
            }
        }
        return xmlGregorianCalendar;
    }

}
