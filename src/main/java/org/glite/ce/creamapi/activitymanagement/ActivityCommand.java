package org.glite.ce.creamapi.activitymanagement;

import javax.xml.datatype.XMLGregorianCalendar;

public class ActivityCommand implements Comparable<ActivityCommand> {
    private long id = 0;
    private String name = null;
    private XMLGregorianCalendar timestamp = null;
    private Boolean isSuccess = null;

    public ActivityCommand() {
        this(null);
    }

    public ActivityCommand(String name) {
        this.name = name;
    }

    public int compareTo(ActivityCommand command) {

        int result = timestamp.compare(command.getTimestamp());

        return ((result == 0) ? name.compareTo(command.getName()) : result);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ActivityCommand)) {
            return false;
        }

        ActivityCommand command = (ActivityCommand) obj;

        return (name.equals(command.getName()) && timestamp.compare(command.getTimestamp()) == 0);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    public Boolean isSuccess() {
        return isSuccess;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(XMLGregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }
}
