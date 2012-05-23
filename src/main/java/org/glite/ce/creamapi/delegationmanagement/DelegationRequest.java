package org.glite.ce.creamapi.delegationmanagement;

import java.util.Date;
import java.util.List;

public class DelegationRequest {
    private String id = null;
    private String dn = null;
    private String publicKey = null;
    private String privateKey = null;
    private String localUser = null;
    private String certificateRequest = null;
    private List<String> vomsAttributes = null;
    private Date timestamp = null;

    public DelegationRequest(String id) {
        this.id = id;
    }

    public String getCertificateRequest() {
        return certificateRequest;
    }

    public String getDN() {
        return dn;
    }

    public String getId() {
        return id;
    }

    public String getLocalUser() {
        return localUser;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<String> getVOMSAttributes() {
        return vomsAttributes;
    }

    public void setCertificateRequest(String certificateRequest) {
        this.certificateRequest = certificateRequest;
    }

    public void setDN(String dn) {
        this.dn = dn;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLocalUser(String localUser) {
        this.localUser = localUser;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;        
    }

    public void setVOMSAttributes(List<String> vomsAttributes) {
        this.vomsAttributes = vomsAttributes;
    }
}
