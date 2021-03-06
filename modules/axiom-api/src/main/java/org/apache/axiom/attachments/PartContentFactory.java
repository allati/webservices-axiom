/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.attachments;

import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

/**
 * Factory for {@link PartContent} objects. There are different ways to store the content of a part
 * (backing file or backing array etc.). These different implementations should not be exposed to
 * the other layers of the code. The {@link PartContentFactory} helps maintain this abstraction, and
 * makes it easier to add new implementations.
 */
class PartContentFactory {
    
    private static int inflight = 0;  // How many attachments are currently being built.
    private static final String semaphore = "PartContentFactory.semaphore";
    
    private static final Log log = LogFactory.getLog(PartContentFactory.class);
    
    // Maximum number of threads allowed through createPart
    private static final int INFLIGHT_MAX = 4;
    
    // Constants for dynamic threshold 
    // Dynamic Threshold = availMemory / THRESHOLD_FACTOR
    private static final int THRESHOLD_FACTOR = 5;
    
    /**
     * Creates a {@link PartContent} object from a given input stream. The remaining parameters are
     * used to determine if the content should be stored in memory (byte buffers) or backed by a
     * file.
     * 
     * @param manager
     * @param in
     * @param isRootPart
     * @param thresholdSize
     * @param attachmentDir
     * @param messageContentLength
     * @return Part
     * @throws OMException
     *             if any exception is encountered while processing.
     */
    static PartContent createPartContent(LifecycleManager manager, InputStream in,
                    boolean isRootPart,
                    int thresholdSize,
                    String attachmentDir,
                    int messageContentLength
                    ) throws OMException {
        if(log.isDebugEnabled()){
            log.debug("Start createPart()");
            log.debug("  isRootPart=" + isRootPart);
            log.debug("  thresholdSize= " + thresholdSize);
            log.debug("  attachmentDir=" + attachmentDir);
            log.debug("  messageContentLength " + messageContentLength);
        }
        
        try {
            PartContent partContent;
            try {
                
                // Message throughput is increased if the number of threads in this
                // section is limited to INFLIGHT_MAX.  Allowing more threads tends to cause
                // thrashing while reading from the HTTP InputStream.  
                // Allowing fewer threads reduces the thrashing.  And when the remaining threads
                // are notified their input (chunked) data is available.
                // 
                // Note: the root part is at the beginning of the message and much smaller than attachments,
                // so don't wait on root parts.
                if (!isRootPart) {
                    synchronized(semaphore) {
                        if (inflight >= INFLIGHT_MAX) {
                            semaphore.wait();
                        }
                        inflight++;
                    }
                }
                // Get new threshold based on the current available memory in the runtime.
                // We only use the thresholds for non-root parts.
                if (!isRootPart && thresholdSize > 0) {     
                    thresholdSize = getRuntimeThreshold(thresholdSize, inflight);
                }

                
                if (isRootPart ||
                        thresholdSize <= 0 ||  
                        (messageContentLength > 0 && 
                                messageContentLength < thresholdSize)) {
                    // If the entire message is less than the threshold size, 
                    // keep it in memory.
                    // If this is the root part, keep it in memory.

                    // Get the bytes of the data without a lot 
                    // of resizing and GC.  The BAAOutputStream 
                    // keeps the data in non-contiguous byte buffers.
                    BAAOutputStream baaos = new BAAOutputStream();
                    BufferUtils.inputStream2OutputStream(in, baaos);
                    partContent = new PartContentOnMemory(baaos.buffers(), baaos.length());
                } else {
                    // We need to read the input stream to determine whether
                    // the size is bigger or smaller than the threshold.
                    BAAOutputStream baaos = new BAAOutputStream();
                    int count = BufferUtils.inputStream2OutputStream(in, baaos, thresholdSize);

                    if (count < thresholdSize) {
                        partContent = new PartContentOnMemory(baaos.buffers(), baaos.length());
                    } else {
                        // A BAAInputStream is an input stream over a list of non-contiguous 4K buffers.
                        BAAInputStream baais = 
                            new BAAInputStream(baaos.buffers(), baaos.length());

                        partContent = new PartContentOnFile(manager, 
                                              baais,
                                              in, 
                                              attachmentDir);
                    }

                } 
            } finally {
                if (!isRootPart) {
                    synchronized(semaphore) {
                        semaphore.notify();
                        inflight--;
                    }
                }
            }

            return partContent;
            
        } catch (Exception e) {
            throw new OMException(e);
        } 
    }
    
    /**
     * This method checks the configured threshold and
     * the current runtime information.  If it appears that we could
     * run out of memory, the threshold is reduced.
     * 
     * This method allows the user to request a much larger threshold without 
     * fear of running out of memory.  Using a larger in memory threshold generally 
     * results in better throughput.
     * 
     * @param configThreshold
     * @param inflight
     * @return threshold
     */
    private static int getRuntimeThreshold(int configThreshold, int inflight) {
        
        // Determine how much free memory is available
        Runtime r = Runtime.getRuntime();
        long totalmem = r.totalMemory();
        long maxmem = r.maxMemory();
        long freemem = r.freeMemory();
        
        // @REVIEW
        // If maximum is not defined...limit to 1G
        if (maxmem == java.lang.Long.MAX_VALUE) {
            maxmem = 1024*1024*1024; 
        }
        
        long availmem = maxmem - (totalmem - freemem);
        
       
        // Now determine the dynamic threshold
        int dynamicThreshold = (int) availmem / (THRESHOLD_FACTOR * inflight);
        
        // If it appears that we might run out of memory with this
        // threshold, reduce the threshold size.
        if (dynamicThreshold < configThreshold) {
            if (log.isDebugEnabled()) {
                log.debug("Using Runtime Attachment File Threshold " + dynamicThreshold);
                log.debug("maxmem   = " + maxmem);
                log.debug("totalmem = " + totalmem);
                log.debug("freemem  = " + freemem);
                log.debug("availmem = " + availmem);
            }
            
        } else {
            dynamicThreshold = configThreshold;
            if (log.isDebugEnabled()) {
                log.debug("Using Configured Attachment File Threshold " + configThreshold);
                log.debug("maxmem   = " + maxmem);
                log.debug("totalmem = " + totalmem);
                log.debug("freemem  = " + freemem);
                log.debug("availmem = " + availmem);
            }
        }
        return dynamicThreshold;
    }
}
