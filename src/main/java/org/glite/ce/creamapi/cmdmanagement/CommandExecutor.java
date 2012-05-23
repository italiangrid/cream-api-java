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

package org.glite.ce.creamapi.cmdmanagement;

import org.glite.ce.creamapi.cmdmanagement.queue.CommandQueueInterface;


public interface CommandExecutor {
    public void addParameter(String key, Object value);    
    public boolean checkCommandSupport(String name);
    public boolean containsParameterKey(String key);
    public void destroy();
    public void execute(Command cmd) throws CommandExecutorException, CommandException, IllegalArgumentException;
    public void execute(Command[] cmd) throws CommandExecutorException, CommandException, IllegalArgumentException;
    public String getCategory();
    public CommandManagerInterface getCommandManager();  
    public CommandQueueInterface getCommandQueue();
    public String[] getCommands();       
    public String getName();
    public Object getParameterValue(String key);
    public String getParameterValueAsString(String key);  
    public void initExecutor() throws CommandException;  
    public void setCategory(String name);      
    public void setCommandManager(CommandManagerInterface cmdManager);  
    public void setCommandQueue(CommandQueueInterface queue);
    public void setCommands(String[] cmd);
    public void setCommandWorkerPoolSize(int size);
    public void setName(String name);
}
