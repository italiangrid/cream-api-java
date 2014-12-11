package org.glite.ce.creamapi.jobmanagement.jdl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import condor.classad.AttrName;
import condor.classad.ClassAdParser;
import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

public class JDL {    
    /*** JDL attributes */
    public static final String ARGUMENTS = "Arguments";
    public static final String BATCHSYSTEM = "BatchSystem";
    public static final String CEREQUIREMENTS = "cerequirements";
    public static final String CPUNUMBER = "CpuNumber";
    public static final String EDG_JOBID = "edg_jobid";
    public static final String ENVIRONMENT = "Environment";
    public static final String EPILOGUE = "Epilogue";
    public static final String EPILOGUE_ARGUMENTS = "EpilogueArguments";
    public static final String EXECUTABLE = "Executable";
    public static final String HLRLOCATION = "HLRLocation";
    public static final String HOSTNUMBER = "HostNumber";
    public static final String INPUTSANDBOX = "InputSandbox";
    public static final String INPUTSANDBOX_BASEURI = "InputSandboxBaseURI";
    public static final String JOBTYPE = "JobType";
    public static final String LB_ADDRESS = "LBAddress";
    public static final String LB_SEQUENCE_CODE = "LB_sequence_code";
    public static final String MAX_OUTPUT_SANDBOX_SIZE = "MaxOutputSandboxSize";
    public static final String MWVERSION = "MwVersion";
    public static final String MYPROXYSERVER = "MyProxyServer";
    public static final String NODENUMBER = "NodeNumber";
    public static final String OSBBASEURI = "OutputSandboxBaseDestURI";
    public static final String OSBURI = "OutputSandboxDestURI";
    public static final String OUTPUTDATA = "OutputData";
    public static final String OUTPUTDATA_OUTPUTFILE = "OutputFile";
    public static final String OUTPUTDATA_LOGICALFILENAME = "LogicalFileName";
    public static final String OUTPUTDATA_STORAGEELEMENT = "StorageElement";
    public static final String OUTPUTSANDBOX = "OutputSandbox";
    public static final String OUTPUTSANDBOX_BASEDESTURI = "OutputSandboxBaseDestURI";
    public static final String OUTPUTSANDBOX_DESTURI = "OutputSandboxDestURI";
    public static final String OUTPUTSB = "OutputSandbox";
    public static final String PERUSAL_FILEENABLE = "PerusalFileEnable";
    public static final String PERUSAL_FILESDESTURI = "PerusalFilesDestURI";
    public static final String PERUSAL_LISTFILEURI = "PerusalListFileURI";
    public static final String PERUSAL_TIMEINTERVAL = "PerusalTimeInterval";
    public static final String PROLOGUE = "Prologue";
    public static final String PROLOGUE_ARGUMENTS = "PrologueArguments";
    public static final String QUEUENAME = "QueueName";
    public static final String REALLYRUNNINGTOKEN = "ReallyRunningToken";
    public static final String SMPGRANULARITY = "SMPGranularity";
    public static final String STDERROR = "StdError";
    public static final String STDINPUT = "StdInput";
    public static final String STDOUTPUT = "StdOutput";
    public static final String TYPE = "Type";
    public static final String VIRTUALORGANISATION = "VirtualOrganisation";
    public static final String WHOLENODES = "WholeNodes";
    public static final String WMS_HOSTNAME = "WMS_HOSTNAME";

    private Hashtable<String, Expr> attributes = null;
    private String jdl = null;

    public JDL(String jdl) throws Exception {
        if (jdl == null) {
            throw (new IllegalArgumentException("JDL not defined!"));
        }

        this.jdl = jdl;
        attributes = new Hashtable<String, Expr>(0);

        parse();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    private String checkAttributeFile(String attribute, boolean isMandatory, boolean isAbsolutePathAllowed) throws Exception {
        String path = (String) getValue(attribute, Expr.STRING, isMandatory);

        if (path == null) {
            if (isMandatory) {
                throw new Exception(attribute + ": file name not defined!");
            } else {
                return null;
            }
        }

        if (path.endsWith("/") || path.endsWith("\\")) {
            throw new Exception(attribute + ": directory path not allowed");
        }

        if (path.indexOf("*") >= 0 || path.indexOf("?") >= 0 || path.indexOf("[") >= 0 || path.indexOf("]") >= 0) {
            throw new Exception(attribute + ": wildcards not allowed");
        }

        if (isAbsolutePathAllowed) {
            if (!(path.startsWith("$") || path.startsWith(File.separator)) && !new File(path).getName().equals(path)) {
                throw new Exception(attribute + ": relative path not allowed");
            }
        } else if (path.startsWith("$") || path.startsWith(File.separator)) {
            throw new Exception(attribute + ": absolute path not allowed");
        }

        return path;
    }

    private void checkBaseURL(String uriValue) throws Exception {
        if (uriValue == null) {
            return;
        }

        String uriWithoutDN = uriValue;
        if (uriValue.indexOf("?DN=") != -1) {
            uriWithoutDN = getURIWithoutDN(uriValue);
        }
        URI uri = null;
        try {
            uri = new URI(uriWithoutDN);
        } catch (URISyntaxException use) {
            throw new Exception("Illegal character for the URI: " + uriValue);
        }

        String scheme = uri.getScheme();

        if (scheme == null || (!scheme.equalsIgnoreCase("gsiftp") && !scheme.equalsIgnoreCase("file") && !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new Exception("the URI \"" + uriValue + "\" doesn't present an allowed scheme (gsiftp, file, http, https)");
        }
    }

    public void checkSandbox(ArrayList<String> list) throws Exception {
        if (list == null) {
            return;
        }

        Hashtable<String, String> table = new Hashtable<String, String>(list.size());
        String key = null;
        int index = 0;

        for (String item : list) {
            if (item.indexOf("?DN=") != -1) {
                key = getURIWithoutDN(item);
            } else {
                key = item;
            }

            index = key.lastIndexOf("/");
            if (index > -1) {
                key = key.substring(index + 1);
            }

            if (table.containsKey(key)) {
                throw new Exception("the file list cannot contain two or more files having the same name (even if in different paths)");
            } else {
                table.put(key, item);
            }
        }
    }

    public String getArguments() throws Exception {
        return (String) getValue(ARGUMENTS, Expr.STRING, false);
    }

    public String getBatchSystem() throws Exception {
        String value = (String) getValue(BATCHSYSTEM, Expr.STRING, true);

        if (value == null) {
            throw new Exception(BATCHSYSTEM + ": LRMS name not defined!");
        }

        return value;
    }

    public String getCERequirements() throws Exception {
        return (String) getValue(CEREQUIREMENTS, Expr.STRING, false);
    }

    private Object getConstant(String attrName, Expr value, int exprType) throws IllegalArgumentException {
        /*
         * if (value.type == Expr.LIST) { // The passed value could be a
         * sub-list return getValue(attrName, value, exprType); // Vector Value
         * } else if (value.type != exprType) { throw new
         * IllegalArgumentException(attrName +
         * ": Requested type doesn't match with value type found."); }
         * 
         * if (exprType == Expr.RECORD) { Ad ad = new Ad(); ad.jobAd =
         * (RecordExpr) value; // put the right casting TBD put a // valid
         * constructor return ad; } else {
         */
        Constant co = (Constant) value;
        if (exprType == Expr.INTEGER) {
            return new Integer(co.intValue()); // Integer Value
        } else if (exprType == Expr.BOOLEAN) {
            return new Boolean(co.isTrue()); // Boolean Value
        } else if (exprType == Expr.REAL) {
            return new Double(co.realValue()); // Real Value
        } else if (exprType == Expr.STRING) {
            return co.stringValue(); // String Value
        } else {
            throw new IllegalArgumentException(attrName + ": unexpected type found.");
        }
        // }
    }

    public String getEDGJobId() throws Exception {
        String value = (String) getValue(EDG_JOBID, Expr.STRING, false);

        if (value == null) {
            value = "N/A";
        }

        return value;
    }

    public Hashtable<String, String> getEnvironment() throws Exception {
        ArrayList<String> list = (ArrayList<String>) getValue(ENVIRONMENT, Expr.LIST, false);

        if (list == null) {
            return null;
        }

        Hashtable<String, String> result = new Hashtable<String, String>(list.size());
        int index = 0;
        for (String item : list) {
            if ((index = item.indexOf("=")) > -1) {
                result.put(item.substring(0, index).trim(), item.substring(index + 1).trim());
            } else {
                throw new Exception(ENVIRONMENT + ": wrong item " + item + " (equality required)");
            }
        }
        return result;
    }

    public String getEpilogue() throws Exception {
        return checkAttributeFile(EPILOGUE, false, true);
    }

    public String getEpilogueArguments() throws Exception {
        return (String) getValue(EPILOGUE_ARGUMENTS, Expr.STRING, false);
    }

    public String getExecutable() throws Exception {
        return checkAttributeFile(EXECUTABLE, true, true);
    }

    public String getHLRLocation() throws Exception {
        return (String) getValue(HLRLOCATION, Expr.STRING, false);
    }

    public int getHostNumber() throws Exception {
        Integer value = (Integer) getValue(HOSTNUMBER, Expr.INTEGER, false);

        if (value != null) {
            if (value <= 0) {
                throw new Exception(HOSTNUMBER + ": the value must be >=1");
            } else {
                return value.intValue();
            }
        }

        return -1;
    }

    public ArrayList<String> getInputSandbox() throws Exception {
        ArrayList<String> list = (ArrayList<String>) getValue(INPUTSANDBOX, Expr.LIST, false);

        try {
            checkSandbox(list);
        } catch (Exception ex) {
            throw new Exception(INPUTSANDBOX + ": " + ex.getMessage());
        }

        return list;
    }

    public String getInputSandboxBaseURI() throws Exception {
        String uri = (String) getValue(INPUTSANDBOX_BASEURI, Expr.STRING, false);

        try {
            checkBaseURL(uri);
        } catch (Exception ex) {
            throw new Exception(INPUTSANDBOX_BASEURI + ": " + ex.getMessage());
        }

        return uri;
    }

    public String getJobType() throws Exception {
        String value = (String) getValue(JOBTYPE, Expr.STRING, false);

        if (value != null && !value.equalsIgnoreCase("Normal")) {
            throw new Exception(JOBTYPE + ": \"" + value + "\" not allowed");
        }

        if (value == null) {
            value = "Normal";
        }

        return value;
    }

    public String getLBAddress() throws Exception {
        return (String) getValue(LB_ADDRESS, Expr.STRING, false);
    }

    private ArrayList<String> getList(Expr expr) throws Exception, ClassCastException {
        if (expr == null) {
            throw new IllegalArgumentException("expression not defined!");
        }

        ArrayList<String> result = new ArrayList<String>(0);

        ListExpr le = (ListExpr) expr;
        String s = null;

        for (int i = 0; i < le.size(); i++) {
            s = getString(le.sub(i));

            if (s.length() > 0) {
                result.add(s);
            }
        }

        return result;
    }

    public String getMaxOutputSandboxSize() throws Exception {
        return "" + getValue(MAX_OUTPUT_SANDBOX_SIZE, Expr.REAL, false);
    }

    public String getMWVersion() throws Exception {
        return (String) getValue(MWVERSION, Expr.STRING, false);
    }

    public String getMyProxyServer() throws Exception {
        return (String) getValue(MYPROXYSERVER, Expr.STRING, false);
    }

    /*
     * Return the file name of a path (expressed in Unix-like or Windows
     * format). If path has no name return an empty string, if the path has
     * wrong format return null.
     * 
     * @param path the string corresponding to the path
     */
    private String getName(String path) {
        int indexBackSlash = path.lastIndexOf("\\");
        int indexSlash = path.lastIndexOf("/");

        if ((path.lastIndexOf("\\") != -1) && (path.lastIndexOf("/") != -1)) {
            return null;
        } else {
            String name1 = "";
            String name2 = "";
            int prefixLength = getPrefixLength(path);

            if (indexBackSlash < prefixLength) {
                name1 = path.substring(prefixLength);
            } else {
                name1 = path.substring(indexBackSlash + 1);
            }

            prefixLength = getPrefixLength(path);

            if (indexSlash < prefixLength) {
                name2 = path.substring(prefixLength);
            } else {
                name2 = path.substring(indexSlash + 1);
            }

            return (name1.length() < name2.length()) ? name1 : name2;
        }
    }

    public int getNodeNumber() throws Exception {
        Integer value = (Integer) getValue(CPUNUMBER, Expr.INTEGER, false);

        if (value != null) {
            if (value <= 0) {
                throw new Exception(CPUNUMBER + ": the value must be >=1");
            } else {
                return value.intValue();
            }
        } else {
            value = (Integer) getValue(NODENUMBER, Expr.INTEGER, false);

            if (value != null) {
                if (value <= 0) {
                    throw new Exception(NODENUMBER + ": the value must be >=1");
                } else {
                    return value.intValue();
                }
            } else {
                return 1;
            }
        }
    }

    public ArrayList<OutputDataRecord> getOutputData() throws Exception {
        Expr expr = attributes.get(OUTPUTDATA.toLowerCase());
        if (expr == null) {
            return null;
        }

        ArrayList<OutputDataRecord> outputDataAttribute = new ArrayList<OutputDataRecord>(0);

        if (expr instanceof ListExpr) {
            Iterator iteratorExpr = ((ListExpr) expr).iterator();
            RecordExpr jdlRecExpr = null;
            Expr exprTmp = null;
            OutputDataRecord outputDataRecord = null;

            while (iteratorExpr.hasNext()) {
                outputDataRecord = new OutputDataRecord();
                jdlRecExpr = (RecordExpr) iteratorExpr.next();

                exprTmp = jdlRecExpr.lookup(OUTPUTDATA_OUTPUTFILE);
                if (exprTmp == null) {
                    throw new Exception("OutputFile is mandatory for the OutputData jdl attribute.");
                }
                outputDataRecord.setODOutputFile(exprTmp.stringValue().trim());

                exprTmp = jdlRecExpr.lookup(OUTPUTDATA_LOGICALFILENAME);
                outputDataRecord.setODLogicalFilename((exprTmp != null) ? exprTmp.stringValue().trim() : "");

                exprTmp = jdlRecExpr.lookup(OUTPUTDATA_STORAGEELEMENT);
                outputDataRecord.setODStorageElement((exprTmp != null) ? exprTmp.stringValue().trim() : "");

                outputDataAttribute.add(outputDataRecord);
            }
        } else {
            throw new Exception("Bad format for 'OutputData' jdl attribute.");
        }
        return outputDataAttribute;
    }

    public ArrayList<String> getOutputSandbox() throws Exception {
        ArrayList<String> list = (ArrayList<String>) getValue(OUTPUTSANDBOX, Expr.LIST, false);

        try {
            checkSandbox(list);
        } catch (Exception ex) {
            throw new Exception(OUTPUTSANDBOX + ": " + ex.getMessage());
        }

        return list;
    }

    public String getOutputSandboxBaseDestURI() throws Exception {
        String uri = (String) getValue(OUTPUTSANDBOX_BASEDESTURI, Expr.STRING, false);

        try {
            checkBaseURL(uri);
        } catch (Exception ex) {
            throw new Exception(OUTPUTSANDBOX_BASEDESTURI + ": " + ex.getMessage());
        }

        return uri;
    }

    public ArrayList<String> getOutputSandboxDestURI() throws Exception {
        ArrayList<String> uriList = (ArrayList<String>) getValue(OUTPUTSANDBOX_DESTURI, Expr.LIST, false);

        if (uriList != null) {
            for (String uri : uriList) {
                checkBaseURL(uri);
            }
        }
        return uriList;
    }

    public URI getPerusalFilesDestURI() throws Exception {
        String value = (String) getValue(PERUSAL_FILESDESTURI, Expr.STRING, isPerusalFileEnable());

        if (value == null) {
            throw new Exception(PERUSAL_FILESDESTURI + ": URI not defined!");
        }

        if (value.startsWith("gsiftp://") || value.startsWith("https://")) {
            return new URI(value);
        } else {
            throw new Exception(PERUSAL_FILESDESTURI + ": wrong protocol (just gridFTP or https URIs are supported)");
        }
    }

    public URI getPerusalListFileURI() throws Exception {
        String value = (String) getValue(PERUSAL_LISTFILEURI, Expr.STRING, isPerusalFileEnable());

        if (value == null) {
            throw new Exception(PERUSAL_LISTFILEURI + ": URI not defined!");
        }

        if (value.startsWith("gsiftp://")) {
            return new URI(value);
        } else {
            throw new Exception(PERUSAL_LISTFILEURI + ": wrong protocol (just gridFTP URIs are supported)");
        }
    }

    public int getPerusalTimeInterval() throws Exception {
        Integer value = (Integer) getValue(PERUSAL_TIMEINTERVAL, Expr.INTEGER, isPerusalFileEnable());

        if (value != null) {
            if (value <= 0) {
                throw new Exception(PERUSAL_TIMEINTERVAL + ": the value must be >=1");
            } else {
                return value.intValue();
            }
        }

        return -1;
    }

    /**
     * This method is used by static String getName(String path)
     */
    private int getPrefixLength(String path) {
        if (path.length() == 0) {
            return 0;
        }

        return (path.charAt(0) == '/') ? 1 : 0;
    }

    public String getPrologue() throws Exception {
        return checkAttributeFile(PROLOGUE, false, true);
    }

    public String getPrologueArguments() throws Exception {
        return (String) getValue(PROLOGUE_ARGUMENTS, Expr.STRING, false);
    }

    public String getQueueName() throws Exception {
        String value = (String) getValue(QUEUENAME, Expr.STRING, true);

        if (value == null) {
            throw new Exception(QUEUENAME + ": queue name not defined!");
        }

        return value;
    }

    public String getSequenceCode() throws Exception {
        return (String) getValue(LB_SEQUENCE_CODE, Expr.STRING, false);
    }

    public int getSMPGranularity() throws Exception {
        Integer value = (Integer) getValue(SMPGRANULARITY, Expr.INTEGER, false);

        if (value != null) {
            if (value <= 0) {
                throw new Exception(SMPGRANULARITY + ": the value must be >=1");
            } else {
                return value.intValue();
            }
        }

        return -1;
    }

    public String getStandardError() throws Exception {
        return checkAttributeFile(STDERROR, false, false);
    }

    public String getStandardInput() throws Exception {
        return checkAttributeFile(STDINPUT, false, true);
    }

    public String getStandardOutput() throws Exception {
        return checkAttributeFile(STDOUTPUT, false, false);
    }

    private String getString(Expr expr) throws Exception, ClassCastException {
        if (expr == null) {
            throw new Exception("expression not defined!");
        }

        Constant co = (Constant) expr;
        String result = co.stringValue();

        if (result != null) {
            if (result.startsWith("\"") && result.endsWith("\"")) {
                result = result.substring(1, result.length() - 1);
            } else if (result.length() == 0) {
                result = null;
            }
        }

        return result.trim();
    }

    public String getTokenURL() throws Exception {
        return (String) getValue(REALLYRUNNINGTOKEN, Expr.STRING, false);
    }

    public String getType() throws Exception {
        String value = (String) getValue(TYPE, Expr.STRING, false);

        if (value != null && !value.equalsIgnoreCase("job")) {
            throw new Exception(TYPE + " \"" + value + "\" not allowed");
        }

        if (value == null) {
            value = "job";
        }

        return value;
    }

    /*
     * protected String getList(String attrName, Expr value, int exprType)
     * throws NoSuchFieldException, IllegalArgumentException { Vector vect = new
     * Vector(); if (value == null) { throw new NoSuchFieldException(attrName +
     * ": attribute has not been set"); } else if (value.type == Expr.LIST) { //
     * It's a list of elements, put them in a vector ListExpr le = (ListExpr)
     * value; for (int i = 0; i < le.size(); i++) {
     * vect.add(getConstant(attrName, le.sub(i), exprType)); } } else {
     * vect.add(getConstant(attrName, value, exprType)); } return vect; }
     */

    public String getURIWithoutDN(String uri) {
        String uriWithoutDN = null;
        if ((uri != null) && (!"".equals(uri)) && (uri.indexOf("?DN=") != -1)) {
            uriWithoutDN = uri.substring(0, uri.indexOf("?DN="));
            // see https://savannah.cern.ch/bugs/?59426
            if (uriWithoutDN.charAt(uriWithoutDN.length() - 1) == '\\') {
                uriWithoutDN = uriWithoutDN.substring(0, uriWithoutDN.length() - 1);
            }
            if (uriWithoutDN.charAt(uriWithoutDN.length() - 1) == '\\') {
                uriWithoutDN = uriWithoutDN.substring(0, uriWithoutDN.length() - 1);
            }
        }
        return uriWithoutDN;
    }

    private Object getValue(String attrName, int exprType, boolean isMandatory) throws Exception {
        if (attrName == null) {
            throw new Exception("attribute name not defined!");
        }

        if (!attributes.containsKey(attrName.toLowerCase())) {
            if (isMandatory) {
                throw new Exception("mandatory attribute \"" + attrName + "\" not defined!");
            } else {
                return null;
            }
        }

        Expr expr = attributes.get(attrName.toLowerCase());
        if (expr instanceof Constant) {
            Constant co = (Constant) expr;

            if (exprType == Expr.INTEGER) {
                try {
                    return new Integer(co.intValue()); // Integer Value
                } catch (Throwable ex) {
                    throw new Exception("wrong type (expected type: integer)");
                }
            } else if (exprType == Expr.BOOLEAN) {
                try {
                    return new Boolean(co.isTrue()); // Boolean Value
                } catch (Throwable ex) {
                    throw new Exception("wrong type (expected type: boolean)");
                }
            } else if (exprType == Expr.REAL) {
                return new Double(co.realValue()); // Real Value
            } else if (exprType == Expr.STRING) {
                String result = co.stringValue();

                if (result != null) {
                    if (result.startsWith("\"") && result.endsWith("\"")) {
                        result = result.substring(1, result.length() - 1);
                    } else if (result.length() == 0) {
                        result = null;
                    }
                }

                return result;
            } else {
                throw new IllegalArgumentException(attrName + ": unexpected type found.");
            }
        }

        if (expr instanceof ListExpr) {
            return getList(expr);
        }

        if (expr != null) {
            String value = expr.toString();
            if (value.length() > 0) {
                if (value.charAt(0) == '"') {
                    value = value.substring(1, value.length() - 1);
                }
            }

            return value;
        }

        throw new Exception("attribute " + attrName + ": " + expr.getClass().getName() + " not supported");
    }

    public String getVirtualOrganisation() throws Exception {
        return (String) getValue(VIRTUALORGANISATION, Expr.STRING, false);
    }

    public String getWMSHostName() throws Exception {
        return (String) getValue(WMS_HOSTNAME, Expr.STRING, false);
    }

    public boolean isPerusalFileEnable() throws Exception {
        Boolean value = (Boolean) getValue(PERUSAL_FILEENABLE, Expr.BOOLEAN, false);

        if (value == null) {
            value = false;
        }

        return value.booleanValue();
    }

    public boolean isWholeNodes() throws Exception {
        Boolean value = (Boolean) getValue(WHOLENODES, Expr.BOOLEAN, false);

        if (value == null) {
            value = false;
        }

        return value.booleanValue();
    }

    private void parse() throws Exception {
        jdl = jdl.trim();
        int ind = 0;
        ind = jdl.indexOf("\\");

        while (ind != -1) {
            jdl = jdl.substring(0, ind) + ("\\\\") + jdl.substring(ind + 1);
            ind = jdl.indexOf("\\", ind + 2);
        }

        ind = jdl.indexOf("\\\"");
        while (ind != -1) {
            jdl = jdl.substring(0, ind) + jdl.substring(ind + 1);
            ind = jdl.indexOf("\\\"", ind + 1);
        }

        if (!jdl.startsWith("[")) {
            jdl = "[ " + jdl + "]";
        }

        ClassAdParser cp = new ClassAdParser(jdl);
        Expr expr = cp.parse();

        if (expr == null) {
            throw new Exception("Unable to parse: doesn't seem to be a valid Expression");
        } else if (expr.type != Expr.RECORD) {
            throw new Exception("Unable to parse: the parsed expression is not a ClassAd");
        }

        RecordExpr jdlExpr = (RecordExpr) expr;

        String type = null;
        expr = jdlExpr.lookup(TYPE);

        if (expr == null) {
            type = "Job";
        } else if (!expr.isConstant()) {
            throw new Exception(TYPE + " \"" + expr.stringValue() + "\" not allowed");
        } else {
            type = expr.stringValue();
        }

        if (type.equalsIgnoreCase("Job")) {
            String jobType = null;

            expr = jdlExpr.lookup(JOBTYPE);

            if (expr == null) {
                jobType = "Normal";
            } else {
                jobType = expr.stringValue();
            }

            if (!jobType.equalsIgnoreCase("Normal")) {
                throw new Exception(JOBTYPE + " \"" + jobType + "\" not allowed");
            }

            @SuppressWarnings("unchecked")
            Iterator<AttrName> it = (Iterator<AttrName>) jdlExpr.attributes();

            String key = null;
            // String value = null;

            final char[] notAllowed = { ' ', ':', '#', '@', '[', ']', '+', '*', '$', '%', '!', '?', '~' };

            while (it.hasNext()) {
                key = it.next().toString().toLowerCase();

                for (int i = 0; i < notAllowed.length; i++) {
                    if (key.indexOf(notAllowed[i]) != -1) {
                        throw new Exception(key + ": Not allowed char '" + notAllowed[i] + "' found");
                    }
                    expr = jdlExpr.lookup(key);

                    if (expr == null) {
                        throw new Exception(key + ": none expression found");
                    } /*else if (!expr.isConstant()) {
                        throw new Exception(key + ": not constant expression found");
                    }*/
                }
                attributes.put(key, expr);
            }
        } else {
            throw new Exception(TYPE + " \"" + type + "\" not allowed");
        }
    }
/*
    public void print() {
        for (String k : attributes.keySet()) {
            Object obj = attributes.get(k);

            System.out.println(k + " = " + obj);
        }
    }
*/
}
