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

package org.apache.ws.commons.om.impl.llom;

import org.apache.ws.commons.attachments.ByteArrayDataSource;
import org.apache.ws.commons.om.AbstractTestCase;
import org.apache.ws.commons.om.OMAttribute;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMOutputFormat;

import javax.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;

public class OMOutputTest extends AbstractTestCase {

    /**
     * @param testName
     */
    public OMOutputTest(String testName) {
        super(testName);
    }

    String outFileName;

    String outBase64FileName;

    OMElement envelope;

    File outMTOMFile;

    File outBase64File;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        DataHandler dataHandler;

        outFileName = "OMSerializeMTOMOut.txt";
        outBase64FileName = "OMSerializeBase64Out.xml";
        outMTOMFile = getTempOutputFile(outFileName);
        outBase64File = getTempOutputFile(outBase64FileName);

        OMNamespaceImpl soap = new OMNamespaceImpl(
                "http://schemas.xmlsoap.org/soap/envelope/", "soap");
        envelope = new OMElementImpl("Envelope", soap);
        OMElement body = new OMElementImpl("Body", soap);

        OMNamespaceImpl dataName = new OMNamespaceImpl(
                "http://www.example.org/stuff", "m");
        OMElement data = new OMElementImpl("data", dataName);

        OMNamespaceImpl mime = new OMNamespaceImpl(
                "http://www.w3.org/2003/06/xmlmime", "m");

        OMElement text = new OMElementImpl("name", dataName);
        OMAttribute cType1 = new OMAttributeImpl("contentType", mime,
                "text/plain");
        text.addAttribute(cType1);
        byte[] byteArray = new byte[]{13, 56, 65, 32, 12, 12, 7, -3, -2, -1,
                                      98};
        dataHandler = new DataHandler(new ByteArrayDataSource(byteArray));
        OMTextImpl textData = new OMTextImpl(dataHandler, false);

        envelope.addChild(body);
        body.addChild(data);
        data.addChild(text);
        text.addChild(textData);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (this.outMTOMFile.exists()) {
            this.outMTOMFile.delete();
        }
        if (this.outBase64File.exists()) {
            this.outBase64File.delete();
        }
    }

    public void testComplete() throws Exception {
        OMOutputFormat mtomOutputFormat = new OMOutputFormat();
        mtomOutputFormat.setDoOptimize(true);
        OMOutputFormat baseOutputFormat = new OMOutputFormat();
        baseOutputFormat.setDoOptimize(false);
        
        envelope.serializeAndConsume(new FileOutputStream(outBase64File), baseOutputFormat);
        envelope.serializeAndConsume(new FileOutputStream(outMTOMFile), mtomOutputFormat);
    }
}