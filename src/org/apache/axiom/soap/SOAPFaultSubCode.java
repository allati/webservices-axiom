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

public interface SOAPFaultSubCode extends OMElement {
    /**
     * Eran Chinthaka (chinthaka@apache.org)
     */

    /**
     * Fault SubCode contain only one mandatory Value child. This value child contains a QName
     *
     * @param soapFaultSubCodeValue
     */
    public void setValue(SOAPFaultValue soapFaultSubCodeValue) throws SOAPProcessingException;

    public SOAPFaultValue getValue();


    /**
     * Fault SubCode can contain an optional SubCode
     *
     * @param subCode
     */
    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException;

    public SOAPFaultSubCode getSubCode();
}