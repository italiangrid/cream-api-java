package org.glite.ce.creamapi.cmdmanagement;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Policy {
    
    public static enum TIME_UNIT {
        SECOND(Calendar.SECOND), MINUTE(Calendar.MINUTE), HOUR(Calendar.HOUR), DAY(Calendar.DAY_OF_YEAR), MONTH(Calendar.MONTH), YEAR(Calendar.YEAR), DATE(Calendar.DATE);
        int field;

        TIME_UNIT(int field) {
            this.field = field;
        }

        public int getTimeUnit() {
            return field;
        }
    }

    private String name = null;
    private String type = null;
    private String value = null;
    private Calendar date = null;
    private TIME_UNIT timeUnit = null;
    private int timeValue = 0;
    

    public Policy(String name, String type, String value, Calendar date) throws IllegalArgumentException { 
        if (name == null) {
            throw new IllegalArgumentException("name not specified!");
        }
        if (type == null) {
            throw new IllegalArgumentException("type not specified!");
        }
        if (value == null) {
            throw new IllegalArgumentException("value not specified!");
        }
        if (date == null) {
            throw new IllegalArgumentException("date not specified!");
        }

        this.name = name;
        this.type = type;
        this.value = value;
        this.date = date;
        timeUnit = TIME_UNIT.DATE;
    }

    public Policy(String name, String type, String value, int timeValue, TIME_UNIT timeUnit) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("name not specified!");
        }
        if (type == null) {
            throw new IllegalArgumentException("type not specified!");
        }
        if (value == null) {
            throw new IllegalArgumentException("value not specified!");
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException("timeField not specified!");
        }

        this.name = name;
        this.type = type;
        this.value = value;
        this.timeValue = Math.abs(timeValue);
        this.timeUnit = timeUnit;
        

        date = new GregorianCalendar();
        date.roll(timeUnit.getTimeUnit(), timeValue);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Policy)) {
            return false;
        }

        Policy policy = (Policy) obj;
        return name.equals(policy.name) && value.equals(policy.value) && type.equals(policy.type) && date.equals(policy.date);
    }

    public Calendar getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public TIME_UNIT getTimeUnit() {
        return timeUnit;
    }

    public int getTimeValue() {
        return timeValue;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
