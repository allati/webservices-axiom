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
package org.apache.axiom.ts.om.sourcedelement;

import java.io.ByteArrayOutputStream;

import org.apache.axiom.om.OMMetaFactory;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

/**
 * Test serialization of OMSourcedElementImpl to a Stream
 */
public class TestSerializeAndConsumeToStream extends OMSourcedElementTest {
    public TestSerializeAndConsumeToStream(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        element.serializeAndConsume(bos);
        XMLAssert.assertXMLIdentical("Serialized text error", XMLUnit.compareXML(testDocument,
                     new String(bos.toByteArray())), true);
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
}
