package org.glite.ce.creamapi.activitymanagement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public class ActivityStatus implements Comparable<ActivityStatus> {
    public static enum StatusAttributeName {
        APP_FAILURE("app-failure"), APP_RUNNING("app-running"), BATCH_SUSPEND("batch-suspend"), CLIENT_PAUSED("client-paused"), CLIENT_STAGEIN_POSSIBLE("client-stagein-possible"), CLIENT_STAGEOUT_POSSIBLE(
                "client-stageout-possible"), DEPROVISIONING("deprovisioning"), EXPIRED("expired"), POSTPROCESSING_CANCEL("postprocessing-cancel"), POSTPROCESSING_FAILURE(
                "postprocessing-failure"), PREPROCESSING_CANCEL("preprocessing-cancel"), PREPROCESSING_FAILURE("preprocessing-failure"), PROCESSING_CANCEL("processing-cancel"), PROCESSING_FAILURE(
                "processing-failure"), PROVISIONING("provisioning"), SERVER_PAUSED("server-paused"), SERVER_STAGEIN("server-stagein"), SERVER_STAGEOUT("server-stageout"), VALIDATING(
                "validating"), VALIDATION_FAILURE("validation-failure");

        private String name;

        private StatusAttributeName(String name) {
            this.name = name;
        }

        public static StatusAttributeName fromValue(String name) {
            StatusAttributeName[] statusAttributeName = values();

            for (int i = 0; i < statusAttributeName.length; i++) {
                if (statusAttributeName[i].getName().equals(name)) {
                    return statusAttributeName[i];
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }
    };

    public static enum StatusName {
        ACCEPTED("accepted"), POSTPROCESSING("postprocessing"), PREPROCESSING("preprocessing"), PROCESSING("processing"), PROCESSING_ACCEPTING("processing-accepting"), PROCESSING_QUEUED(
                "processing-queued"), PROCESSING_RUNNING("processing-running"), TERMINAL("terminal");

        private String name;

        private StatusName(String name) {
            this.name = name;
        }

        public static StatusName fromValue(String name) {
            StatusName[] statusName = values();

            for (int i = 0; i < statusName.length; i++) {
                if (statusName[i].getName().equals(name)) {
                    return statusName[i];
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }
    };

    private long id = 0;
    private Boolean isTransient = Boolean.FALSE;
    private String description;
    private StatusName statusName = null;
    private List<StatusAttributeName> statusAttributes;
    private XMLGregorianCalendar timestamp = null;

    public ActivityStatus() {
        this(null);
    }

    public ActivityStatus(StatusName statusName) {
        this.statusName = statusName;
    }

    public int compareTo(ActivityStatus status) {
        if (status == null) {
            return -1;
        }

        int result = statusName.compareTo(status.getStatusName());

        if (result == 0) {
            if (statusName == StatusName.TERMINAL && description.indexOf(status.getDescription()) < 0) {
                description = description.concat("; ").concat(status.getDescription());
            }

            result = timestamp.compare(status.getTimestamp());

            if (result == 0) {
                if (getStatusAttributes().size() < status.getStatusAttributes().size()) {
                    result = -1;
                } else if (getStatusAttributes().size() > status.getStatusAttributes().size()) {
                    result = 1;                    
                } else if (statusAttributes.containsAll(status.getStatusAttributes())) {
                    result = 0;
                } else {
                    result = -1;                    
                }
                
//            if (result == 0) {
//                return status.isTransient.compareTo(isTransient);
//            }
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ActivityStatus)) {
            return false;
        }

        ActivityStatus activityStatus = (ActivityStatus) obj;

        boolean areEqual = (statusName.equals(activityStatus.getStatusName()) && timestamp.compare(activityStatus.getTimestamp()) == 0);
        
        if (areEqual) {
            areEqual = (statusAttributes != null && activityStatus.getStatusAttributes() != null && 
                    statusAttributes.size() == activityStatus.getStatusAttributes().size() && 
                    statusAttributes.containsAll(activityStatus.getStatusAttributes()));
        }

        return areEqual;
    }

    public List<StatusAttributeName> getStatusAttributes() {
        if (statusAttributes == null) {
            statusAttributes = new ArrayList<StatusAttributeName>(0);
        }
        return statusAttributes;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public StatusName getStatusName() {
        return statusName;
    }

    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    public Boolean isTransient() {
        return isTransient;
    }

    public void setStatusAttributes(List<StatusAttributeName> statusAttributes) {
        this.statusAttributes = statusAttributes;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIsTransient(Boolean isTransient) {
        this.isTransient = isTransient;
    }

    public void setStatusName(StatusName statusName) {
        this.statusName = statusName;
    }

    public void setTimestamp(XMLGregorianCalendar timestamp) {
        if (timestamp != null) {
            timestamp.setMillisecond(0);
        }

        this.timestamp = timestamp;
    }

    public String toString() {
        StringBuffer info = new StringBuffer();
        info.append("statusName=").append(statusName.getName());
        info.append("; timestamp=").append(timestamp.toGregorianCalendar().getTime());

        if (description != null) {
            info.append("; description=").append(description);
        }

        if (statusAttributes != null && statusAttributes.size() > 0) {
            info.append("; attributes={");

            for (StatusAttributeName attribute : statusAttributes) {
                info.append(attribute.getName()).append(", ");
            }

            info = info.delete(info.length()-2, info.length());
            info.append("}");
        }

        return info.toString();
    }
}
