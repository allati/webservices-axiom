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

package org.apache.ws.commons.soap;

import org.apache.ws.commons.om.OMElement;


/**
 * The Role element information item identifies the role the node was operating
 * in at the point the fault occurred.
 * <p/>
 * The Role element information item has:
 * A [local name] of Role .
 * A [namespace name] of http://www.w3.org/2003/05/soap-envelope .
 */
public interface SOAPFaultRole extends OMElement {
    /**
     * Eran Chinthaka (chinthaka@apache.org)
     */
    
    /**
     * The value of the Role element information item MUST be one of the roles
     * assumed by the node during processing of the message
     *
     * @param uri
     */
    public void setRoleValue(String uri);

    public String getRoleValue();
}