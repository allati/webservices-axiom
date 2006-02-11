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

public interface SOAPConstants {
    /**
     * Eran Chinthaka (chinthaka@apache.org)
     */
    /**
     * Field SOAP_DEFAULT_NAMESPACE_PREFIX
     */
    public static final String SOAP_DEFAULT_NAMESPACE_PREFIX = "soapenv";
    /**
     * Field SOAPENVELOPE_LOCAL_NAME
     */
    public static final String SOAPENVELOPE_LOCAL_NAME = "Envelope";

    /**
     * Field HEADER_LOCAL_NAME
     */
    public static final String HEADER_LOCAL_NAME = "Header";

    /**
     * Field BODY_LOCAL_NAME
     */
    public static final String BODY_LOCAL_NAME = "Body";
    /**
     * Field BODY_NAMESPACE_PREFIX
     */
    public static final String BODY_NAMESPACE_PREFIX =
            SOAP_DEFAULT_NAMESPACE_PREFIX;
    /**
     * Field BODY_FAULT_LOCAL_NAME
     */
    public static final String BODY_FAULT_LOCAL_NAME = "Fault";

    /**
     * Field ATTR_MUSTUNDERSTAND
     */
    public static final String ATTR_MUSTUNDERSTAND = "mustUnderstand";
    public static final String ATTR_MUSTUNDERSTAND_TRUE = "true";
    public static final String ATTR_MUSTUNDERSTAND_FALSE = "false";
    public static final String ATTR_MUSTUNDERSTAND_0 = "0";
    public static final String ATTR_MUSTUNDERSTAND_1 = "1";
    /**
     * Field SOAPFAULT_LOCAL_NAME
     */
    public static final String SOAPFAULT_LOCAL_NAME = "Fault";
    /**
     * Field SOAPFAULT_DETAIL_LOCAL_NAME
     */
    public static final String SOAPFAULT_DETAIL_LOCAL_NAME = "detail";

    /**
     * Field SOAPFAULT_NAMESPACE_PREFIX
     */
    public static final String SOAPFAULT_NAMESPACE_PREFIX =
            SOAP_DEFAULT_NAMESPACE_PREFIX;

    public static final String SOAP_FAULT_DETAIL_EXCEPTION_ENTRY = "Exception";

    // -------- SOAP Fault Codes ------------------------------
    public static final String FAULT_CODE_VERSION_MISMATCH = "env:VersionMismatch";
    public static final String FAULT_CODE_MUST_UNDERSTAND  = "env:MustUnderstand";
    public static final String FAULT_CODE_DATA_ENCODING_UNKNOWN = "env:DataEncodingUnknown";

    // Followings are different in SOAP 1.1 and 1.2 specifications
    public static final String FAULT_CODE_SENDER = "";
    public static final String FAULT_CODE_RECEIVER = "";

}