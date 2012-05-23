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

import java.util.List;

import org.glite.ce.creamapi.cmdmanagement.queue.CommandQueueInterface;

public interface CommandExecutorInterface {
    public void addParameter(Parameter parameter);

    public void addParameter(String key, Object value);

    public void addParameter(List<Parameter> parameters);

    public boolean checkCommandSupport(String name);

    public boolean containsParameterKey(String key);

    public void destroy() throws CommandExecutorException;

    public void execute(Command command) throws CommandExecutorException, CommandException;

    public void execute(List<Command> commandList) throws CommandExecutorException, CommandException;

    public String getCategory();

    public CommandManagerInterface getCommandManager();

    public CommandQueueInterface getCommandQueue();

    public List<String> getCommands();

    public int getCommandWorkerPoolSize();

    public String getName();

    public Parameter getParameter(String key);
    
    public List<Parameter> getParameters();

    public Object getParameterValue(String key);

    public String getParameterValueAsString(String key);

    public void initExecutor() throws CommandExecutorException;

    public void setCategory(String name);

    public void setCommandManager(CommandManagerInterface commandManager);
    
    public void setCommandQueueShared(boolean isShared);
    
    public void setCommandQueueSize(int size);
    
    public void setCommands(List<String> commandList);

    public void setCommandWorkerPoolSize(int size);

    public void setName(String name);

    public void setParameter(List<Parameter> parameters);

    public void start() throws CommandExecutorException;
}
