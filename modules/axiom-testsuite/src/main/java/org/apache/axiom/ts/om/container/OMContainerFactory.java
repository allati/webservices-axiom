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
package org.apache.axiom.ts.om.container;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.RootWhitespaceFilter;
import org.apache.axiom.ts.AxiomTestCase;
import org.xml.sax.InputSource;

/**
 * Prepares {@link OMContainer} instances from a test file.
 */
public interface OMContainerFactory {
    OMContainerFactory DOCUMENT = new OMContainerFactory() {
        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("container", "document");
        }

        public InputSource getControl(InputStream testFileContent) {
            return new InputSource(testFileContent);
        }

        public OMContainer getContainer(OMXMLParserWrapper builder) {
            return builder.getDocument();
        }

        public XMLStreamReader filter(XMLStreamReader reader) {
            return new RootWhitespaceFilter(reader);
        }
    };
    
    void addTestProperties(AxiomTestCase testCase);
    InputSource getControl(InputStream testFileContent) throws Exception;
    OMContainer getContainer(OMXMLParserWrapper builder);
    XMLStreamReader filter(XMLStreamReader reader);
}
