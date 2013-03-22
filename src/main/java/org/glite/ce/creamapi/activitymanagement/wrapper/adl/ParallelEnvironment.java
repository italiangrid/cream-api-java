package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import java.util.ArrayList;
import java.util.List;

import org.glite.ce.creamapi.ws.es.adl.OptionType;
import org.glite.ce.creamapi.ws.es.adl.ParallelEnvironment_type0;

public class ParallelEnvironment {
    protected ProcessesPerHost processesPerHost = null;
    protected ThreadsPerProcess threadsPerProcess = null;
    protected List<Option> option = null;
    protected String type = null;
    protected String version = null;

    public ParallelEnvironment(ParallelEnvironment_type0 parallelEnvironment_type0) {
        if (parallelEnvironment_type0 != null) {
            type = parallelEnvironment_type0.getType();
            processesPerHost = new ProcessesPerHost(parallelEnvironment_type0.getProcessesPerHost());
            threadsPerProcess = new ThreadsPerProcess(parallelEnvironment_type0.getThreadsPerProcess());
            
            if (parallelEnvironment_type0.isOptionSpecified()) {
                OptionType[] options = parallelEnvironment_type0.getOption();
                
                option = new ArrayList<Option>(options.length);
                
                for (int i=0; i<options.length; i++) {
                    option.add(new Option(options[i]));
                }
            }
            
            if (parallelEnvironment_type0.isVersionSpecified()) {            
                version = parallelEnvironment_type0.getVersion();
            }
        }
    }

    public ProcessesPerHost getProcessesPerHost() {
        return processesPerHost;
    }

    public void setProcessesPerHost(ProcessesPerHost processesPerHost) {
        this.processesPerHost = processesPerHost;
    }

    public ThreadsPerProcess getThreadsPerProcess() {
        return threadsPerProcess;
    }

    public void setThreadsPerProcess(ThreadsPerProcess threadsPerProcess) {
        this.threadsPerProcess = threadsPerProcess;
    }

    public List<Option> getOption() {
        return option;
    }

    public void setOption(List<Option> option) {
        this.option = option;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
