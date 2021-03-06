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
package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMXMLStreamReader;

/**
 * Interface that is used internally by Axiom and that should not be considered being part of the
 * public API.
 */
public interface OMXMLStreamReaderEx extends OMXMLStreamReader {
    // *** Methods used by StreamingOMSerializer
    
    /**
     * If enabled, treat OMSourcedElements that have
     * a OMDataSource as leaf nodes.  The caller
     * should use the getDataSource method to obtain
     * the OMDataSource for these events.
     * @param value boolean
     */
    void enableDataSourceEvents(boolean value);

    /**
     * @return OMDataSource if available
     */
    OMDataSource getDataSource();
    
    // *** Methods that potentially could be promoted to OMXMLStreamReader ***
    
    boolean isClosed();
    
    void releaseParserOnClose(boolean value);
}
