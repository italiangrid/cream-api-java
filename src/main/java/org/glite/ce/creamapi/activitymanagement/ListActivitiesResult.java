package org.glite.ce.creamapi.activitymanagement;

import java.util.ArrayList;
import java.util.List;

public class ListActivitiesResult {
    private List<String> activityIdList;
    private Boolean isTruncated = null;

    public List<String> getActivityIdList() {
        if (activityIdList == null) {
            activityIdList = new ArrayList<String>(0);
        }

        return activityIdList;
    }

    public Boolean isTruncated() {
        if (isTruncated == null) {
            isTruncated = Boolean.FALSE;
        }

        return isTruncated;
    }

    public void setActivityIdList(List<String> activityIdList) {
        this.activityIdList = activityIdList;
    }

    public void setIsTruncated(Boolean isTruncated) {
        this.isTruncated = isTruncated;
    }
}
