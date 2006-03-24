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


public interface SOAP11Constants extends SOAPConstants {
    /**
     * Eran Chinthaka (chinthaka@apache.org)
     */
    public static final String SOAP_ENVELOPE_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * Field ATTR_ACTOR
     */
    public static final String ATTR_ACTOR = "actor";

    /**
     * Field SOAP_FAULT_CODE_LOCAL_NAME
     */
    public static final String SOAP_FAULT_CODE_LOCAL_NAME = "faultcode";
    /**
     * Field SOAP_FAULT_STRING_LOCAL_NAME
     */
    public static final String SOAP_FAULT_STRING_LOCAL_NAME = "faultstring";
    /**
     * Field SOAP_FAULT_ACTOR_LOCAL_NAME
     */
    public static final String SOAP_FAULT_ACTOR_LOCAL_NAME = "faultactor";

    public static final String SOAP_FAULT_DETAIL_LOCAL_NAME = "detail";

    //SOAP 1.2 Content Type
    public static final String SOAP_11_CONTENT_TYPE = "text/xml";

     // -------- SOAP Fault Codes ------------------------------
    public static final String FAULT_CODE_SENDER = "Client";
    public static final String FAULT_CODE_RECEIVER = "Server";

    public static final String SOAP_ACTOR_NEXT = "http://schemas.xmlsoap.org/soap/actor/next";
}