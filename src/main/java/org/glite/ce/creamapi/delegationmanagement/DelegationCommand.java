package org.glite.ce.creamapi.delegationmanagement;

import org.glite.ce.creamapi.cmdmanagement.Command;

public class DelegationCommand extends Command {
    //Command category
    public static final String DELEGATION_MANAGEMENT = "DELEGATION_MANAGEMENT";

    //Command names
    public static final String DESTROY_DELEGATION         = "DESTROY_DELEGATION";
    public static final String GET_DATABASE_VERSION       = "GET_DATABASE_VERSION";
    public static final String GET_DELEGATION             = "GET_DELEGATION";
    public static final String GET_NEW_DELEGATION_REQUEST = "GET_NEW_DELEGATION_REQUEST";
    public static final String GET_DELEGATION_REQUEST     = "GET_DELEGATION_REQUEST";
    public static final String GET_SERVICE_MEDATADA       = "GET_SERVICE_MEDATADA";
    public static final String GET_TERMINATION_TIME       = "GET_TERMINATION_TIME";
    public static final String PUT_DELEGATION             = "PUT_DELEGATION";
    public static final String RENEW_DELEGATION_REQUEST   = "RENEW_DELEGATION_REQUEST";

    //Command fields
    public static final String CERTIFICATE_REQUEST = "CERTIFICATE_REQUEST";
    public static final String DELEGATION          = "DELEGATION";    
    public static final String DELEGATION_ID       = "DELEGATION_ID";
    public static final String ISSUER              = "ISSUER";
    public static final String LOCAL_USER          = "LOCAL_USER";
    public static final String LOCAL_USER_GROUP    = "LOCAL_USER_GROUP";
    public static final String SUBJECT             = "SUBJECT";
    public static final String TERMINATION_TIME    = "TERMINATION_TIME";
    public static final String USER_CERTIFICATE    = "USER_CERTIFICATE";
    public static final String USER_DN_RFC2253     = "USER_DN_RFC2253";
    public static final String USER_DN_X500        = "USER_DN_X500";
    public static final String VOMS_ATTRIBUTES     = "VOMS_ATTRIBUTES";

    public DelegationCommand(String name) {
        super(name, DELEGATION_MANAGEMENT);
    }
}
