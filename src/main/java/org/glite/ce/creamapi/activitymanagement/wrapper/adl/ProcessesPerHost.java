package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import org.glite.ce.creamapi.ws.es.adl.ProcessesPerHost_type0;

public class ProcessesPerHost {
    protected long processesPerHost = 1;
    protected boolean useSlotsPerHost = false;
    
    public ProcessesPerHost(ProcessesPerHost_type0 processesPerHost_type0) {
        if (processesPerHost_type0 != null) {
            if (processesPerHost_type0.getUnsignedLong() != null) {
                processesPerHost = processesPerHost_type0.getUnsignedLong().longValue();
            }
            useSlotsPerHost = processesPerHost_type0.getUseSlotsPerHost();
        }        
    }

    public long getProcessesPerHost() {
        return processesPerHost;
    }

    public void setProcessesPerHost(long processesPerHost) {
        this.processesPerHost = processesPerHost;
    }

    public boolean isUseSlotsPerHost() {
        return useSlotsPerHost;
    }

    public void setUseSlotsPerHost(boolean useSlotsPerHost) {
        this.useSlotsPerHost = useSlotsPerHost;
    }
}
