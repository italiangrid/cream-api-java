package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import org.glite.ce.creamapi.ws.es.adl.SlotsPerHost_type0;

/*
 * The SlotsPerHost specifies the total number of slots to allocate on the batch system.
 * The term Slot is used to denote a logical CPU visible to and allocable by the resource management system.
 * It may correspond to a physical CPU, a physical CPU core or a virtual CPU or core, depending on the hardware capabilities.
 */
public class SlotsPerHost {
    protected long numberOfSlotsPerHost = 1;
    protected boolean useNumberOfSlots = false;

    public SlotsPerHost(SlotsPerHost_type0 slotsPerHost_type0) {
        if (slotsPerHost_type0 != null) {
            numberOfSlotsPerHost = slotsPerHost_type0.getUnsignedLong().longValue();
            useNumberOfSlots = slotsPerHost_type0.getUseNumberOfSlots();
        }
    }

    public long getNumberOfSlotsPerHost() {
        return numberOfSlotsPerHost;
    }

    public void setNumberOfSlotsPerHost(long numberOfSlotsPerHost) {
        this.numberOfSlotsPerHost = numberOfSlotsPerHost;
    }

    public boolean isUseNumberOfSlots() {
        return useNumberOfSlots;
    }

    public void setUseNumberOfSlots(boolean useSlotsPerHost) {
        this.useNumberOfSlots = useSlotsPerHost;
    }
}
