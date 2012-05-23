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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.glite.ce.creamapi.cmdmanagement.queue.CommandQueue;
import org.glite.ce.creamapi.cmdmanagement.queue.CommandQueueException;
import org.glite.ce.creamapi.cmdmanagement.queue.CommandQueueInterface;

public abstract class AbstractCommandExecutor implements CommandExecutorInterface {
    private static final Logger logger = Logger.getLogger(AbstractCommandExecutor.class.getName());

    private String name;
    private String category;
    private List<String> commands;
    private List<CommandWorker> threadPool = null;
    private CommandManagerInterface commandManager;
    private CommandQueueInterface commandQueue;
    private int commandWorkerPoolSize = 5;
    private int commandQueueSize = 500;
    private boolean commandQueueShared = false;
    private Hashtable<String, Object> parameter;
    protected ThreadGroup poolGroup = new ThreadGroup("Manager Worker Threads");
    protected String dataSourceName = null;

    protected AbstractCommandExecutor(String name, String category) throws CommandExecutorException {
        this(name, category, null);
    }

    protected AbstractCommandExecutor(String name, String category, List<String> commands) throws CommandExecutorException {
        if (name == null || name.length() == 0) {
            logger.error("command executor's name not specified!");            
        }

        if (category == null || category.length() == 0) {
            logger.error("command executor's category not specified!");            
        }
                
        this.name = name;        
        this.category = category;
        this.commands = commands;
        parameter = new Hashtable<String, Object>(0);         
    }

    public void addParameter(Parameter parameter) {
        if (parameter != null) {
            addParameter(parameter.getName(), parameter.getValue());
        }
    }

    public void addParameter(List<Parameter> parameters) {
        if (parameters == null) {
            return;
        }

        for (Parameter parameter : parameters) {
            addParameter(parameter);
        }
    }

    public void addParameter(String key, Object value) {
        if (key != null && value != null) {
            parameter.put(key, value);
        }
    }

    public boolean checkCommandSupport(String name) {
        if (name == null || commands == null) {
            return false;
        }

        for (String cmdName : commands) {
            if (cmdName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsParameterKey(String key) {
        if (key == null) {
            return false;
        }
        return parameter.containsKey(key);
    }

    public void destroy() {
        logger.info("destroying the " + name + " executor...");

        if (commandQueue != null) {
            commandQueue.close();

            do {
                for (int i = 0; i < threadPool.size(); i++) {
                    if (!threadPool.get(i).isProcessing() && !threadPool.get(i).isInterrupted()) {
                        threadPool.get(i).terminate();
                        threadPool.remove(i);
                    }
                }
            } while (threadPool.size() > 0);

            threadPool.clear();
        }
        
        threadPool = null;
        commandQueue = null;
        commandManager = null;
        logger.info("executor " + name + " destroyed!");
    }

    public abstract void execute(Command command) throws CommandExecutorException, CommandException;

    public abstract void execute(List<Command> commandList) throws CommandExecutorException, CommandException;

    public String getCategory() {
        return category;
    }

    public CommandManagerInterface getCommandManager() {
        return commandManager;
    }

    public CommandQueueInterface getCommandQueue() {
        return commandQueue;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getCommandWorkerPoolSize() {
        return commandWorkerPoolSize;
    }

    public String getName() {
        return name;
    }

    public Parameter getParameter(String key) {
        if (key != null) {
            return new Parameter(key, parameter.get(key));
        }

        return null;
    }

    public Set<String> getParameterKeySet() {
        return parameter.keySet();
    }
    
    public List<Parameter> getParameters() {
        List<Parameter> list = new ArrayList<Parameter>(parameter.size());

        for (String key : parameter.keySet()) {
            list.add(new Parameter(key, parameter.get(key)));
        }

        return list;
    }
    public Object getParameterValue(String key) {
        if (key != null) {
            return parameter.get(key);
        }

        return null;
    }

    public String getParameterValueAsString(String key) {
        if (key != null) {
            return (String) parameter.get(key);
        }

        return null;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setCommandManager(CommandManagerInterface cmdManager) {
        this.commandManager = cmdManager;
    }
    
    public void setCommands(List<String> cmd) {
        commands = cmd;
    }

    public void setCommandQueueShared(boolean isShared) {
        commandQueueShared = isShared;
    }
    
    public void setCommandQueueSize(int size) {
        if (size <= 0) {
            logger.warn("queueSize <= 0, using the default value (" + commandQueueSize + ")");
        } else {
            commandQueueSize = size;
        }
    }
    
    public void setCommandWorkerPoolSize(int size) {
        if (size < 0) {
            logger.warn("commandWorkerPoolSize <= 0, using the default value (" + commandWorkerPoolSize + ")");
        } else if (size == 0) {
            commandWorkerPoolSize = size;
            logger.info("commandWorkerPoolSize = 0, queue disabled!");
        } else {
            commandWorkerPoolSize = size;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameter(List<Parameter> parameters) {
        if (parameter == null) {
            return;
        }
        parameter.clear();

        for (Parameter parameter : parameters) {
            addParameter(parameter);
        }
    }

    public void start() throws CommandExecutorException {
        logger.info("starting the " + name + " commandExecutor");

        if(commandWorkerPoolSize > 0) {        
            try {
                commandQueue = new CommandQueue(dataSourceName, commandQueueSize);
                commandQueue.setShared(commandQueueShared);
                commandQueue.open();
            } catch (CommandQueueException e) {
                logger.error("cannot create the queue for the " + name + " commandExecutor: " + e.getMessage());
                throw new CommandExecutorException(e.getMessage()); 
            }
            
            // Initialize the thread pool
            threadPool = new ArrayList<CommandWorker>(commandWorkerPoolSize);
            CommandWorker cw = null;
            for (int i = 0; i < commandWorkerPoolSize; i++) {
                cw = new CommandWorker(this, i);
                cw.start();

                threadPool.add(cw);
            }            
        }

        logger.info(name + " commandExecutor started successfully");
    }
}
