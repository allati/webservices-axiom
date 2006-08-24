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

package org.apache.axiom.om;

import junit.framework.TestCase;

/**
 * Each of the following tests have a parent "person" and children "name", "age", "weight".
 * The parent is defined as either:
 *   a) a qualified name (QParent)
 *   b) an unqualified name (UParent)
 *   c) a qualified name using the default namespace (DParent)
 * 
 * Likewise the children are defined as either:
 *   a) qualified names (QChildren)
 *   b) unqualified children (UChildren) 
 *   c) qualified using the default namespace (DChildren)
 * 
 *
 */
public class SerializationTest extends TestCase {

	private static final String NS = "http://ws.apache.org/axis2/apacheconasia/06";
	private static final String PREFIX = "prefix";
	
    public void testDParentDChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace(NS, "");
        OMNamespace nsChildren = fac.createOMNamespace(NS, "");
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect namespace serialization",2, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
    }
    
    public void testDParentUChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace(NS, "");
        OMNamespace nsChildren = fac.createOMNamespace("", "");
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect namespace serialization",2, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
        assertEquals("Incorrect namespace serialization",4, xml.split("\"\"").length);
    }
    
    public void testDParentQChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace(NS, "");
        OMNamespace nsChildren = fac.createOMNamespace(NS, PREFIX);
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect namespace serialization",5, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
    }
    
    
    public void testQParentQChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace(NS, PREFIX);
        OMNamespace nsChildren = fac.createOMNamespace(NS, PREFIX);
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect namespace serialization",2, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
    }
    
    public void testQParentUChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace(NS, PREFIX);
        OMNamespace nsChildren = fac.createOMNamespace("", "");
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect default namespace serialization",2, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
        assertEquals("Incorrect namespace serialization",1, xml.split("\"\"").length);
    }
    
    public void testQParentDChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace(NS, PREFIX);
        OMNamespace nsChildren = fac.createOMNamespace(NS, "");
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect default namespace serialization",5, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
    }
    
    public void testUParentUChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace("", "");
        OMNamespace nsChildren = fac.createOMNamespace("", "");
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect default namespace serialization",1, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
        assertEquals("Incorrect namespace serialization",1, xml.split("\"\"").length);
    }
    
    public void testUParentQChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace("", "");
        OMNamespace nsChildren = fac.createOMNamespace(NS, PREFIX);
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect default namespace serialization",4, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
        assertEquals("Incorrect namespace serialization",1, xml.split("\"\"").length);
    }
    
    public void testUParentDChildren() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        
        OMNamespace nsParent = fac.createOMNamespace("", "");
        OMNamespace nsChildren = fac.createOMNamespace(NS, PREFIX);
        
        OMElement personElem = fac.createOMElement("person", nsParent);
        OMElement nameElem = fac.createOMElement("name", nsChildren);
        nameElem.setText("John");
        
        OMElement ageElem = fac.createOMElement("age", nsChildren);
        ageElem.setText("34");
        
        OMElement weightElem = fac.createOMElement("weight", nsChildren);
        weightElem.setText("50");
        
        //Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);
        
        String xml = personElem.toString();
        
        assertEquals("Incorrect namespace serialization",4, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
        assertEquals("Incorrect namespace serialization",1, xml.split("\"\"").length);
    }
    
    
    
   
    
    /**
     * Special case when OMElement is created with a null OMNamespace.
     * In this case, the created OMElement uses that default namespace of the parent.
     */
    public void testNullOMNamespace() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("http://ws.apache.org/axis2/apacheconasia/06", "");
        OMElement personElem = fac.createOMElement("person", ns);

        //Create and add using null namespace...this should pick up default namespace from parent
        OMElement nameElem = fac.createOMElement("name", null);
        nameElem.setText("John");
        personElem.addChild(nameElem);

        String xml = personElem.toString();
        
        assertEquals("Incorrect namespace serialization",2, xml.split("http://ws.apache.org/axis2/apacheconasia/06").length);
        assertEquals("Incorrect serialization", 1, xml.split("xmlns=\"\"").length);
        
    }
    
}