/*
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.axiom.om.util;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * Helper class for attributes.
 */
public class AttributeHelper {
    /**
    * In Axiom, a single tree should always contain objects created from the same type
    * of factory (eg: LinkedListImplFactory, DOMFactory, etc.,). This method will convert
    * omAttribute to the given omFactory.
    * 
    * @see ElementHelper#importOMElement(OMElement, OMFactory) to convert instances of OMElement
    */
    public static void importOMAttribute(OMAttribute omAttribute, OMElement omElement) {
        // first check whether the given OMAttribute has the same OMFactory
        if (omAttribute.getOMFactory().getClass().isInstance(omElement.getOMFactory())) {
            omElement.addAttribute(omAttribute);
        }
        else {
            omElement.addAttribute(omAttribute.getLocalName(), omAttribute.getAttributeValue(), omAttribute.getNamespace());
        }
    }
}