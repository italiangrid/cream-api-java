/*
 * Copyright (c) Members of the EGEE Collaboration. 2004. 
 * See http://www.eu-egee.org/partners/ for details on the copyright
 * holders.  
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

/*
 * 
 * Authors: Luigi Zangrando, <luigi.zangrando@pd.infn.it>
 *
 */

package org.glite.ce.creamapi.delegationmanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Delegation implements Cloneable {
    private static final long serialVersionUID = 1L;

    private String id = "N/A";
    private String info = "N/A";
    private String certificate = null;
    private String vo = null;
    private String fqan = null;
    private String dn = null;
    private String localUser = null;
    private String localUserGroup = null;
    private String fileName = null;
    private String path = null;
    private List<String> vomsAttributes = null;
    private Date startTime = null;
    private Date expirationTime = null;
    private Date lastUpdateTime = null;
    boolean rfc = false;

    public Delegation(String id) {
        this.id = id;
    }

    public Delegation(String id, String certificate) {
        this.id = id;
        this.certificate = certificate;
    }

    public Object clone() {
        Delegation delegation = new Delegation(id);
        delegation.setCertificate(certificate);
        delegation.setInfo(info);
        delegation.setDN(dn);
        delegation.setFQAN(fqan);
        delegation.setVO(vo);
        delegation.setLocalUser(localUser);
        delegation.setLocalUserGroup(localUserGroup);
        delegation.setVOMSAttributes(vomsAttributes);
        delegation.setExpirationTime(expirationTime);
        delegation.setLastUpdateTime(lastUpdateTime);
        delegation.setStartTime(startTime);
        delegation.setPath(path);

        return delegation;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getDN() {
        return dn;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFQAN() {
        return fqan;
    }

    public String getFullPath() {
        if (path != null && fileName != null) {
            return path + fileName;            
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getLocalUser() {
        return localUser;
    }

    public String getLocalUserGroup() {
        return localUserGroup;
    }

    public String getPath() {
        return path;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getUserId() {
        if (fqan != null) {
            return normalize(dn + fqan);
        }

        return normalize(dn);
    }

    public String getVO() {
        return vo;
    }

    public List<String> getVOMSAttributes() {
        if (vomsAttributes == null) {
            vomsAttributes = new ArrayList<String>(0);
        }
        return vomsAttributes;
    }

    public boolean isRFC() {
        return rfc;
    }

    public boolean isValid() {
        return System.currentTimeMillis() < expirationTime.getTime();
    }

    private String normalize(String s) {
        if (s != null) {
            return s.replaceAll("\\W", "_");
        }
        return null;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setDN(String dn) {
        this.dn = dn;
    }

    public void setExpirationTime(Date time) {
        expirationTime = time;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFQAN(String fqan) {
        this.fqan = fqan;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setLocalUser(String localUser) {
        this.localUser = localUser;
    }

    public void setLocalUserGroup(String localUserGroup) {
        this.localUserGroup = localUserGroup;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRFC(boolean b) {
        rfc = b;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setVO(String vo) {
        this.vo = vo;
    }

    public void setVOMSAttributes(List<String> vomsAttributes) {
        this.vomsAttributes = vomsAttributes;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer("[id='");
        buff.append(id);
        buff.append("'; rfc=").append(rfc);

        if (dn != null) {
            buff.append("; dn='").append(dn);
        }

        if (localUser != null) {
            buff.append("'; localUser='").append(localUser);
        }

        if (vo != null) {
            buff.append("'; vo='").append(vo);
        }

        if (startTime != null || expirationTime != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            if (startTime != null) {
                buff.append("'; startTime='").append(dateFormat.format(startTime)).append(" (GMT)");
            }

            if (expirationTime != null) {
                buff.append("'; expirationTime='").append(dateFormat.format(expirationTime)).append(" (GMT)");
            }
        }

//        if (path != null && fileName != null) {
//            buff.append("'; path='").append(path).append(fileName);
//        }

        buff.append("'];");

        return buff.toString();
    }
}
