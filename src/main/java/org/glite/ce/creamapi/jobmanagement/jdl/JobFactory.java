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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.glite.ce.creamapi.jobmanagement.Job;

public class JobFactory {

    private static boolean checkFileSandbox(String filename, List<String> sandbox) {
        if (filename != null && !(filename.startsWith("$") || filename.startsWith(File.separator)) && new File(filename).getName().equals(filename)) {
            boolean found = false;

            for (String file : sandbox) {
                if (file.endsWith(filename)) {
                    found = true;
                }
            }

            return found;
        } else {
            return true;
        }
    }

    public static Job makeJob(String classad) throws Exception {
        JDL jdl = new JDL(classad);

        Job job = new Job();
        job.setLRMSAbsLayerJobId("N/A");
        job.setLRMSJobId("N/A");
        job.setWorkerNode("N/A");
        job.setType(jdl.getJobType());
        job.setExecutable(jdl.getExecutable());
        job.setBatchSystem(jdl.getBatchSystem());
        job.setQueue(jdl.getQueueName());
        job.setVirtualOrganization(jdl.getVirtualOrganisation());
        job.setNodeNumber(jdl.getNodeNumber());
        job.setGridJobId(jdl.getEDGJobId());
        job.setEnvironment(jdl.getEnvironment());
        job.setArguments(jdl.getArguments());
        job.setCeRequirements(jdl.getCERequirements());
        job.setHlrLocation(jdl.getHLRLocation());
        job.setMyProxyServer(jdl.getMyProxyServer());
        job.setPrologue(jdl.getPrologue());
        job.setPrologueArguments(jdl.getPrologueArguments());
        job.setEpilogue(jdl.getEpilogue());
        job.setEpilogueArguments(jdl.getEpilogueArguments());
        job.setSequenceCode(jdl.getSequenceCode());
        job.setTokenURL(jdl.getTokenURL());
        job.setInputFiles(jdl.getInputSandbox());
        job.setOutputFiles(jdl.getOutputSandbox());
        job.setInputSandboxBaseURI(jdl.getInputSandboxBaseURI());
        job.setOutputSandboxBaseDestURI(jdl.getOutputSandboxBaseDestURI());
        job.setOutputSandboxDestURI(jdl.getOutputSandboxDestURI());
        job.setStandardInput(jdl.getStandardInput());
        job.setStandardOutput(jdl.getStandardOutput());
        job.setStandardError(jdl.getStandardError());

        /*
         * INPUTSANDBOX / EXECUTABLE cross- check
         */
        if (!checkFileSandbox(job.getExecutable(), job.getInputFiles())) {
            throw new Exception(JDL.INPUTSANDBOX + ": the executable file is not listed in the InputSandbox");
        }

        /*
         * INPUTSANDBOX / STDINPUT cross-check
         */
        if (!checkFileSandbox(job.getStandardInput(), job.getInputFiles())) {
            throw new Exception(JDL.INPUTSANDBOX + ": the stdInput file is not listed in the InputSandbox");
        }

        /*
         * STDERROR / STDOUTPUT cross-check
         */
        if (job.getStandardOutput() != null && job.getStandardError() != null) {
            File stdOutput = new File(job.getStandardOutput());
            File stdError = new File(job.getStandardError());

            if (stdOutput.getName().equals(stdError.getName()) && !stdOutput.getPath().equals(stdError.getPath())) {
                throw new Exception(JDL.STDOUTPUT + " & " + JDL.STDERROR + ": the values specified for StdError and StdOutput are the same but with different paths");
            }
        }

        if (job.getOutputFiles().size() > 0) {
            if (job.getOutputSandboxBaseDestURI() == null && job.getOutputSandboxDestURI().size() == 0) {
                // job.setOutputSandboxBaseDestURI("gsiftp://localhost");
                throw new Exception(JDL.OUTPUTSANDBOX + ": one of the following attributes OutputSandboxDestURI or OutputSandboxBaseDestURI must be specified");
            }

            /*
             * OUTPUTSANDBOX / OUTPUTSANDBOX_BASEDESTURI cross-check
             */
            if (job.getOutputSandboxBaseDestURI() == null && job.getOutputFiles().size() != job.getOutputSandboxDestURI().size()) {
                throw new Exception(JDL.OUTPUTSANDBOX_DESTURI + ": the OutputSandboxDestURI list must have the same cardinality as the OutputSandbox list");
            }

            /*
             * OUTPUTSANDBOX_DESTURI / OUTPUTSANDBOX_BASEDESTURI cross-check
             */
            if (job.getOutputSandboxBaseDestURI() != null && job.getOutputSandboxDestURI().size() > 0) {
                throw new Exception(JDL.OUTPUTSANDBOX_DESTURI + " & " + JDL.OUTPUTSANDBOX_BASEDESTURI + ": one (and only one) among the OutputSandboxDestURI and OutputSandboxBaseDestURI attributes is allowed");
            }
        }

        boolean isWholeNodes = jdl.isWholeNodes();
        job.addExtraAttribute(JDL.WHOLENODES, "" + isWholeNodes);

        int smpGranularity = jdl.getSMPGranularity();
        if (smpGranularity > 0) {
            job.addExtraAttribute(JDL.SMPGRANULARITY, "" + smpGranularity);
        }

        int hostNumber = jdl.getHostNumber();
        if (hostNumber > 0) {
            job.addExtraAttribute(JDL.HOSTNUMBER, "" + hostNumber);
        }

        if (!isWholeNodes && smpGranularity > 0 && hostNumber > 0) {
            throw new Exception("the SMPGranularity and HostNumber attributes cannot be specified together when WholeNodes=false");
        }

        if (jdl.isPerusalFileEnable()) {
            job.setPerusalTimeInterval(jdl.getPerusalTimeInterval());
            job.setPerusalFilesDestURI(jdl.getPerusalFilesDestURI().toString());
            job.setPerusalListFileURI(jdl.getPerusalListFileURI().toString());
        } else {
            job.setPerusalFilesDestURI(null);
        }

        String mwVersion = jdl.getMWVersion();
        if (mwVersion != null) {
            job.addExtraAttribute(JDL.MWVERSION, mwVersion);
        }

        String lbAddress = jdl.getLBAddress();
        if (lbAddress != null) {
            job.addVolatileProperty(Job.LB_ADDRESS, lbAddress);
        }

        String wmsHostname = jdl.getWMSHostName();
        if (wmsHostname != null) {
            job.addVolatileProperty(Job.WMS_HOSTNAME, wmsHostname);
        }

        String maxOutputSandboxSize = jdl.getMaxOutputSandboxSize();
        if (maxOutputSandboxSize != null) {
            job.addVolatileProperty(Job.MAX_OUTPUT_SANDBOX_SIZE, maxOutputSandboxSize);
        }

        ArrayList<OutputDataRecord> outputData = jdl.getOutputData();
        if (outputData != null) {
            job.addVolatileProperty(Job.OUTPUT_DATA, outputData);
        }

        return job;
    }

    public static void main(String[] arg) {
        String jdl = "";

        FileReader in = null;
        try {
            in = new FileReader("/home/zangrand/test-isb.jdl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        char[] buffer = new char[1024];
        int n = 1;

        while (n > 0) {
            try {
                n = in.read(buffer, 0, buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (n > 0) {
                jdl += new String(buffer, 0, n);
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println(jdl);
        
        try {
            makeJob(jdl);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
