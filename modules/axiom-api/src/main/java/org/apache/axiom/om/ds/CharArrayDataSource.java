/*
 * Copyright 2004,2007 The Apache Software Foundation.
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
package org.apache.axiom.om.ds;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

/**
 * CharArrayDataSource is an example implementation of OMDataSourceExt.
 * Use it to insert a char[] into an OM Tree.
 * This data source is useful for placing characters into an OM
 * tree, instead of having a deeply nested tree.
 */
public class CharArrayDataSource implements OMDataSourceExt {

    char[] chars = null;
    
    /**
     * Constructor
     * @param bytes 
     * @param encoding
     */
    public CharArrayDataSource(char[] chars) {
        this.chars = chars;
    }
   
    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        try {
            // Write bytes to the output stream
            output.write(getXMLBytes(format.getCharSetEncoding()));
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(chars);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        // Some XMLStreamWriters (e.g. MTOMXMLStreamWriter) 
        // provide direct access to the OutputStream.  
        // This allows faster writing.
        OutputStream os = getOutputStream(xmlWriter);
        if (os != null) {
            String encoding = getCharacterEncoding(xmlWriter);
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding(encoding);
            serialize(os, format);
        } else {
            // Read the bytes into a reader and 
            // write to the writer.
            XMLStreamReader xmlReader = getReader();
            reader2writer(xmlReader, xmlWriter);
        }
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        CharArrayReader reader = new CharArrayReader(chars);
        
        return StAXUtils.createXMLStreamReader(reader);                                                                   
    }
    
    public InputStream getXMLInputStream(String encoding)  throws 
        UnsupportedEncodingException{
        return new ByteArrayInputStream(getXMLBytes(encoding));
    }

    public Object getObject() {
       return chars;
    }

    public boolean isDestructiveRead() {
        // Reading chars is not destructive
        return false;
    }

    public boolean isDestructiveWrite() {
        // Writing chars is not destructive
        return false;
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        
        String text = new String(chars);
        return text.getBytes(encoding);
    }
    
    public void close() {
        chars = null;
    }

    public OMDataSourceExt copy() {
        // Return shallow copy
        return new CharArrayDataSource(chars);
    }
    
    /**
     * Some XMLStreamWriters expose an OutputStream that can be
     * accessed directly.
     * @return OutputStream or null
     */
    private OutputStream getOutputStream(XMLStreamWriter writer) 
     throws XMLStreamException {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter) writer).getOutputStream();
        }
        return null;
    }
    
    /**
     * Get the character set encoding of the XMLStreamWriter
     * @return String or null
     */
    private String getCharacterEncoding(XMLStreamWriter writer) {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter) writer).getCharSetEncoding();
        }
        return null;
    }
    
    /**
     * Simple utility that takes an XMLStreamReader and writes it
     * to an XMLStreamWriter
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    private static void reader2writer(XMLStreamReader reader, 
                                     XMLStreamWriter writer)
    throws XMLStreamException {
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        builder.releaseParserOnClose(true);
        try {
            OMDocument omDocument = builder.getDocument();
            Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                OMNode omNode = (OMNode) it.next();
                omNode.serializeAndConsume(writer);
            }
        } finally {
            builder.close();
        }
    }
    
}