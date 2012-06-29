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

package org.glite.ce.creamapi.cmdmanagement.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.db.DatabaseException;
import org.glite.ce.commonj.db.DatasourceManager;
import org.glite.ce.creamapi.cmdmanagement.Command;
import org.glite.ce.creamapi.cmdmanagement.Command.ExecutionModeValues;

/**
 * Class managing a command queue in the database.
 */
public class CommandQueue implements CommandQueueInterface {
    
    private class FillUpQueueThread extends Thread {
        
        public FillUpQueueThread() {
            super("FillUpQueueThread");
            setDaemon(true);
        }

        public void run() {
            logger.info("[queue=" + QUEUE_TABLE + "]: started!");
            TreeSet<Command> commandList = new TreeSet<Command>();
            TreeSet<Command> commandToBeEnqueued1L = new TreeSet<Command>();
            TreeSet<Command> commandToBeEnqueued2L = new TreeSet<Command>();

            while (isOpen) {
                try {
                    if (queue.size() <= maxQueueSize * 0.1 && (commandToBeEnqueued2L.size() > 0 || getSize() > 0L)) {
                        commandList.clear();
                        commandList.addAll(commandToBeEnqueued2L);

                        commandToBeEnqueued2L.clear();

                        for (int priorityLevel = Command.HIGH_PRIORITY; priorityLevel >= Command.LOW_PRIORITY &&
                                (maxQueueSize > queue.size() + commandToBeEnqueued1L.size() + commandToBeEnqueued2L.size()); priorityLevel--) {
                            commandList.addAll(executeGetCommands(false, priorityLevel, maxQueueSize - queue.size() - commandToBeEnqueued1L.size() - commandToBeEnqueued2L.size()));

                            for (Command command : commandList) {
                                if (excludedCommandGroupIdSet.contains(command.getCommandGroupId())) {
                                    commandToBeEnqueued2L.add(command);
                                    continue;
                                }

                                if (command.getExecutionMode().equals(Command.ExecutionModeValues.SERIAL)) {
                                    excludedCommandGroupIdSet.add(command.getCommandGroupId());
                                }

                                commandToBeEnqueued1L.add(command);

                                command.setStatus(Command.SCHEDULED);

                                if (logger.isDebugEnabled()) {
                                    logger.debug("status change for command [" + command.toString() + "]");
                                }
                            }

                            commandList.clear();
                        }

                        queue.addAll(commandToBeEnqueued1L);
                        synchronized (queueLock) {
                            queueLock.notifyAll();
                            logger.debug("NotifyAll (queueLock): items available.");    
                        }
                        
                        logger.debug("[queue=" + QUEUE_TABLE + "]: queue size = " + queue.size());

                        commandToBeEnqueued1L.clear();
                    }
                } catch (CommandQueueException ex) {
                    logger.error("[queue=" + QUEUE_TABLE + "]: " + ex.getMessage());
                }

                try {
                    long msTimeout = 0L;

                    if (isShared) {
                        msTimeout = getSize() == 0L && commandToBeEnqueued2L.size() == 0 ? emptyQueueTimeout : notEmptyQueueTimeout;

                        logger.info("[queue=" + QUEUE_TABLE + "]: waiting " + msTimeout + " msec...");
                        
                        synchronized (lock) {
                            lock.wait(msTimeout);
                        }
                    } else {
                        msTimeout = queue.size() == 0 && getSize() == 0L && commandToBeEnqueued2L.size() == 0 ? 0L : notEmptyQueueTimeout;

                        if (msTimeout == 0L) {
                            logger.info("[queue=" + QUEUE_TABLE + "]: waiting indefinitely...");
                            
                            synchronized (lock) {
                                lock.wait(msTimeout);
                            }
                        } else {
                            logger.info("[queue=" + QUEUE_TABLE + "]: waiting " + msTimeout + " msec...");
                            try {
                                Thread.sleep(msTimeout);
                            } catch (InterruptedException e) {
                               logger.error("[queue=" + QUEUE_TABLE + "] error: " + e.getMessage());
                            }       
                        }
                    }
                } catch (Throwable e) {
                    logger.error("[queue=" + QUEUE_TABLE + "] error: " + e.getMessage());
                }       
            }

            logger.info("[queue=" + QUEUE_TABLE + "]: end");
        }
    }

    /** The logger */
    private static final Logger logger = Logger.getLogger(CommandQueue.class);
    
    /**  The table columns */
    private static final String ID_FIELD = "id";
    private static final String COMMAND_ID_FIELD = "commandId";
    private static final String COMMAND_GROUP_ID_FIELD = "commandGroupId";
    private static final String IS_SCHEDULED_FIELD = "isScheduled";
    private static final String PRIORITY_LEVEL_FIELD = "priorityLevel";
    private static final String EXECUTION_MODE_FIELD = "executionMode";
    private static final String NAME_FIELD = "name";
    private static final String VALUE_FIELD = "value";
    private static final String CATEGORY_FIELD = "category";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String STATUS_TYPE_FIELD = "statusType";
    private static final String FAILURE_REASON_FIELD = "failureReason";
    private static final String USER_ID_FIELD = "userId";
    private static final String CREATION_TIME_FIELD = "creationTime";
    
    /** The table name */
    private String QUEUE_TABLE = "command_queue";
    private String PARAMETER_TABLE = "command_queue_parameter";

    /** The datasource name */
    private String dataSourceName = null;

    private final AtomicLong maxInThroughput = new AtomicLong(0);
    private final AtomicLong maxOutThroughput = new AtomicLong(0);
    private final AtomicLong currentInThroughput = new AtomicLong(0);
    private final AtomicLong currentOutThroughput = new AtomicLong(0);
    private final AtomicLong commandInCounter = new AtomicLong(0);
    private final AtomicLong commandOutCounter = new AtomicLong(0);
    private final Set<String> excludedCommandGroupIdSet = new ConcurrentSkipListSet<String>();
    private final Object lock = new Object();
    private final Object queueLock = new Object();
    private final long emptyQueueTimeout = 5000L;
    private long notEmptyQueueTimeout = 1000L;
    private Calendar lastThroughputUpdate = null;
    private BlockingQueue<Command> queue;
    private FillUpQueueThread fillUpQueueThread = null;
    private boolean isOpen = false;
    private boolean isShared = false;
    private int maxQueueSize = 500;

    /**
     * Constructor.
     * 
     * @param queueName
     *            The name of the queue, which corresponds to the same one of
     *            the associated table.
     * @throws CommandQueueException
     */
    public CommandQueue(String dataSourceName) throws CommandQueueException {
        this(dataSourceName, 500);
    }

    /**
     * Constructor.
     * 
     * @param queueName
     *            The name of the queue, which corresponds to the same one of
     *            the associated table.
     * @param queueSize
     *            The queue size.
     * @throws CommandQueueException
     */
    public CommandQueue(String dataSourceName, int queueSize) throws CommandQueueException {
        if (dataSourceName == null) {
            logger.error("dataSourceName not defined!");
            throw new CommandQueueException("dataSourceName not specified!");
        }

        this.dataSourceName = dataSourceName;
        lastThroughputUpdate = Calendar.getInstance();

        // Create the queue table in the db if not present
        createQueueTable();
        
        if (queueSize <= 0) {
            logger.warn("queueSize <= 0, using default value (500)");
            maxQueueSize = 500;
        } else {
            maxQueueSize = queueSize;
        }

        queue = new LinkedBlockingQueue<Command>(maxQueueSize); 
    }

    private boolean checkIfTableExists(String tableName, Connection connection) throws CommandQueueException {
        if (tableName == null) {
            throw new CommandQueueException("tableName not specified!");
        }

        if (connection == null) {
            throw new CommandQueueException("connection not specified!");
        } 
        
        logger.debug("BEGIN checkIfTableExists");

        boolean result = false;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            pstmt = connection.prepareStatement("select count(*) from " + tableName);

            rset = pstmt.executeQuery();

            if (rset != null) {
                result = true;
            }

        } catch (SQLException sqle) {
            result = false;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1.getMessage());
                }
            }

            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException sqle2) {
                    logger.error(sqle2.getMessage());
                }
            }
        }

        logger.debug("END checkIfTableExists");
        return result;
    }

    /** 
     * Closes the queue.
     */
    public void close() {
        isOpen = false;
        logger.warn("the queue has been closed");
        synchronized (queueLock) {
            queueLock.notifyAll();
            logger.debug("NotifyAll for queueLock because the queue has been closed!");
        }
        
        synchronized (lock) {
            lock.notifyAll();
        }

        fillUpQueueThread = null;

        logger.info("the queue " + QUEUE_TABLE + " is now closed!");
    }

    /**
     * Creates the queue table.
     * 
     * @throws CommandQueueException
     */
    private void createQueueTable() throws CommandQueueException {
        logger.debug("BEGIN createQueueTable: " + QUEUE_TABLE);

        PreparedStatement pstmt = null;
        Connection connection = null;

        try {
            connection = getConnection();

            StringBuffer query = null;
            boolean exists = checkIfTableExists(QUEUE_TABLE, connection);
            
            if (!exists) {
                query = new StringBuffer("create table if not exists ");
                query.append(QUEUE_TABLE).append(" (");
                query.append(ID_FIELD).append(" BIGINT NOT NULL AUTO_INCREMENT, ");
                query.append(NAME_FIELD).append(" VARCHAR(256) NOT NULL, ");
                query.append(CATEGORY_FIELD).append(" VARCHAR(256) NULL, ");
                query.append(USER_ID_FIELD).append(" TEXT NOT NULL, ");
                query.append(DESCRIPTION_FIELD).append(" TEXT NULL, ");
                query.append(FAILURE_REASON_FIELD).append(" TEXT NULL, ");
                query.append(STATUS_TYPE_FIELD).append(" INTEGER NOT NULL, ");
                query.append(CREATION_TIME_FIELD).append(" TIMESTAMP NULL DEFAULT now(), ");
                query.append(IS_SCHEDULED_FIELD).append(" BOOL NOT NULL, ");
                query.append(PRIORITY_LEVEL_FIELD).append(" TINYINT UNSIGNED NOT NULL DEFAULT 0, ");
                query.append(COMMAND_GROUP_ID_FIELD).append(" VARCHAR(14) NULL, ");
                query.append(EXECUTION_MODE_FIELD).append(" CHAR(1) NOT NULL, primary key (");
                query.append(ID_FIELD).append(")) engine=InnoDB");

                pstmt = connection.prepareStatement(query.toString());
                pstmt.executeUpdate();
                
                logger.info(QUEUE_TABLE + " table created");
            }
            
            if (!checkIfTableExists(PARAMETER_TABLE, connection)) {            
                query = new StringBuffer("create table if not exists ");
                query.append(PARAMETER_TABLE).append(" (");
                query.append(ID_FIELD).append(" BIGINT NOT NULL AUTO_INCREMENT, ");
                query.append(COMMAND_ID_FIELD).append(" BIGINT NOT NULL, ");
                query.append(NAME_FIELD).append(" VARCHAR(256) NOT NULL, ");
                query.append(VALUE_FIELD).append(" TEXT NOT NULL, primary key ( ");
                query.append(ID_FIELD).append(")) engine=InnoDB");

                pstmt = connection.prepareStatement(query.toString());
                pstmt.executeUpdate();
                
                logger.info(PARAMETER_TABLE + " table created");

                // Foreign Key
                query = new StringBuffer("ALTER TABLE ");
                query.append(PARAMETER_TABLE).append(" ADD CONSTRAINT fk_").append(PARAMETER_TABLE);
                query.append(" FOREIGN KEY (").append(COMMAND_ID_FIELD).append(") REFERENCES ");
                query.append(QUEUE_TABLE).append(" (id) ON UPDATE CASCADE ON DELETE CASCADE");

                pstmt = connection.prepareStatement(query.toString());
                pstmt.executeUpdate();
            } 
            
            if (exists) {
                logger.info("The queue is already existing, now I try to recover it...");

                query = new StringBuffer("update ");
                query.append(QUEUE_TABLE).append(" set ");
                query.append(STATUS_TYPE_FIELD).append(" = ?, ");
                query.append(DESCRIPTION_FIELD).append(" = ?, ");
                query.append(IS_SCHEDULED_FIELD).append(" = ?, ");
                query.append(PRIORITY_LEVEL_FIELD).append(" = ? where ");
                query.append(IS_SCHEDULED_FIELD).append(" = true");

                pstmt = connection.prepareStatement(query.toString());
                pstmt.setInt(1, Command.RESCHEDULED);
                pstmt.setString(2, "command rescheduled by CREAM");
                pstmt.setBoolean(3, false);
                pstmt.setLong(4, Command.HIGH_PRIORITY);

                pstmt.executeUpdate();
                
                logger.info("queue recovered successfully!");
            }

            // Commit
            connection.commit();
        } catch (SQLException sqle) {
            String rollbackMessage = null;

            if (connection != null) {
                try {
                    connection.rollback();
                    rollbackMessage = " (rollback performed)";                
                } catch (SQLException sqle1) {
                    rollbackMessage = " (rollback failed: " + sqle1.getMessage() + ")";
                }
            }
            
            logger.error("createQueueTable failed: cannot create the " + QUEUE_TABLE + " table: " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
            throw new CommandQueueException("Cannot create the " + QUEUE_TABLE + " table: " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle2) {
                    logger.error("Problem in closing connection: " + sqle2.getMessage());
                    throw new CommandQueueException(sqle2.getMessage());
                }
            }
        }
        logger.debug("END createQueueTable: " + QUEUE_TABLE);
    }

    /**
     * Deletes a given queue name.
     * 
     * @param queueName
     *            The queue name to be deleted.
     * @throws CommandQueueException
     * @throws IllegalArgumentException
     */
    private void deleteQueueTable() throws CommandQueueException {
        logger.debug("BEGIN deleteQueueTable " + QUEUE_TABLE);

        Connection connection = getConnection();
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement("drop table if exists " + PARAMETER_TABLE);
            pstmt.executeUpdate();

            logger.debug(PARAMETER_TABLE + " table dropped!");

            pstmt = connection.prepareStatement("drop table if exists " + QUEUE_TABLE);
            pstmt.executeUpdate();

            logger.debug(QUEUE_TABLE + " table dropped!");

            // Commit
            connection.commit();
        } catch (SQLException sqle) {
            String rollbackMessage = null;

            if (connection != null) {
                try {
                    connection.rollback();
                    rollbackMessage = " (rollback performed)";                
                } catch (SQLException sqle1) {
                    rollbackMessage = " (rollback failed: " + sqle1.getMessage() + ")";
                }
            }
            
            logger.error("createQueueTable failed: cannot create the " + QUEUE_TABLE + " table: " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
            throw new CommandQueueException("Cannot delete the " + QUEUE_TABLE + " table: " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle2) {
                    logger.error("Problem in closing connection: " + sqle2.getMessage());
                    throw new CommandQueueException(sqle2.getMessage());
                }
            }
        }

        logger.debug("END deleteQueueTable " + QUEUE_TABLE);
    }

    /** 
     * Dequeues the command.
     * @param the command to dequeue.
     * @throws CommandQueueException - if the command or its id is not specified or some problem occurred during the database connection.
     */
    public void dequeue(Command command) throws CommandQueueException {
        logger.debug("BEGIN dequeue");

        if (command == null) {
            throw new CommandQueueException("command not specified!");
        }

        if (command.getId() == 0L) {
            throw new CommandQueueException("command id not specified!");
        }

        Connection connection = getConnection();
        PreparedStatement pstmt = null;

        StringBuffer query = new StringBuffer("delete from ");
        query.append(QUEUE_TABLE).append(" where ");
        query.append(ID_FIELD).append(" = ?");

        try {
            pstmt = connection.prepareStatement(query.toString());
            pstmt.setLong(1, command.getId());

            int rowCount = pstmt.executeUpdate();
            logger.debug("deleted " + rowCount + " rows");

            // Commit
            connection.commit();

            if (ExecutionModeValues.SERIAL.equals(command.getExecutionMode())) {
                excludedCommandGroupIdSet.remove(command.getCommandGroupId());
            }
        } catch (SQLException sqle) {
            String rollbackMessage = null;

            if (connection != null) {
                try {
                    connection.rollback();
                    rollbackMessage = " (rollback performed)";                
                } catch (SQLException sqle1) {
                    rollbackMessage = " (rollback failed: " + sqle1.getMessage() + ")";
                }
            }
            
            logger.error("dequeue failed: cannot dequeue the command id=" + command.getId() + ": " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
            throw new CommandQueueException("Cannot dequeue the command id=" + command.getId() + ": " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle2) {
                    logger.error("Problem in closing connection: " + sqle2.getMessage());
                    throw new CommandQueueException(sqle2.getMessage());
                }
            }
        }
        logger.debug("END dequeue");
    }

    /** 
     * Enqueues the command.
     * @param the command to enqueue.
     * @throws CommandQueueException - if the command is not specified or not completely filled, or some problem occurred during the database connection.
     */
    public void enqueue(Command command) throws CommandQueueException {
        if (command == null) {
            throw new CommandQueueException("command not specified!");
        }
        if (command.getPriorityLevel() < Command.LOW_PRIORITY || command.getPriorityLevel() > Command.HIGH_PRIORITY) {
            throw new CommandQueueException("invalid priority level=" + command.getPriorityLevel());
        }
        if (command.getExecutionMode() == Command.ExecutionModeValues.SERIAL && (command.getCommandGroupId() == null || ("".equals(command.getCommandGroupId())))) {
            logger.error("CommandGroupId field mustn't be empty for serial commands.");
            throw new CommandQueueException("The command has a null CommandGroupId");
        }
        if (command.getUserId() == null) {
            throw new CommandQueueException("The command has a null userId");
        }
        if (command.getCreationTime() == null) {
            throw new CommandQueueException("The command has a null creation time");
        }

        logger.debug("BEGIN enqueue");
        
        StringBuffer query = new StringBuffer("insert into ");
        query.append(QUEUE_TABLE).append(" (");
        query.append(NAME_FIELD).append(", ");
        query.append(CATEGORY_FIELD).append(", ");
        query.append(DESCRIPTION_FIELD).append(", ");
        query.append(FAILURE_REASON_FIELD).append(", ");
        query.append(COMMAND_GROUP_ID_FIELD).append(", ");
        query.append(USER_ID_FIELD).append(", ");
        query.append(STATUS_TYPE_FIELD).append(", ");
        query.append(PRIORITY_LEVEL_FIELD).append(", ");
        query.append(IS_SCHEDULED_FIELD).append(", ");
        query.append(EXECUTION_MODE_FIELD).append(", ");
        query.append(CREATION_TIME_FIELD);
        query.append(") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        long commandId = -1;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        Connection connection = getConnection();

        try {
            // Get the id from the sequence named sequenceName
            pstmt = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, command.getName());
            pstmt.setString(2, command.getCategory());
            pstmt.setString(3, command.getDescription());
            pstmt.setString(4, command.getFailureReason());
            pstmt.setString(5, command.getCommandGroupId());
            pstmt.setString(6, command.getUserId());
            pstmt.setInt(7, command.getStatus());
            pstmt.setInt(8, command.getPriorityLevel());
            pstmt.setBoolean(9, command.isScheduled());
            pstmt.setString(10, command.getExecutionMode().getStringValue());
            pstmt.setTimestamp(11, new Timestamp(command.getCreationTime().getTimeInMillis()));

            //logger.debug("insert statetement = " + pstmt.toString());

            int rowCount = pstmt.executeUpdate();

            // Get autogenerated keys
            rset = pstmt.getGeneratedKeys();

            if (rset != null && rset.next()) {
                commandId = rset.getLong(1);
                command.setId(commandId);
            } else {
                throw new SQLException("Problem in retrieving autogenerated keys");
            }

            if (commandId <= -1) {
                throw new SQLException("Problem in retrieving autogenerated keys");
            }

            if (command.getParameterKeySet().size() > 0) {
                query = new StringBuffer("insert into ");
                query.append(PARAMETER_TABLE);
                query.append(" (").append(COMMAND_ID_FIELD).append(", ");
                query.append(NAME_FIELD).append(", ");
                query.append(VALUE_FIELD);
                query.append(") values(?, ?, ?)");

                pstmt = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
                rowCount = 0;

                for (String key : command.getParameterKeySet()) {
                    List<String> valueList = command.getParameterMultivalue(key);

                    for (String value : valueList) {
                        pstmt.setLong(1, command.getId());
                        pstmt.setString(2, key);
                        pstmt.setString(3, value);

                        rowCount += pstmt.executeUpdate();

                        // Get autogenerated keys
                        rset = pstmt.getGeneratedKeys();

                        if (!rset.next()) {
                            throw new SQLException("Problem in retrieving autogenerated keys");
                        }

                        pstmt.clearParameters();
                        //System.out.println("Inserted parameter [name=" + key + ", value=" + value + "]");
                        logger.debug("Inserted parameter [name=" + key + ", value=" + value + "]");
                    }
                }
            }

            connection.commit();

            synchronized (lastThroughputUpdate) {
                commandInCounter.incrementAndGet();
            }

            synchronized (lock) {
                lock.notifyAll();
            }
        } catch (SQLException sqle) {
            String rollbackMessage = null;

            if (connection != null) {
                try {
                    connection.rollback();
                    rollbackMessage = " (rollback performed)";                
                } catch (SQLException sqle1) {
                    rollbackMessage = " (rollback failed: " + sqle1.getMessage() + ")";
                }
            }

            logger.error("enqueue failed: cannot enqueue the command id=" + command.getId() + ": " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
            throw new CommandQueueException("Cannot enqueue the command id=" + command.getId() + ": " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    logger.error(sqle.getMessage());
                }
            }

            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException sqle) {
                    logger.error(sqle.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle2) {
                    logger.error("Problem in closing connection: " + sqle2.getMessage());
                    throw new CommandQueueException(sqle2.getMessage());
                }
            }
        }

        logger.debug("END enqueue");
    }

    private List<Command> executeGetCommands(boolean scheduled, int priorityLevel, int limit) throws CommandQueueException {
        logger.debug("BEGIN executeGetCommands: scheduled=" + scheduled + "; priorityLevel=" + priorityLevel + "; limit=" + limit + "; excludedCommandGroupId.size=" + excludedCommandGroupIdSet.size());

        StringBuffer query = new StringBuffer("select ");
        query.append(QUEUE_TABLE).append(".").append(ID_FIELD).append(" as ").append(ID_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(NAME_FIELD).append(" as ").append(NAME_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(CATEGORY_FIELD).append(" as ").append(CATEGORY_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(USER_ID_FIELD).append(" as ").append(USER_ID_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(DESCRIPTION_FIELD).append(" as ").append(DESCRIPTION_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(FAILURE_REASON_FIELD).append(" as ").append(FAILURE_REASON_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(STATUS_TYPE_FIELD).append(" as ").append(STATUS_TYPE_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(COMMAND_GROUP_ID_FIELD).append(" as ").append(COMMAND_GROUP_ID_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(CREATION_TIME_FIELD).append(" as ").append(CREATION_TIME_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(IS_SCHEDULED_FIELD).append(" as ").append(IS_SCHEDULED_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(PRIORITY_LEVEL_FIELD).append(" as ").append(PRIORITY_LEVEL_FIELD).append(", ");
        query.append(QUEUE_TABLE).append(".").append(EXECUTION_MODE_FIELD).append(" as ").append(EXECUTION_MODE_FIELD);
        query.append(" from ").append(QUEUE_TABLE).append(" where ");
        query.append(QUEUE_TABLE).append(".").append(IS_SCHEDULED_FIELD).append(" = ?");

        if (priorityLevel >= 0) {
            query.append(" and ").append(QUEUE_TABLE).append(".").append(PRIORITY_LEVEL_FIELD).append(" = ?");
        }

        if (isShared) {
            query.append(" and ").append(COMMAND_GROUP_ID_FIELD).append(" NOT IN (select ");
            query.append(COMMAND_GROUP_ID_FIELD).append(" from ");
            query.append(QUEUE_TABLE).append(" where ");
            query.append(IS_SCHEDULED_FIELD).append(" = true and ");
            query.append(EXECUTION_MODE_FIELD).append(" = '");
            query.append(Command.EXECUTION_MODE_SERIAL).append("')");
        } else if (excludedCommandGroupIdSet.size() > 0) {
            query.append(" and ").append(COMMAND_GROUP_ID_FIELD).append(" NOT IN (");

            synchronized (excludedCommandGroupIdSet) {
                for (String cmdGroupId : excludedCommandGroupIdSet) {
                    query.append("'").append(cmdGroupId).append("',");
                }
            }

            query.replace(query.length() - 1, query.length(), ")");
        } 

        query.append(" order by ").append(ID_FIELD).append(" ASC");
        
        if (limit >= 1) {
            query.append(" limit ?");
        }

        query.append(" for update");            
                
        SortedMap<String, Command> commandList = new TreeMap<String, Command>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String parameterName = null, parameterValue = null;

        Connection connection = getConnection();

        try {
            int index = 1;
            
            pstmt = connection.prepareStatement(query.toString());
            pstmt.setBoolean(index++, scheduled);

            if (priorityLevel >= 0) {
                pstmt.setInt(index++, priorityLevel);
            }

            if (limit >= 1) {
                pstmt.setInt(index, limit);
            }

            rset = pstmt.executeQuery();

            Command command = null;
            Calendar calendar = null;
            Timestamp timestamp = null;

            if (rset != null) {                
                query = new StringBuffer(" IN (");

                while (rset.next()) {
                    command = new Command(rset.getString(NAME_FIELD), rset.getString(CATEGORY_FIELD));
                    command.setId(rset.getLong(ID_FIELD));
                    command.setUserId(rset.getString(USER_ID_FIELD));
                    command.setDescription(rset.getString(DESCRIPTION_FIELD));
                    command.setFailureReason(rset.getString(FAILURE_REASON_FIELD));
                    command.setCommandGroupId(rset.getString(COMMAND_GROUP_ID_FIELD));
                    command.setAsynchronous(true);
                    command.setPriorityLevel(rset.getInt(PRIORITY_LEVEL_FIELD));
                    command.setExecutionMode(ExecutionModeValues.PARALLEL.equals(rset.getString(EXECUTION_MODE_FIELD)) ? ExecutionModeValues.PARALLEL: ExecutionModeValues.SERIAL);

                    calendar = null;
                    timestamp = rset.getTimestamp(CREATION_TIME_FIELD);
                    if (timestamp != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp.getTime());
                        command.setCreationTime(calendar);
                    }

                    commandList.put(""+command.getId(), command);

                    query.append("'").append(command.getId()).append("',");
                }

                query.replace(query.length() - 1, query.length(), ")");

                if (commandList.size() > 0) {
                    StringBuffer setScheduledQuery = new StringBuffer("update ");
                    setScheduledQuery.append(QUEUE_TABLE).append(" set ").append(IS_SCHEDULED_FIELD);
                    setScheduledQuery.append(" = true where ").append(ID_FIELD).append(query);

                    pstmt = connection.prepareStatement(setScheduledQuery.toString());
                    pstmt.executeUpdate();

                    StringBuffer selectParameterQuery = new StringBuffer("select ");
                    selectParameterQuery.append(PARAMETER_TABLE).append(".").append(ID_FIELD).append(" as PARAMETER_ID, ");
                    selectParameterQuery.append(PARAMETER_TABLE).append(".").append(COMMAND_ID_FIELD).append(" as ").append(COMMAND_ID_FIELD).append(", ");
                    selectParameterQuery.append(PARAMETER_TABLE).append(".").append(NAME_FIELD).append(" as PARAMETER_NAME, ");
                    selectParameterQuery.append(PARAMETER_TABLE).append(".").append(VALUE_FIELD).append(" as PARAMETER_VALUE from ");
                    selectParameterQuery.append(PARAMETER_TABLE).append(" where ").append(COMMAND_ID_FIELD).append(query);

                    pstmt = connection.prepareStatement(selectParameterQuery.toString());
                    rset = pstmt.executeQuery();

                    if (rset != null) {
                        long commandId = -1;

                        while (rset.next()) {
                            commandId = rset.getLong(COMMAND_ID_FIELD);

                            command = commandList.get(""+commandId);

                            if (commandId > 0) {
                                parameterName = rset.getString("PARAMETER_NAME");
                                parameterValue = rset.getString("PARAMETER_VALUE");

                                if (command.containsParameterKey(parameterName)) {
                                    List<String> valueList = command.getParameterMultivalue(parameterName);
                                    valueList.add(parameterValue);

                                    command.addParameter(parameterName, valueList);
                                } else {
                                    command.addParameter(parameterName, parameterValue);
                                }
                            }
                        }
                    }
                }
            }

            // Commit
            connection.commit();
        } catch (SQLException sqle) {
            String rollbackMessage = null;

            if (connection != null) {
                try {
                    connection.rollback();
                    rollbackMessage = " (rollback performed)";                
                } catch (SQLException sqle1) {
                    rollbackMessage = " (rollback failed: " + sqle1.getMessage() + ")";
                }
            }

            logger.error("executeGetCommands failed: " + sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
            throw new CommandQueueException(sqle.getMessage() + (rollbackMessage != null? rollbackMessage : ""));
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle1) {
                    logger.error(sqle1.getMessage());
                }
            }

            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException sqle2) {
                    logger.error(sqle2.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle2) {
                    logger.error("Problem in closing connection: " + sqle2.getMessage());
                    throw new CommandQueueException(sqle2.getMessage());
                }
            }
        }

        logger.debug("END executeGetCommands: found #" + commandList.size() + " commands");
        return new ArrayList<Command>(commandList.values());
    }

    public void evalutateThroughput() {
        Calendar now = Calendar.getInstance();
        long maxInThroughputTmp = 0L;
        long maxOutThroughputTmp = 0L;
        long currentInThroughputTmp = 0L;
        long currentOutThroughputTmp = 0L;
        long elapsedTimeInMillis = now.getTimeInMillis() - lastThroughputUpdate.getTimeInMillis();

        logger.debug("[queue=" + QUEUE_TABLE + "]: evaluating the throughput...");

        synchronized (lastThroughputUpdate) {
            lastThroughputUpdate.setTime(now.getTime());

            maxInThroughputTmp = maxInThroughput.get();

            if (commandInCounter.get() > 0) {
                currentInThroughputTmp = (commandInCounter.get() * 60000 / elapsedTimeInMillis);
            }

            currentInThroughput.set(currentInThroughputTmp);

            if (maxInThroughputTmp < currentInThroughputTmp) {
                maxInThroughputTmp = currentInThroughputTmp;
                maxInThroughput.set(maxInThroughputTmp);
            }

            maxOutThroughputTmp = maxOutThroughput.get();

            if (commandOutCounter.get() > 0) {
                currentOutThroughputTmp = (commandOutCounter.get() * 60000 / elapsedTimeInMillis);
            }

            currentOutThroughput.set(currentOutThroughputTmp);
                        
            if (maxOutThroughputTmp < currentOutThroughputTmp) {
                maxOutThroughputTmp = currentOutThroughputTmp;
                maxOutThroughput.set(maxOutThroughputTmp);
            }

            commandInCounter.set(0);
            commandOutCounter.set(0);
        }

        //logger.info("throughput evaluated [queue = " + QUEUE_TABLE + "; currentInThroughput = " + currentInThroughputTmp + " cmd/min; maxInThroughput = " + maxInThroughputTmp + " cmd/min; currentOutThroughput = " + currentOutThroughputTmp + " cmd/min; maxOutThroughput = " + maxOutThroughputTmp + " cmd/min]");
        logger.debug("[queue=" + QUEUE_TABLE + "]: evaluating the throughput... done!");
    } 

    /**
     * Returns the connection to the database.
     * 
     * @return The connection to the database.
     * @throws DatabaseException
     */
    private Connection getConnection() throws CommandQueueException {
        Connection connection = null;
        try {
            connection = DatasourceManager.getConnection(dataSourceName);
        } catch (DatabaseException ex) {
            throw new CommandQueueException(ex.getMessage());
        }

        if (connection == null) {
            logger.error("cannot get the database connection");
            throw new CommandQueueException("cannot get the database connection");
        }

        return connection;
    }

    public long getCurrentInThroughput() {
        return currentInThroughput.get();
    }   

    public long getCurrentOutThroughput() {
        return currentOutThroughput.get();
    }   
    
    public Calendar getLastThroughputUpdate() {
        return lastThroughputUpdate;
    }   

    public long getMaxInThroughput() {
        return maxInThroughput.get();
    }

    public long getMaxOutThroughput() {
        return maxOutThroughput.get();
    }

    /**
     * @see org.glite.ce.creamapi.cmdmanagement.queue.CommandQueueInterface#getName()
     */
    public String getName() {
        return QUEUE_TABLE;
    }


    /** 
     * Returns the queue size.
     * @return the queue size.
     * @throws CommandQueueException - if some problem occurred during the database connection.
     */
    public int getSize() throws CommandQueueException {
        logger.debug("BEGIN getSize");

        Connection connection = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int queueSize = 0;

        try {
            // Get the scheduled command list
            pstmt = connection.prepareStatement("select count(1) from " + QUEUE_TABLE + " where isScheduled=false");
            rset = pstmt.executeQuery();

            if (rset != null) {
                rset.next();

                if (rset.getRow() > 0) {
                    queueSize = rset.getInt(1);
                } else {
                    logger.debug("No command in queue");
                }
            }

            // Commit
            connection.commit();
            logger.debug("queue size is " + queueSize);
        } catch (SQLException sqle) {
            throw new CommandQueueException(sqle.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    logger.error(sqle.getMessage());
                }
            }

            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException sqle) {
                    logger.error(sqle.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqle2) {
                    logger.error("Problem in closing connection: " + sqle2.getMessage());
                    throw new CommandQueueException(sqle2.getMessage());
                }
            }
        }
        logger.debug("END getSize");

        return queueSize;
    }

    public boolean isOpen() {
        return isOpen;
    }
   
    public boolean isShared() {
        return isShared;
    }
    
    /** 
     * Opens the queue.
     */
    public void open() {
        isOpen = true;
        fillUpQueueThread = new FillUpQueueThread();
        fillUpQueueThread.start();
        logger.info("the queue " + QUEUE_TABLE + " is now opened!");
    }
    
    /** 
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty.
     * @throws CommandQueueException - if this queue closed
     */
    public Command peek() throws CommandQueueException {
        if (!isOpen) {
            throw new CommandQueueException("the queue " + QUEUE_TABLE + " is closed!");
        }

        if (queue.size() == 0) {
            return null;
        }
        
        Command command = null;
        
        try {
            command =  queue.peek();

            synchronized (lastThroughputUpdate) {
                commandOutCounter.incrementAndGet();
            }
        } catch (Throwable t) {
            throw new CommandQueueException(t.getMessage());
        }
        
        return command;
    }

    /** 
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty
     * @throws CommandQueueException - if this queue closed
     */
    public Command poll() throws CommandQueueException {
        if (!isOpen) {
            throw new CommandQueueException("the queue " + QUEUE_TABLE + " is closed!");
        }
        
        if (queue.size() == 0) {
            return null;
        }
        
        Command command = null;
        
        try {
            command =  queue.poll();
            dequeue(command);

            synchronized (lastThroughputUpdate) {
                commandOutCounter.incrementAndGet();
            }
        } catch (Throwable t) {
            throw new CommandQueueException(t.getMessage());
        }
        
        return command;
    }

    /** 
     * Retrieves and removes the head of this queue. This method differs from poll only in that it throws an exception if this queue is empty. 
     * @return the head of this queue 
     * @throws CommandQueueException - if this queue is empty or closed
     */
    public Command remove() throws CommandQueueException {
        if (!isOpen) {
            throw new CommandQueueException("the queue " + QUEUE_TABLE + " is closed!");
        }
        
        if (queue.size() == 0) {
            throw new CommandQueueException("the queue " + QUEUE_TABLE + " is empty!");
        }
        
        Command command = null;
        
        try {
            command =  queue.remove();
            dequeue(command);
            
            synchronized (lastThroughputUpdate) {
                commandOutCounter.incrementAndGet();
            }
        } catch (Throwable t) {
            throw new CommandQueueException(t.getMessage());
        }
        
        return command;
    }
    
    public void setShared(boolean isShared) {
        this.isShared = isShared;
    }

    public void setTimeout(long msTimeout) {
        notEmptyQueueTimeout = msTimeout < 0L ? 60000 : msTimeout;
    }

    /** 
     * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available. 
     * @return the head of this queue.
     * @throws CommandQueueException - if this queue closed or if interrupted while waiting
     */
    public Command take() throws CommandQueueException {
        Command command = null;
        
        try {
            synchronized (queueLock) {
                while ((queue.size() == 0) && (isOpen)) {
                    logger.debug("Waiting for items because queue.size=0.");
                    queueLock.wait();
                }
                logger.debug("The waiting is terminated. queue.size = " + queue.size() + " isOpen = "+ isOpen);
                if (isOpen) {
                    logger.debug("Before taking one item.");
                    command =  queue.take();
                    logger.debug("Take item: done");
                    dequeue(command);
                }
            }
            logger.debug("synchronized queueLock: done.");
            synchronized (lastThroughputUpdate) {
                commandOutCounter.incrementAndGet();
            }
        } catch (Throwable t) {
            throw new CommandQueueException(t.getMessage());
        }
        logger.debug("command: " + command);
        return command;
    }
}
