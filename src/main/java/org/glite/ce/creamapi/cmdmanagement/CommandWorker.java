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
 * Authors: Moreno Marzolla
 *
 */

package org.glite.ce.creamapi.cmdmanagement;

import org.apache.log4j.Logger;
import org.glite.ce.creamapi.cmdmanagement.queue.CommandQueueException;

/**
 * This class implements a WorkerThread inside the CommandManager. WorkerThreads
 * are used to execute tasks from the CommandManager in parallel. The
 * behavior of a worker thread is very simple: it fetches a command from the
 * journal manager and executes it. Accesses to the journal manager are (of
 * course) done in mutual exclusion. We need to guarantee that commands related
 * to the same job are executed in the exact same order as they appear in the
 * journal. To do so, we use the <code>scoreboard</code> variable, which is the
 * set of ID of jobs which are currently being executed by the worker threads.
 * During the fetchCommand() method, the journal is checked one entry at a time.
 * If the entry refers to a command related to a job on which another command
 * appears in the scoreboard, that entry is skipped.
 */
final class CommandWorker extends Thread {
    private final static Logger logger = Logger.getLogger(CommandWorker.class.getName());

    private CommandExecutorInterface executor = null;
    private boolean isProcessing = false;
    private boolean exit = false;

    public CommandWorker(CommandExecutorInterface executor, int id) {
        super("WorkerThread_" + executor.getName() + "_" + id);
        setDaemon(true); // This is actually a workaround to allow clean

        this.setName("WorkerThread_" + executor.getName() + "_" + id);
        this.executor = executor;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void terminate() {
        logger.info("terminate invoked for " + getName());
        exit = true;
    }

    public void run() {
        Command command = null;

        do {
            logger.debug(getName() + " waiting for a task");
            try {
                command = executor.getCommandQueue().take();
            } catch (CommandQueueException e) {
                if (e.getMessage().equals("queue closed!")) {
                    exit = true;
                } else {
                    logger.debug(getName() + " error: " + e.getMessage(), e);
                }
                command = null;
            }

            if (command != null && !exit) {
                try {
                    isProcessing = true;
                    command.setStatus(Command.EXECUTING);

                    if (logger.isDebugEnabled()) {
                        logger.debug("status change for command [" + command.toString() + "]");
                    }

                    executor.execute(command);

                    command.setStatus(Command.EXECUTED_OK);

                    if (logger.isDebugEnabled()) {
                        logger.debug("status change for command [" + command.toString() + "]");
                    }

                } catch (Throwable e) {
                    logger.error(e.getMessage());

                    command.setFailureReason(e.getMessage());
                    command.setStatus(Command.EXECUTED_ERROR);
                    logger.info("status change for command [" + command.toString() + "]");
                }

                isProcessing = false;
            }
        } while (!exit && !isInterrupted());

        logger.info(getName() + " terminated!");
    }
}
