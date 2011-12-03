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
package org.apache.axiom.ts.om.element;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMElement#getDefaultNamespace()} in the special case where the
 * element has no namespace and was created as a child element of an element having a default
 * namespace with a non empty namespace URI. In this case the element must have a namespace
 * declaration that overrides the default namespace. Therefore
 * {@link OMElement#getDefaultNamespace()} must return null.
 * <p>
 * This is a regression test for
 * <a href="https://issues.apache.org/jira/browse/AXIOM-400">AXIOM-400</a>.
 */
public class TestGetDefaultNamespace2 extends AxiomTestCase {
    public TestGetDefaultNamespace2(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", "urn:test", "");
        OMElement child = factory.createOMElement("child", null, parent);
        OMNamespace ns = child.getDefaultNamespace();
        // TODO: need to specify if getDefaultNamespace should return null or ("","")
        assertTrue(ns == null || ns.equals("", ""));
    }
}