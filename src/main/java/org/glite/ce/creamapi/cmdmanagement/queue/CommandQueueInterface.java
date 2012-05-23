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

import java.util.Calendar;

import org.glite.ce.creamapi.cmdmanagement.Command;

public interface CommandQueueInterface {

    /** 
     * Closes the queue.
     */
    public void close();

    /** 
     * Dequeues the command.
     * @param the command to dequeue.
     * @throws CommandQueueException - if the command or its id is not specified or some problem occurred during the database connection.
     */
    public void dequeue(Command command) throws CommandQueueException;

    /** 
     * Destroys the queue.
     */
    //public void destroy() throws CommandQueueException;

    /** 
     * Enqueues the command.
     * @param the command to enqueue.
     * @throws CommandQueueException - if the command is not specified or not completely filled, or some problem occurred during the database connection.
     */
    public void enqueue(Command command) throws CommandQueueException;

    public void evalutateThroughput();
    
    public long getCurrentInThroughput();

    public long getCurrentOutThroughput();

    public Calendar getLastThroughputUpdate();

    public long getMaxInThroughput();

    public long getMaxOutThroughput();

    /** 
     * Returns the queue name.
     */
    public String getName();

    /** 
     * Returns the queue size.
     * @return the queue size.
     * @throws CommandQueueException - if some problem occurred during the database connection.
     */
    public int getSize() throws CommandQueueException;

    /** 
     * Returns the queue size.
     */
    public boolean isOpen();
    
    public boolean isShared();

    /** 
     * Opens the queue.
     */
    public void open();
        

    /** 
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty.
     * @throws CommandQueueException - if this queue closed
     */
    public Command peek() throws CommandQueueException;
    
    /** 
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty
     * @throws CommandQueueException - if this queue closed
     */
    public Command poll() throws CommandQueueException;
    
    /** 
     * Retrieves and removes the head of this queue. This method differs from poll only in that it throws an exception if this queue is empty. 
     * @return the head of this queue 
     * @throws CommandQueueException - if this queue is empty or closed
     */
    public Command remove() throws CommandQueueException;

    public void setShared(boolean isShared);

    //public void setSize(int size) throws CommandQueueException;
        
    public void setTimeout(long mtTimeout);

    /** 
     * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available. 
     * @return the head of this queue.
     * @throws CommandQueueException - if this queue closed or if interrupted while waiting
     */
    public Command take() throws CommandQueueException;

}
