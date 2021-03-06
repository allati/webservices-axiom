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
package org.apache.axiom.ts.om.navigator;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.OMNavigator;
import org.apache.axiom.om.util.StAXUtils;

public class TestFullyBuilt extends OMNavigatorTestCase {
    public TestFullyBuilt(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        assertNotNull(envelope);
        //dump the out put to a  temporary file
        XMLStreamWriter output =
            StAXUtils.createXMLStreamWriter(
                    new ByteArrayOutputStream(), OMConstants.DEFAULT_CHAR_SET_ENCODING);
        envelope.serialize(output);

        //now the OM is fully created -> test the navigation
        OMNavigator navigator = new OMNavigator(envelope);
        OMSerializable node = null;
        while (navigator.isNavigable()) {
            node = navigator.next();
            assertNotNull(node);
        }
    }
}
