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

package org.glite.ce.creamapi.eventmanagement;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public class EventManagerFactory {
	
	private static final Logger logger = Logger.getLogger(EventManagerFactory.class.getName());
	private static Hashtable<String, EventManagerInterface> eventManagers = new Hashtable<String, EventManagerInterface>(0);
	
	public static EventManagerInterface getEventManager(String type) throws IllegalArgumentException {
		if ((type == null) || "".equals(type)){
			throw new IllegalArgumentException("Parameter type must be not empty or null!");
		}
		return eventManagers.get(type);
	}

   public static void addEventManager(String type, EventManagerInterface eventManager) throws IllegalArgumentException {
		if ((type == null) || "".equals(type)){
			throw new IllegalArgumentException("Parameter type must be not empty or null!");
		}
	    if (eventManager == null){
	    	throw new IllegalArgumentException("Parameter eventManager must be not null");
	    }
	    if (eventManagers.containsKey(type)) {
	    	logger.error("EventManager for type= " + type + " already exists!");
	    	throw new IllegalArgumentException("EventManager for type= " + type + " already exists!");
	    }
	    eventManagers.put(type, eventManager);
	    logger.info("Added eventManager for type= " + type);
   }
   
}
