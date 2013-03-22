package org.glite.ce.creamapi.activitymanagement.wrapper.adl;

import org.glite.ce.creamapi.ws.es.adl.OptionType;

public class Option {
    protected String name = null;
    protected String value = null;

    public Option(OptionType option) {
        if (option != null) {
            name = option.getName();
            value = option.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
