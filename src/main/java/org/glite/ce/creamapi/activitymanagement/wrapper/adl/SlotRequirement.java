package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import org.glite.ce.creamapi.ws.es.adl.SlotRequirement_type0;

public class SlotRequirement {
    protected long numberOfSlots = 1;
    protected boolean exclusiveExecution = false;
    protected SlotsPerHost slotsPerHost = null;

    public SlotRequirement(SlotRequirement_type0 slotRequirement) {
        if (slotRequirement != null) {
            numberOfSlots = slotRequirement.getNumberOfSlots().longValue();
            
            if (slotRequirement.isExclusiveExecutionSpecified()) {
                exclusiveExecution = slotRequirement.getExclusiveExecution();
            }            
            
            if (slotRequirement.isSlotsPerHostSpecified()) {
                slotsPerHost = new SlotsPerHost(slotRequirement.getSlotsPerHost());
            }
        }
    }

    public long getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(long numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public boolean isExclusiveExecution() {
        return exclusiveExecution;
    }

    public void setExclusiveExecution(boolean exclusiveExecution) {
        this.exclusiveExecution = exclusiveExecution;
    }

    public SlotsPerHost getSlotsPerHost() {
        return slotsPerHost;
    }

    public void setSlotsPerHost(SlotsPerHost slotsPerHost) {
        this.slotsPerHost = slotsPerHost;
    }
}
