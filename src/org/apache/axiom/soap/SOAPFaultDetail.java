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

package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;

import java.util.Iterator;


/**
 * The Detail element information item is intended for carrying application
 * specific error information related to the SOAP Body .
 * <p/>
 * The Detail element information item has:
 * A [local name] of Detail .
 * A [namespace name] of http://www.w3.org/2003/05/soap-envelope .
 * Zero or more attribute information items in its [attributes] property.
 * Zero or more child element information items in its [children] property.
 */
public interface SOAPFaultDetail extends OMElement {
    /**
     * Eran Chinthaka (chinthaka@apache.org)
     */
    public void addDetailEntry(OMElement detailElement);

    public Iterator getAllDetailEntries();

}