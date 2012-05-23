package org.glite.ce.creamapi.cmdmanagement;

public abstract class PolicyTask {
    private CommandExecutorInterface executor;
    private String name;

    public PolicyTask(String name) {
        this(name, null);
    }

    public PolicyTask(String name, CommandExecutorInterface executor) {
        this.name = name;
        this.executor = executor;
    }

    public abstract void execute(Policy policy) throws PolicyException;

    public CommandExecutorInterface getCommandExecutor() {
        return executor;
    }

    public String getName() {
        return name;
    }

    public void setCommandExecutor(CommandExecutorInterface executor) {
        this.executor = executor;
    }
}
