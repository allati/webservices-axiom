/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.serializer;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileReader;

public class OMFaultSerializerTest extends AbstractTestCase {
    private XMLStreamReader reader1;
    private XMLStreamReader reader2;

    public OMFaultSerializerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        reader1 =
                XMLInputFactory.newInstance().
                        createXMLStreamReader(
                                new FileReader(
                                        getTestResourceFile("soap/soap11/soapfault1.xml")));
        reader2 =
            XMLInputFactory.newInstance().
                    createXMLStreamReader(
                            new FileReader(
                                    getTestResourceFile("soap/soap11/soapfault2.xml")));
       
    }

    /**
     * Test SOAPFault that does not disable the default namespace
     * (i.e. does not use xmlns="")
     * @throws Exception
     */
    public void test1() throws Exception {
    	StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(reader1, null);
        OMElement ome = builder.getDocumentElement();
        System.out.println(ome);
    }
    /**
     * Test SOAPFault that does disable the default namespace
     * (i.e. does use xmlns="")
     * @throws Exception
     */
    public void test2() throws Exception {
    	StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(reader2, null);
        OMElement ome = builder.getDocumentElement();
        System.out.println(ome);
    }
}
    