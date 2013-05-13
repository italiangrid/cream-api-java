package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import org.glite.ce.creamapi.ws.es.adl.ThreadsPerProcess_type0;

public class ThreadsPerProcess {
    protected long threadsPerProcess = 1;
    protected boolean useSlotsPerHost = false;
    
    
    public ThreadsPerProcess(ThreadsPerProcess_type0 threadsPerProcess_type0) {
        if (threadsPerProcess_type0 != null) {
            if (threadsPerProcess_type0.getUnsignedLong() != null) {
                threadsPerProcess = threadsPerProcess_type0.getUnsignedLong().longValue();
            }
            useSlotsPerHost = threadsPerProcess_type0.getUseSlotsPerHost();
        }        
    }

    public long getThreadsPerProcess() {
        return threadsPerProcess;
    }


    public boolean isUseSlotsPerHost() {
        return useSlotsPerHost;
    }


    public void setThreadsPerProcess(long threadsPerProcess) {
        this.threadsPerProcess = threadsPerProcess;
    }


    public void setUseSlotsPerHost(boolean useSlotsPerHost) {
        this.useSlotsPerHost = useSlotsPerHost;
    }
}
