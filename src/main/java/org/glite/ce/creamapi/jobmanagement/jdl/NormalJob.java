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

package org.glite.ce.creamapi.jobmanagement.jdl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.glite.ce.commonj.utils.CEUtils;
import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.jdl.Jdl;
import org.glite.jdl.JobAd;

import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

public final class NormalJob {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(NormalJob.class.getName());

    private static String getAttributeValue(JobAd jab, String attribute) {
        String value = null;
        Expr result = jab.lookup(attribute);

        if (result != null) {
            value = result.toString();
            if (value.length() > 0) {
                if (value.charAt(0) == '"') {
                    value = value.substring(1, value.length() - 1);
                }
            }
        }
        return value;
    }

    private static String getAttributeValue(JobAd jab, String attribute, String fault) throws IllegalArgumentException, Exception {
        if (jab == null) {
            throw new IllegalArgumentException("JobAd not defined!");
        }

        if (attribute == null) {
            throw new IllegalArgumentException("attribute not defined!");
        }

        String result = getAttributeValue(jab, attribute);

        if (result == null) {
            throw new Exception(fault);
        }

        return result;
    }

    private static String getBaseURL(JobAd jab, String attribute) throws IllegalArgumentException, Exception {
        if (attribute == null) {
            return null;
        }

        if (jab == null) {
            throw new IllegalArgumentException("JobAd not defined!");
        }

        String uriValue = getAttributeValue(jab, attribute);
        if (uriValue != null) {
            String uriValueWithoutDN = uriValue;
            if (uriValue.indexOf("?DN=") != -1) {
                uriValueWithoutDN = CEUtils.getURIWithoutDN(uriValue);
            }
            URI uri = null;
            try {
                uri = new URI(uriValueWithoutDN);
            } catch (URISyntaxException use) {
                logger.error("Illegal character for the URI: " + uriValue, use);
                throw new IllegalArgumentException("Illegal character for the URI: " + uriValue);
            }
            String scheme = uri.getScheme();

            if (scheme == null || (!scheme.startsWith("gsiftp") && !scheme.startsWith("file") && !scheme.startsWith("http") && !scheme.startsWith("https"))) {
                throw new IllegalArgumentException("invalid argument: the URI \"" + uriValue + "\" doesn't present an allowed scheme (gsiftp, file, http, https)");
            }

            return uriValue;
        }

        return null;
    }

    private static List<String> getBaseURLs(JobAd jab, String sb) throws IllegalArgumentException, Exception {
        if (sb == null) {
            return null;
        }

        if (jab == null) {
            throw new IllegalArgumentException("JobAd not defined!");
        }

        ArrayList<String> uriArray = new ArrayList<String>(0);

        Expr expression = jab.lookup(sb);
        if (expression == null) {
            return null;
        }

        if (expression instanceof ListExpr) {
            Iterator iter2 = ((ListExpr) expression).iterator();

            String uriValue = null;
            String uriValueWithoutDN = null;
            URI uri = null;
            String scheme = null;

            while (iter2.hasNext()) {
                uriValue = ((Constant) iter2.next()).stringValue();
                if (uriValue != null) {
                    uriValueWithoutDN = uriValue;
                    if (uriValue.indexOf("?DN=") != -1) {
                        uriValueWithoutDN = CEUtils.getURIWithoutDN(uriValue);
                    }
                    try {
                        uri = new URI(uriValueWithoutDN);
                    } catch (URISyntaxException use) {
                        logger.error("Illegal character for the URI: " + uriValue, use);
                        throw new IllegalArgumentException("Illegal character for the URI: " + uriValue);
                    }
                    scheme = uri.getScheme();

                    if (scheme == null || (!scheme.startsWith("gsiftp") && !scheme.startsWith("file") && !scheme.startsWith("http") && !scheme.startsWith("https"))) {
                        throw (new IllegalArgumentException("invalid argument: the URI \"" + uriValue + "\" doesn't present an allowed scheme (gsiftp, file, http, https)"));
                    }

                    uriArray.add(uriValue);
                }
            }
        } else {
            String uriValue = ((Constant) expression).stringValue();

            if (uriValue != null) {
                String uriValueWithoutDN = uriValue;
                if (uriValue.indexOf("?DN=") != -1) {
                    uriValueWithoutDN = CEUtils.getURIWithoutDN(uriValue);
                }
                URI uri = null;
                try {
                    uri = new URI(uriValueWithoutDN);
                } catch (URISyntaxException use) {
                    logger.error("Illegal character for the URI: " + uriValue, use);
                    throw new IllegalArgumentException("Illegal character for the URI: " + uriValue);
                }
                String scheme = uri.getScheme();

                if (scheme == null || (!scheme.startsWith("gsiftp") && !scheme.startsWith("file") && !scheme.startsWith("http") && !scheme.startsWith("https"))) {
                    throw (new IllegalArgumentException("invalid argument: the URI \"" + uriValue + "\" doesn't present an allowed scheme (gsiftp, file, http, https)"));
                }
                uriArray.add(uriValue);
            }
        }

        return uriArray;
    }

    private static List<String> getFiles(JobAd jab, String sb) throws IllegalArgumentException {
        if (sb == null) {
            return null;
        }

        if (jab == null) {
            throw new IllegalArgumentException("JobAd not defined!");
        }

        ArrayList<String> fileArray = new ArrayList<String>(0);

        Expr expression = jab.lookup(sb);
        if (expression == null) {
            return null;
        }

        if (expression instanceof ListExpr) {
            Iterator iter2 = ((ListExpr) expression).iterator();

            while (iter2.hasNext()) {
                String uri = ((Constant) iter2.next()).stringValue();

                fileArray.add(uri);
            }
        } else {
            String uri = ((Constant) expression).stringValue();

            fileArray.add(uri);
        }

        return fileArray;
    }

    public static Job makeJob(String jdl) throws Exception {
        if (jdl == null) {
            throw new IllegalArgumentException("JDL not defined!");
        }

        return makeJob(new JobAd(jdl));
    }

    private static ArrayList<OutputDataRecord> getOutputData(JobAd jab, String sb) throws IllegalArgumentException {
        if (sb == null) {
            return null;
        }

        if(jab == null) {
            throw new IllegalArgumentException("JobAd not defined!");
        }
        Expr expression = jab.lookup(sb);

        if (expression == null) {
            return null;
        }

        ArrayList<OutputDataRecord> outputDataAttribute = new ArrayList<OutputDataRecord>(0);

        if (expression instanceof ListExpr) {
            Iterator iteratorExpr = ((ListExpr) expression).iterator();
            RecordExpr jdlRecExpr    = null;
            Expr exprTmp             = null;
            OutputDataRecord outputDataRecord = null;

            while (iteratorExpr.hasNext()) {
                outputDataRecord = new OutputDataRecord();
                jdlRecExpr = (RecordExpr)iteratorExpr.next();
                exprTmp = jdlRecExpr.lookup(Jdl.OD_OUTPUT_FILE);
                if (exprTmp == null) {
                   throw new IllegalArgumentException("OutputFile is mandatory for the OutputData jdl attribute.");
                }
                outputDataRecord.setODOutputFile(exprTmp.stringValue());
                exprTmp = jdlRecExpr.lookup(Jdl.OD_LOGICAL_FILENAME);
                outputDataRecord.setODLogicalFilename((exprTmp != null) ? exprTmp.stringValue() : "");
                exprTmp = jdlRecExpr.lookup(Jdl.OD_STORAGE_ELEMENT);
                outputDataRecord.setODStorageElement((exprTmp != null) ? exprTmp.stringValue() : "");

                outputDataAttribute.add(outputDataRecord);
            }
        } else {
            throw new IllegalArgumentException("Bad format for 'OutputData' jdl attribute.");
        }
        return outputDataAttribute;
    }

    public static Job makeJob(JobAd jobAd) throws Exception {
        if (jobAd == null) {
            throw new IllegalArgumentException("JobAd not defined!");
        }

        jobAd.setLocalAccess(false);
        jobAd.checkAll(new String[] { Jdl.EXECUTABLE, Jdl.QUEUENAME, Jdl.JOBTYPE, "BatchSystem" });

        Job job = new Job();
        job.setLRMSAbsLayerJobId("N/A");
        job.setLRMSJobId("N/A");
        job.setWorkerNode("N/A");

        // if (creamURL == null) {
        // setCREAMJobId(getId());
        // } else {
        // setCREAMJobId(creamURL + (creamURL.endsWith("/") ? "" : "/")
        // + getId());
        // }

        // setId(getAttributeValue(job, Jdl.JOBID));
        job.setType(getAttributeValue(jobAd, Jdl.JOBTYPE));
        job.setBatchSystem(getAttributeValue(jobAd, "BatchSystem"));
        job.setQueue(getAttributeValue(jobAd, Jdl.QUEUENAME));
        job.setVirtualOrganization(getAttributeValue(jobAd, Jdl.VIRTUAL_ORGANISATION));
        job.setStandardError(getAttributeValue(jobAd, Jdl.STDERROR));
        job.setStandardInput(getAttributeValue(jobAd, Jdl.STDINPUT));
        job.setStandardOutput(getAttributeValue(jobAd, Jdl.STDOUTPUT));
        job.setExecutable(getAttributeValue(jobAd, Jdl.EXECUTABLE));
        job.setCeRequirements(getAttributeValue(jobAd, Jdl.CE_REQUIREMENTS));
        job.setInputSandboxBaseURI(getBaseURL(jobAd, "InputSandboxBaseURI"));
        job.setOutputSandboxBaseDestURI(getBaseURL(jobAd, "OutputSandboxBaseDestURI"));
        job.setOutputSandboxDestURI(getBaseURLs(jobAd, "OutputSandboxDestURI"));
        job.setInputFiles(getFiles(jobAd, Jdl.INPUTSB));
        job.setOutputFiles(getFiles(jobAd, Jdl.OUTPUTSB));
        job.setHlrLocation(getAttributeValue(jobAd, Jdl.HLR_LOCATION));
        job.setMyProxyServer(getAttributeValue(jobAd, Jdl.MYPROXY));
        job.setPrologue(getAttributeValue(jobAd, "Prologue"));
        job.setPrologueArguments(getAttributeValue(jobAd, "PrologueArguments"));
        job.setEpilogue(getAttributeValue(jobAd, "Epilogue"));
        job.setEpilogueArguments(getAttributeValue(jobAd, "EpilogueArguments"));
        job.setSequenceCode(getAttributeValue(jobAd, "LB_sequence_code"));
        job.setTokenURL(getAttributeValue(jobAd, "ReallyRunningToken"));

        String mwVersion = getAttributeValue(jobAd, Jdl.MW_VERSION);
        if (mwVersion != null) {
            job.addExtraAttribute(Jdl.MW_VERSION, mwVersion);
        }

        String lbAddress = getAttributeValue(jobAd, Jdl.LB_ADDRESS);
        if (lbAddress != null) {
            job.addVolatileProperty(Job.LB_ADDRESS, lbAddress);
        }

        ArrayList<OutputDataRecord> outputData = getOutputData(jobAd, Jdl.OUTPUTDATA);
        if (outputData != null){
            job.addVolatileProperty(Job.OUTPUT_DATA, outputData);
        }

        String wmsHostname = getAttributeValue(jobAd, Jdl.WMS_HOSTNAME);
        if (wmsHostname != null) {
            job.addVolatileProperty(Job.WMS_HOSTNAME, wmsHostname);
        }

        String maxOutputSandboxSize = getAttributeValue(jobAd, Jdl.MAX_OUTPUT_SANDBOX_SIZE);
        if (maxOutputSandboxSize != null) {
            job.addVolatileProperty(Job.MAX_OUTPUT_SANDBOX_SIZE, maxOutputSandboxSize);
        }

        if (job.getOutputFiles().size() > 0) {
            if (job.getOutputSandboxBaseDestURI() != null && job.getOutputSandboxBaseDestURI().length() > 0) {
                if (job.getOutputSandboxDestURI().size() > 0) {     
                    throw new Exception("the OutputSandboxDestURI and OutputSandboxBaseDestURI attributes cannot be specified together in the same JDL");
                }
            } else {
                if (job.getOutputSandboxDestURI().size() == 0) {            
                    throw new Exception("the OutputSandbox attribute requires the specification in the same JDL of one of the following attributes: OutputSandboxDestURI or OutputSandboxBaseDestURI");
                } else if (job.getOutputSandboxDestURI().size() != job.getOutputFiles().size()) {
                    throw new Exception("the OutputSandbox and OutputSandboxDestURI attributes must have the same cardinality");
                }
            }
        }

        String args = getAttributeValue(jobAd, Jdl.ARGUMENTS);
        if (args != null && args.length() > 0) {
            List<String> argsList = new ArrayList<String>(1);
            argsList.add(args);
            
            job.setArguments(argsList);
        }

        String edg_jobid = getAttributeValue(jobAd, "edg_jobid");
        if (edg_jobid == null) {
            edg_jobid = "N/A";
        }
        job.setGridJobId(edg_jobid);

        if (job.getType() == null) {
            job.setType("normal");
        }

        Expr expression = jobAd.lookup(Jdl.ENVIRONMENT);

        if (expression != null && expression instanceof ListExpr) {
            Iterator item = ((ListExpr) expression).iterator();

            while (item.hasNext()) {
                String tmps = ((Constant) item.next()).stringValue();
                String[] tokens = tmps.split("=", 2);
                job.addEnvironmentAttribute(tokens[0].trim(), tokens[1].trim());
            }
        }

        job.setNodeNumber(1);

        expression = jobAd.lookup(Jdl.CPUNUMB);
        if (expression != null) {
            if (expression.type != Expr.INTEGER || expression.intValue() <= 0) {
                 throw new Exception("wrong value for " + Jdl.CPUNUMB + ": it must be >=1");
            }

            job.setNodeNumber(expression.intValue());
        } else {
            expression = jobAd.lookup(Jdl.NODENUMB);
            
            if (expression != null) {
                if (expression.type != Expr.INTEGER || expression.intValue() <= 0) {
                    throw new Exception("wrong value for " + Jdl.NODENUMB + ": it must be >=1");
                }

                job.setNodeNumber(expression.intValue());
            }
        }

        boolean wholeNodes = false;
        expression = jobAd.lookup(Jdl.WHOLE_NODES);
        if (expression != null && expression.type == Expr.BOOLEAN && expression.isTrue()) {
            wholeNodes = true;
        }

        job.addExtraAttribute(Jdl.WHOLE_NODES, Boolean.toString(wholeNodes));

        expression = jobAd.lookup(Jdl.SMP_GRANULARITY);
        if (expression != null) {
            if (expression.type != Expr.INTEGER || expression.intValue() <= 0) {
                 throw new Exception("wrong value for " + Jdl.SMP_GRANULARITY + ": it must be >=1");
            }

            job.addExtraAttribute(Jdl.SMP_GRANULARITY, "" + expression.intValue());
        }

        expression = jobAd.lookup(Jdl.HOST_NUMBER);
        if (expression != null) {
            if (expression.type != Expr.INTEGER || expression.intValue() <= 0) {
                 throw new Exception("wrong value for " + Jdl.HOST_NUMBER + ": it must be >=1");
            }

            job.addExtraAttribute(Jdl.HOST_NUMBER, "" + expression.intValue());
        }

        if (!wholeNodes && job.containsExtraAttribute(Jdl.SMP_GRANULARITY) && job.containsExtraAttribute(Jdl.HOST_NUMBER)) {
            throw new Exception("the SMPGranularity and HostNumber attributes cannot be specified together when WholeNodes=false");
        }

        expression = jobAd.lookup("PerusalFileEnable");
        if (expression != null && expression.type == Expr.BOOLEAN && expression.isTrue()) {
            expression = jobAd.lookup("PerusalTimeInterval");
            if (expression != null && expression instanceof Constant) {
                job.setPerusalTimeInterval(expression.intValue());
            } else {
                job.setPerusalTimeInterval(5);
            }
            job.setPerusalFilesDestURI(getAttributeValue(jobAd, "PerusalFilesDestURI"));
            job.setPerusalListFileURI(getAttributeValue(jobAd, "PerusalListFileURI"));
        } else {
            job.setPerusalFilesDestURI(null);
        }

        return job;
    }
}
