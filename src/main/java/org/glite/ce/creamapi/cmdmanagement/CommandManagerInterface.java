package org.glite.ce.creamapi.cmdmanagement;

import java.util.Calendar;
import java.util.List;

public interface CommandManagerInterface {
    public void addCommandExecutor(CommandExecutorInterface cmdExec) throws CommandManagerException;

    public void addCommandExecutor(List<CommandExecutorInterface> cmdExecList) throws CommandManagerException;

    public boolean checkCommandExecutor(String name, String category);

    public void execute(Command cmd) throws CommandException, CommandManagerException;

    public void execute(List<Command> cmdList) throws CommandException, CommandManagerException;

    public CommandExecutorInterface getCommandExecutor(String name, String category) throws CommandManagerException;

    public List<CommandExecutorInterface> getCommandExecutors();

    public long getCurrentThroughput();

    public Calendar getLastThroughputUpdate();

    public long getMaxThroughput();

    public void init() throws CommandManagerException;

    public void removeCommandExecutor(CommandExecutorInterface cmdExec) throws CommandManagerException;

    public void removeCommandExecutor(String name, String category) throws CommandManagerException;

    public void terminate();
}
