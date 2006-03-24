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

/**
 * Interface OMComment
 */
public interface OMComment extends OMNode {
    /**
     * Returns the value of this comment as defined by XPath 1.0.
     * @return Returns String.
     */
    public String getValue();

    /**
     * Sets the content of this comment to the specified string.
     * @param text
     */
    public void setValue(String text);
}