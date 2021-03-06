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

package org.apache.axiom.soap;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;

public class CashWihBuildElementTest extends AbstractTestCase {

    OMXMLParserWrapper stAXOMBuilder;
    private OMElement rootElement;

    /**
     * Constructor.
     */
    public CashWihBuildElementTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        stAXOMBuilder =
                OMXMLBuilderFactory.createOMBuilder(
                        OMAbstractFactory.getSOAP11Factory(),
                        getTestResource("non_soap.xml"));
        rootElement = stAXOMBuilder.getDocumentElement();
    }

    protected void tearDown() throws Exception {
        rootElement.close(false);
    }

    public void testWithCashAndBuild() throws Exception {
        SOAPFactory soapFactory = new SOAP11Factory();
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        envelope.getBody().addChild(rootElement);
        envelope.getBody().getFirstElement().getXMLStreamReaderWithoutCaching();
    }


}
