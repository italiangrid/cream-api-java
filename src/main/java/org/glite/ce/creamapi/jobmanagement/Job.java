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
 * Authors: Luigi Zangrando <zangrando@pd.infn.it>
 *
 */

package org.glite.ce.creamapi.jobmanagement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.glite.ce.creamapi.jobmanagement.command.JobCommand;

public class Job implements Serializable, Externalizable {
    private static final long serialVersionUID = 1L;

    private static final String JOBTYPE_INTERACTIVE = "interactive";
    private static final String JOBTYPE_MPICH = "mpich";
    private static final String JOBTYPE_NORMAL = "normal";
    private static final Random jobIdGenerator = new Random();
    public static final String version = "1.1";
    public static final String NOT_AVAILABLE_VALUE = "N/A";
    public static final String MAX_OUTPUT_SANDBOX_SIZE = "MAX_OUTPUT_SANDBOX_SIZE";
    public static final String DELEGATION_PROXY_CERT_SANDBOX_PATH = "DELEGATION_PROXY_CERT_SANDBOX_PATH";
    public static final String WMS_HOSTNAME = "WMS_HOSTNAME";
    public static final String OUTPUT_DATA = "OUTPUT_DATA";
    public static final String DS_UPLOAD_OUTPUT_FILE = "DS_UPLOAD_OUTPUT_FILE";

    private String creamURL = null;
    private String id = null;
    private String cerequirements = null;
    private String virtualOrganization = null;
    private String userId = null;
    private String batchSystem = null;
    private String queue = null;
    private String standardInput, standardOutput, standardError = null;
    private String executable = null;
    private String delegationProxyCertPath, authNProxyCertPath = null;
    private String hlrLocation = null;
    private String loggerDestURI = null;
    private String tokenURL = null;
    private String perusalFilesDestURI, perusalListFileURI = null;
    private String prologue, prologueArguments = null;
    private String epilogue, epilogueArguments = null;
    private String sequenceCode = null;
    private String lrmsJobId, lrmsAbsLayerJobId, gridJobId, iceId, fatherJobId, ceId = null;
    private String type, creamInputSandboxURI, creamOutputSandboxURI, sandboxBasePath, inputSandboxBaseURI, outputSandboxBaseDestURI = null;
    private String workerNode = null;
    private String jdl = null;
    private String myProxyServer = null;
    private String localUser, delegationProxyId, delegationProxyInfo, workingDirectory = null;
    private List<String> childJobId = null;
    private List<String> arguments = null;
    private List<String> outputSandboxDestURI, inputFiles, outputFiles = null;
    private int perusalTimeInterval, nodes = 0;
    private Hashtable<String, String> extraAttribute = null;
    private Hashtable<String, String> environment = null;
    private Hashtable<String, Object> volatileProperty = null;
    private List<JobStatus> statusHistory = null;
    private List<JobCommand> commandHistory = null;
    private Lease lease = null;

    public Job() {
        setId(generateJobId());
    }

    public Job(String id) {
        if (id == null) {
            setId(generateJobId());
        } else {
            setId(id);
        }
    }

    public void addArgument(String arg) {
        if (arg != null) {
            getArguments().add(arg);            
        }        
    }
    
    public void addCommandHistory(JobCommand cmd) {
        if (cmd != null) {
            cmd.setJobId(id);
            getCommandHistory().add(cmd);
        }
    }

    public void addEnvironmentAttribute(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key not specified!");
        }

        if (value == null) {
            throw new IllegalArgumentException("value not specified!");
        }

        getEnvironment().put(key, value);
    }

    public void addExtraAttribute(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key not specified!");
        }

        if (value == null) {
            throw new IllegalArgumentException("value not specified!");
        }

        getExtraAttribute().put(key, value);
    }

    public void addStatus(JobStatus status) {
        if (status != null) {
            status.setJobId(id);
            getStatusHistory().add(status);
        }
    }

    public void addVolatileProperty(String key, Object value) throws IllegalArgumentException {
        if (key != null && value != null) {
            getVolatileProperty().put(key, value);
        } else {
            throw new IllegalArgumentException("Neither the key nor the value can be null. (key=" + key + " value=" + value + ")");
        }
    }

    public boolean containsExtraAttribute(String key) {
        if (key != null) {
            return getExtraAttribute().containsKey(key);
        }

        return false;
    }

    public boolean containsVolatilePropertyKeys(String key) {
        if (key != null) {
            return getVolatileProperty().containsKey(key);
        }

        return false;
    }

    public synchronized String generateJobId() {
        String suffix = "000000000" + jobIdGenerator.nextInt(1000000000);
        suffix = suffix.substring(suffix.length() - 9);
        return "CREAM" + suffix;
    }

    public List<String> getArguments() {
        if (arguments == null) {
            arguments = new ArrayList<String>(0);
        }

        return arguments;
    }

    public String getAuthNProxyCertPath() {
        return authNProxyCertPath;
    }

    public String getBatchSystem() {
        return batchSystem;
    }

    public String getCeId() {
        return ceId;
    }

    public String getCeRequirements() {
        return cerequirements;
    }

    public List<String> getChildJobId() {
        return childJobId;
    }

    public List<JobCommand> getCommandHistory() {
        if (commandHistory == null) {
            commandHistory = new ArrayList<JobCommand>(0);
        }

        return commandHistory;
    }

    public JobCommand getCommandHistoryAt(int index) throws IndexOutOfBoundsException {
        return getCommandHistory().get(index);
    }

    public int getCommandHistoryCount() {
        return getCommandHistory().size();
    }

    public String getCREAMInputSandboxURI() {
        return creamInputSandboxURI;
    }

    public String getCREAMOutputSandboxURI() {
        return creamOutputSandboxURI;
    }

    public String getCREAMSandboxBasePath() {
        return sandboxBasePath;
    }

    public String getCreamURL() {
        return creamURL;
    }

    public String getDelegationProxyCertPath() {
        return delegationProxyCertPath;
    }

    public String getDelegationProxyId() {
        return delegationProxyId;
    }

    public String getDelegationProxyInfo() {
        return delegationProxyInfo;
    }

    public Hashtable<String, String> getEnvironment() {
        if (environment == null) {
            environment = new Hashtable<String, String>(0);
        }

        return environment;
    }

    public String getEnvironmentAttribute(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key not specified!");
        }

        return getEnvironment().get(key);
    }

    public String getEpilogue() {
        return epilogue;
    }

    public String getEpilogueArguments() {
        return epilogueArguments;
    }

    public String getExecutable() {
        return executable;
    }

    public Hashtable<String, String> getExtraAttribute() {
        if (extraAttribute == null) {
            extraAttribute = new Hashtable<String, String>(0);
        }

        return extraAttribute;
    }

    public String getExtraAttribute(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key not specified!");
        }

        return extraAttribute.get(key);
    }

    public String getFatherJobId() {
        return fatherJobId;
    }

    public String getGridJobId() {
        return gridJobId;
    }

    public String getHlrLocation() {
        return hlrLocation;
    }

    public String getICEId() {
        return iceId;
    }

    public String getId() {
        return id;
    }

    public List<String> getInputFiles() {
        if (inputFiles == null) {
            inputFiles = new ArrayList<String>(0);
        }
        
        return inputFiles;
    }

    public String getInputSandboxBaseURI() {
        return inputSandboxBaseURI;
    }

    public String getJDL() {
        return jdl;
    }

    public JobCommand getLastCommand() {
        List<JobCommand> cmdHistory = getCommandHistory();

        if (cmdHistory.size() > 0) {
            return cmdHistory.get(cmdHistory.size() - 1);
        }

        return null;
    }

    public JobStatus getLastStatus() {
        List<JobStatus> stHistory = getStatusHistory();
        if (stHistory.size() > 0) {
            return stHistory.get(stHistory.size() - 1);
        }

        return null;
    }

    public Lease getLease() {
        return lease;
    }

    public String getLocalUser() {
        return localUser;
    }

    public String getLoggerDestURI() {
        return loggerDestURI;
    }

    public String getLRMSAbsLayerJobId() {
        return lrmsAbsLayerJobId;
    }

    public String getLRMSJobId() {
        return lrmsJobId;
    }

    public String getMyProxyServer() {
        return myProxyServer;
    }

    public int getNodeNumber() {
        return nodes;
    }

    public List<String> getOutputFiles() {
        if (outputFiles == null) {
            outputFiles = new ArrayList<String>(0);
        }
        return outputFiles;
    }

    public String getOutputSandboxBaseDestURI() {
        return outputSandboxBaseDestURI;
    }

    public List<String> getOutputSandboxDestURI() {
        if (outputSandboxDestURI == null) {
            outputSandboxDestURI = new ArrayList<String>(0);
        }

        return outputSandboxDestURI;
    }

    public String getPerusalFilesDestURI() {
        return perusalFilesDestURI;
    }

    public String getPerusalListFileURI() {
        return perusalListFileURI;
    }

    public int getPerusalTimeInterval() {
        return perusalTimeInterval;
    }

    public String getPrologue() {
        return prologue;
    }

    public String getPrologueArguments() {
        return prologueArguments;
    }

    public String getQueue() {
        return queue;
    }

    public String getSandboxBasePath() {
        return sandboxBasePath;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public String getStandardError() {
        return standardError;
    }

    public String getStandardInput() {
        return standardInput;
    }

    public String getStandardOutput() {
        return standardOutput;
    }

    public JobStatus getStatusAt(int index) throws IndexOutOfBoundsException {
        return getStatusHistory().get(index);
    }

    public int getStatusCount() {
        return getStatusHistory().size();
    }

    public List<JobStatus> getStatusHistory() {
        if (statusHistory == null) {
            statusHistory = new ArrayList<JobStatus>(0);
        }

        return statusHistory;
    }

    public String getTokenURL() {
        return tokenURL;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getVirtualOrganization() {
        return virtualOrganization;
    }

    public Hashtable<String, Object> getVolatileProperty() {
        if (volatileProperty == null) {
            volatileProperty = new Hashtable<String, Object>(0);
        }

        return volatileProperty;
    }

    public Object getVolatileProperty(String key) {
        if (key != null) {
            return getVolatileProperty().get(key);
        }

        return null;
    }

    public Enumeration<String> getVolatilePropertyKeys() {
        return getVolatileProperty().keys();
    }

    public String getWorkerNode() {
        return workerNode;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public boolean isInteractive() {
        return JOBTYPE_INTERACTIVE.equalsIgnoreCase(getType());
    }

    public boolean isMpich() {
        return JOBTYPE_MPICH.equalsIgnoreCase(getType());
    }

    public boolean isNormal() {
        return JOBTYPE_NORMAL.equalsIgnoreCase(getType());
    }

    private Calendar readCalendar(ObjectInput in) throws IOException {
        long ts = in.readLong();

        if (ts == 0) {
            return null;
        }

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(ts);
        return result;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String version = readString(in);
        if (!version.equalsIgnoreCase(version)) {
            throw new IOException("job serialization version mismatch: found \"" + version + "\" required \"" + this.version + "\"");
        }

        creamURL = readString(in);
        id = readString(in);
        lrmsJobId = readString(in);
        lrmsAbsLayerJobId = readString(in);
        gridJobId = readString(in);
        iceId = readString(in);
        fatherJobId = readString(in);
        ceId = readString(in);
        userId = readString(in);
        jdl = readString(in);
        myProxyServer = readString(in);
        workerNode = readString(in);
        cerequirements = readString(in);
        virtualOrganization = readString(in);
        batchSystem = readString(in);
        queue = readString(in);
        standardInput = readString(in);
        standardOutput = readString(in);
        standardError = readString(in);
        executable = readString(in);
        delegationProxyCertPath = readString(in);
        authNProxyCertPath = readString(in);
        hlrLocation = readString(in);
        loggerDestURI = readString(in);
        tokenURL = readString(in);
        perusalFilesDestURI = readString(in);
        perusalListFileURI = readString(in);
        prologue = readString(in);
        prologueArguments = readString(in);
        epilogue = readString(in);
        epilogueArguments = readString(in);
        sequenceCode = readString(in);
        type = readString(in);
        creamInputSandboxURI = readString(in);
        creamOutputSandboxURI = readString(in);
        sandboxBasePath = readString(in);
        inputSandboxBaseURI = readString(in);
        outputSandboxBaseDestURI = readString(in);
        localUser = readString(in);
        delegationProxyId = readString(in);
        delegationProxyInfo = readString(in);
        workingDirectory = readString(in);
        arguments = readStringArray(in);
        childJobId = readStringArray(in);
        outputSandboxDestURI = readStringArray(in);
        inputFiles = readStringArray(in);
        outputFiles = readStringArray(in);
        nodes = in.readInt();
        perusalTimeInterval = in.readInt();

        int htSize = in.readInt();

        if (htSize == 1) {
            lease = new Lease();
            lease.setLeaseId(readString(in));
            lease.setUserId(readString(in));
            lease.setLeaseTime(readCalendar(in));
        }

        htSize = in.readInt();
        if (htSize >= 0) {
            Hashtable<String, String> environment = getEnvironment();
            environment.clear();

            for (int k = 0; k < htSize; k++) {
                environment.put(in.readUTF(), in.readUTF());
            }
        }

        htSize = in.readInt();
        if (htSize >= 0) {
            Hashtable<String, String> extraAttribute = getExtraAttribute();
            extraAttribute.clear();

            for (int k = 0; k < htSize; k++) {
                extraAttribute.put(in.readUTF(), in.readUTF());
            }
        }

        htSize = in.readInt();
        if (htSize >= 0) {
            List<JobStatus> stHistory = getStatusHistory();
            stHistory.clear();

            for (int i = 0; i < htSize; i++) {
                stHistory.add(readJobStatus(in));
            }
        }

        htSize = in.readInt();
        if (htSize >= 0) {
            List<JobCommand> cmdHistory = getCommandHistory();
            cmdHistory.clear();

            for (int i = 0; i < htSize; i++) {
                cmdHistory.add(readJobCommand(in));
            }
        }
    }

    private JobCommand readJobCommand(ObjectInput in) throws IOException {
        if (in == null) {
            throw new IOException("readJobCommand error: ObjectInput is null");
        }

        JobCommand cmd = new JobCommand();
        cmd.setCommandExecutorName(readString(in));
        cmd.setDescription(readString(in));
        cmd.setFailureReason(readString(in));
        cmd.setJobId(readString(in));
        cmd.setUserId(readString(in));
        cmd.setCreationTime(readCalendar(in));
        cmd.setExecutionCompletedTime(readCalendar(in));
        cmd.setStartProcessingTime(readCalendar(in));
        cmd.setStartSchedulingTime(readCalendar(in));
        cmd.setStatus(in.readInt());
        cmd.setType(in.readInt());

        return cmd;
    }

    private JobStatus readJobStatus(ObjectInput in) throws IOException {
        if (in == null) {
            throw new IOException("readJobStatus error: ObjectInput is null");
        }

        JobStatus status = new JobStatus(in.readInt());
        status.setDescription(readString(in));
        status.setExitCode(readString(in));
        status.setFailureReason(readString(in));
        status.setJobId(readString(in));
        status.setTimestamp(readCalendar(in));

        return status;
    }

    private String readString(ObjectInput in) throws IOException {
        if (in == null) {
            throw new IOException("ObjectIntput is null");
        }

        String s = in.readUTF();
        if (s == null || s.equals("")) {
            return null;
        }
        return s;
    }

    private List<String> readStringArray(ObjectInput in) throws IOException {
        int size = in.readInt();
        if (size >= 0) {
            List<String> result = new ArrayList<String>(size);
            for (int k = 0; k < size; k++) {
                result.add(in.readUTF());
            }
            return result;
        }

        return null;
    }

    public void setArguments(List<String> argumentList) {
        List<String> arguments = getArguments();
        arguments.clear();

        if (argumentList != null) {
            arguments.addAll(argumentList);
        }
    }

    public void setAuthNProxyCertPath(String authNProxyCertPath) {
        this.authNProxyCertPath = authNProxyCertPath;
    }

    public void setBatchSystem(String batchSystem) {
        this.batchSystem = batchSystem;
    }

    public void setCeId(String ceId) {
        this.ceId = ceId;
    }

    public void setCeRequirements(String cerequirements) {
        this.cerequirements = cerequirements;
    }

    public void setChildJobId(List<String> childJobId) {
        this.childJobId = childJobId;
    }

    public void setCommandHistory(List<JobCommand> cmdList) {
        List<JobCommand> cmdHistory = getCommandHistory();
        cmdHistory.clear();

        if (cmdList != null) {
            cmdHistory.addAll(cmdList);
        }
    }

    public void setCREAMInputSandboxURI(String inputSandboxURI) {
        creamInputSandboxURI = inputSandboxURI;
    }

    public void setCREAMOutputSandboxURI(String outputSandboxURI) {
        creamOutputSandboxURI = outputSandboxURI;
    }

    public void setCREAMSandboxBasePath(String sandboxBasePath) {
        this.sandboxBasePath = sandboxBasePath;
    }

    public void setCreamURL(String creamURL) {
        this.creamURL = creamURL;
    }

    public void setDelegationProxyCertPath(String dlgProxyCertPath) {
        this.delegationProxyCertPath = dlgProxyCertPath;
    }

    public void setDelegationProxyId(String proxyDelegationId) {
        this.delegationProxyId = proxyDelegationId;
    }

    public void setDelegationProxyInfo(String proxyInfo) {
        this.delegationProxyInfo = proxyInfo;
    }

    public void setEnvironment(Hashtable<String, String> env) {
        Hashtable<String, String> environment = getEnvironment();
        environment.clear();

        if (env != null) {
            environment.putAll(env);
        }
    }

    public void setEpilogue(String epilogue) {
        this.epilogue = epilogue;
    }

    public void setEpilogueArguments(String args) {
        epilogueArguments = args;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public void setExtraAttribute(Hashtable<String, String> extraAttr) {
        Hashtable<String, String> extraAttribute = getExtraAttribute();
        extraAttribute.clear();

        if (extraAttr != null) {
            extraAttribute.putAll(extraAttr);
        }
    }

    public void setFatherJobId(String fatherJobId) {
        this.fatherJobId = fatherJobId;
    }

    public void setGridJobId(String gridJobId) {
        this.gridJobId = gridJobId;
    }

    public void setHlrLocation(String hl) {
        hlrLocation = hl;
    }

    public void setICEId(String iceId) {
        this.iceId = iceId;
    }

    public void setId(String id) {
        this.id = id;

        for (JobStatus status : getStatusHistory()) {
            status.setJobId(id);
        }

        for (JobCommand cmd : getCommandHistory()) {
            cmd.setJobId(id);
        }
    }

    public void setInputFiles(List<String> inputFiles) {
        List<String> inFiles = getInputFiles();
        inFiles.clear();

        if (inputFiles != null) {
            inFiles.addAll(inputFiles);
        }
    }

    public void setInputSandboxBaseURI(String uri) {
        this.inputSandboxBaseURI = uri;
    }

    public void setJDL(String jdl) {
        this.jdl = jdl;
    }

    public void setLastCommand(JobCommand cmd) {
        if (cmd != null) {
            cmd.setJobId(id);

            List<JobCommand> cmdHistory = getCommandHistory();

            if (cmdHistory.size() == 0) {
                cmdHistory.add(cmd);
            } else {
                cmdHistory.set(cmdHistory.size() - 1, cmd);
            }
        }
    }

    public void setLastStatus(JobStatus status) {
        if (status != null) {
            status.setJobId(getId());

            List<JobStatus> stHistory = getStatusHistory();

            if (stHistory.size() == 0) {
                stHistory.add(status);
            } else {
                stHistory.set(stHistory.size() - 1, status);
            }
        }
    }

    public void setLease(Lease lease) {
        if (lease != null) {
            lease.setUserId(userId);

            if (lease.getLeaseId() == null) {
                lease.setLeaseId(id);
            }
        }
        this.lease = lease;
    }

    public void setLocalUser(String localUser) {
        this.localUser = localUser;
    }

    public void setLoggerDestURI(String s) {
        loggerDestURI = s;
    }

    public void setLRMSAbsLayerJobId(String lrmsAbsLayerJobId) {
        this.lrmsAbsLayerJobId = lrmsAbsLayerJobId;
    }

    public void setLRMSJobId(String lrmsJobId) {
        this.lrmsJobId = lrmsJobId;
    }

    public void setMyProxyServer(String myProxyServer) {
        this.myProxyServer = myProxyServer;
    }

    public void setNodeNumber(int n) {
        nodes = n;
    }

    public void setOutputFiles(List<String> outputFileList) {
        List<String> list = getOutputFiles();
        list.clear();
        
        if (outputFileList != null) {
            list.addAll(outputFileList);
        }
    }

    public void setOutputSandboxBaseDestURI(String uri) {
        outputSandboxBaseDestURI = uri;
    }

    public void setOutputSandboxDestURI(List<String> uriList) {
        List<String> list = getOutputSandboxDestURI();
        list.clear();
        
        if (uriList != null) {
            list.addAll(uriList);
        }
    }

    public void setPerusalFilesDestURI(String perusalFilesDestURI) {
        this.perusalFilesDestURI = perusalFilesDestURI;
    }

    public void setPerusalListFileURI(String perusalListFileURI) {
        this.perusalListFileURI = perusalListFileURI;
    }

    public void setPerusalTimeInterval(int perusalTimeInterval) {
        this.perusalTimeInterval = perusalTimeInterval;
    }

    public void setPrologue(String prologue) {
        this.prologue = prologue;
    }

    public void setPrologueArguments(String args) {
        prologueArguments = args;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setSandboxBasePath(String sandboxBasePath) {
        this.sandboxBasePath = sandboxBasePath;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public void setStandardError(String standardError) {
        this.standardError = standardError;
    }

    public void setStandardInput(String standardInput) {
        this.standardInput = standardInput;
    }

    public void setStandardOutput(String standardOutput) {
        this.standardOutput = standardOutput;
    }

    public void setStatusHistory(List<JobStatus> statusList) {
        List<JobStatus> stHistory = getStatusHistory();
        stHistory.clear();
        
        if (statusList != null) {
            stHistory.addAll(statusList);
        }
    }

    public void setTokenURL(String s) {
        tokenURL = s;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVirtualOrganization(String vo) {
        virtualOrganization = vo;
    }

    public void setVolatileProperty(Hashtable<String, Object> volatileProperty) {
        Hashtable<String, Object> vlProperty = getVolatileProperty();
        vlProperty.clear();
        
        if (volatileProperty != null) {
            vlProperty.putAll(volatileProperty);
        }
    }

    public void setWorkerNode(String wn) {
        workerNode = wn;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    private void writeCalendar(ObjectOutput out, Calendar cal) throws IOException {
        out.writeLong(cal != null ? cal.getTimeInMillis() : 0);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if (out == null) {
            throw new IOException("ObjectOutput is null");
        }
        writeString(out, version);
        writeString(out, creamURL);
        writeString(out, id);
        writeString(out, lrmsJobId);
        writeString(out, lrmsAbsLayerJobId);
        writeString(out, gridJobId);
        writeString(out, iceId);
        writeString(out, fatherJobId);
        writeString(out, ceId);
        writeString(out, userId);
        writeString(out, jdl);
        writeString(out, myProxyServer);
        writeString(out, workerNode);
        writeString(out, cerequirements);
        writeString(out, virtualOrganization);
        writeString(out, batchSystem);
        writeString(out, queue);
        writeString(out, standardInput);
        writeString(out, standardOutput);
        writeString(out, standardError);
        writeString(out, executable);
        writeString(out, delegationProxyCertPath);
        writeString(out, authNProxyCertPath);
        writeString(out, hlrLocation);
        writeString(out, loggerDestURI);
        writeString(out, tokenURL);
        writeString(out, perusalFilesDestURI);
        writeString(out, perusalListFileURI);
        writeString(out, prologue);
        writeString(out, prologueArguments);
        writeString(out, epilogue);
        writeString(out, epilogueArguments);
        writeString(out, sequenceCode);
        writeString(out, type);
        writeString(out, creamInputSandboxURI);
        writeString(out, creamOutputSandboxURI);
        writeString(out, sandboxBasePath);
        writeString(out, inputSandboxBaseURI);
        writeString(out, outputSandboxBaseDestURI);
        writeString(out, localUser);
        writeString(out, delegationProxyId);
        writeString(out, delegationProxyInfo);
        writeString(out, workingDirectory);
        writeStringArray(out, arguments);
        writeStringArray(out, childJobId);
        writeStringArray(out, outputSandboxDestURI);
        writeStringArray(out, inputFiles);
        writeStringArray(out, outputFiles);
        out.writeInt(nodes);
        out.writeInt(perusalTimeInterval);

        if (lease != null) {
            out.writeInt(1);
            writeString(out, lease.getLeaseId());
            writeString(out, lease.getUserId());
            writeCalendar(out, lease.getLeaseTime());
        } else {
            out.writeInt(-1);
        }

        Hashtable<String, String> environment = getEnvironment();
        if (environment.size() > 0) {
            out.writeInt(environment.size());

            Enumeration<String> allKeys = environment.keys();
            while (allKeys.hasMoreElements()) {
                String key = allKeys.nextElement();
                writeString(out, key);
                writeString(out, environment.get(key));
            }
        } else {
            out.writeInt(-1);
        }

        Hashtable<String, String> extraAttribute = getExtraAttribute();
        if (extraAttribute.size() > 0) {
            out.writeInt(extraAttribute.size());

            Enumeration<String> allKeys = extraAttribute.keys();
            while (allKeys.hasMoreElements()) {
                String key = allKeys.nextElement();
                writeString(out, key);
                writeString(out, extraAttribute.get(key));
            }
        } else {
            out.writeInt(-1);
        }

        List<JobStatus> stHistory = getStatusHistory();
        if (stHistory.size() > 0) {
            out.writeInt(stHistory.size());
            for (JobStatus status : stHistory) {
                writeJobStatus(out, status);
            }
        } else {
            out.writeInt(-1);
        }

        List<JobCommand> cmdHistory = getCommandHistory();
        if (cmdHistory.size() > 0) {
            out.writeInt(cmdHistory.size());
            for (JobCommand cmd : cmdHistory) {
                writeJobCommand(out, cmd);
            }
        } else {
            out.writeInt(-1);
        }
    }

    private void writeJobCommand(ObjectOutput out, JobCommand cmd) throws IllegalArgumentException, IOException {
        if (cmd == null) {
            throw new IllegalArgumentException("writeJobCommand error: jobCommand not specified!");
        }

        if (out == null) {
            throw new IOException("writeJobCommand error: ObjectInput is null");
        }

        writeString(out, cmd.getCommandExecutorName());
        writeString(out, cmd.getDescription());
        writeString(out, cmd.getFailureReason());
        writeString(out, cmd.getJobId());
        writeString(out, cmd.getUserId());
        writeCalendar(out, cmd.getCreationTime());
        writeCalendar(out, cmd.getExecutionCompletedTime());
        writeCalendar(out, cmd.getStartProcessingTime());
        writeCalendar(out, cmd.getStartSchedulingTime());
        out.writeInt(cmd.getStatus());
        out.writeInt(cmd.getType());
    }

    private void writeJobStatus(ObjectOutput out, JobStatus jobStatus) throws IllegalArgumentException, IOException {
        if (jobStatus == null) {
            throw new IllegalArgumentException("writeJobStatus error: jobStatus not specified!");
        }

        if (out == null) {
            throw new IOException("writeJobStatus error: ObjectInput is null");
        }

        out.writeInt(jobStatus.getType());
        writeString(out, jobStatus.getDescription());
        writeString(out, jobStatus.getExitCode());
        writeString(out, jobStatus.getFailureReason());
        writeString(out, jobStatus.getJobId());
        writeCalendar(out, jobStatus.getTimestamp());
    }

    private void writeString(ObjectOutput out, String s) throws IOException {
        out.writeUTF(s != null ? s : "");
    }

    private void writeStringArray(ObjectOutput out, List<String> array) throws IOException {
        if (array != null) {
            out.writeInt(array.size());

            for (int k = 0; k < array.size(); k++) {
                out.writeUTF(array.get(k) != null ? array.get(k) : "");
            }
        } else {
            out.writeInt(-1);
        }
    }
}
