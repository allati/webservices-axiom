/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.NamespaceIterator;
import org.apache.axiom.om.impl.common.OMChildElementIterator;
import org.apache.axiom.om.impl.common.OMChildrenLegacyQNameIterator;
import org.apache.axiom.om.impl.common.OMChildrenLocalNameIterator;
import org.apache.axiom.om.impl.common.OMChildrenNamespaceIterator;
import org.apache.axiom.om.impl.common.OMChildrenQNameIterator;
import org.apache.axiom.om.impl.common.OMContainerHelper;
import org.apache.axiom.om.impl.common.OMDescendantsIterator;
import org.apache.axiom.om.impl.common.OMElementImplUtil;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.jaxp.OMSource;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.impl.util.EmptyIterator;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/** Class OMElementImpl */
public class OMElementImpl extends OMNodeImpl
        implements OMElementEx, OMConstants, OMContainerEx {

    private static final Log log = LogFactory.getLog(OMElementImpl.class);
    
    /**
     * The namespace of this element. Possible values:
     * <ul>
     * <li><code>null</code> (if the element has no namespace)
     * <li>any {@link OMNamespace} instance, with the following exceptions:
     * <ul>
     * <li>an {@link OMNamespace} instance with a <code>null</code> prefix
     * <li>an {@link OMNamespace} instance with both prefix and namespace URI set to the empty
     * string
     * </ul>
     * </ul>
     */
    protected OMNamespace ns;

    /** Field localName */
    protected String localName;

    protected QName qName;

    /** Field firstChild */
    protected OMNode firstChild;

    /** Field namespaces */
    protected HashMap namespaces = null;
    
    /** Field attributes */
    protected HashMap attributes = null;

    protected OMNode lastChild;
    private int lineNumber;
    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    /**
     * Constructor OMElementImpl. A null namespace indicates that the default namespace in scope is
     * used
     */
    public OMElementImpl(String localName, OMNamespace ns, OMContainer parent,
                         OMXMLParserWrapper builder, OMFactory factory) {
        super(parent, factory, false);
        this.localName = localName;
        if (ns != null) {
            setNamespace(ns);
        }
        this.builder = builder;
        firstChild = null;
    }


    /** Constructor OMElementImpl. */
    public OMElementImpl(String localName, OMNamespace ns, OMFactory factory) {
        this(localName, ns, null, factory);
    }

    /**
     * This is the basic constructor for OMElement. All the other constructors depends on this.
     *
     * @param localName - this MUST always be not null
     * @param ns        - can be null
     * @param parent    - this should be an OMContainer
     * @param factory   - factory that created this OMElement
     *                  <p/>
     *                  A null namespace indicates that the default namespace in scope is used
     */
    public OMElementImpl(String localName, OMNamespace ns, OMContainer parent,
                         OMFactory factory) {
        super(parent, factory, true);
        if (localName == null || localName.trim().length() == 0) {
            throw new OMException("localname can not be null or empty");
        }
        this.localName = localName;
        setNamespace(ns);
    }

    /**
     * It is assumed that the QName passed contains, at least, the localName for this element.
     *
     * @param qname - this should be valid qname according to javax.xml.namespace.QName
     * @throws OMException
     */
    public OMElementImpl(QName qname, OMContainer parent, OMFactory factory)
            throws OMException {
        super(parent, factory, true);
        localName = qname.getLocalPart();
        this.ns = handleNamespace(qname);
    }

    /** Method handleNamespace. */
    OMNamespace handleNamespace(QName qname) {
        OMNamespace ns = null;

        // first try to find a namespace from the scope
        String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            String prefix = qname.getPrefix();
            ns = findNamespace(namespaceURI, prefix);

            /**
             * What is left now is
             *  1. nsURI = null & parent != null, but ns = null
             *  2. nsURI != null, (parent doesn't have an ns with given URI), but ns = null
             */
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                ns = declareNamespace(namespaceURI, prefix);
            }
        } else if (qname.getPrefix().length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        return ns;
    }

    private OMNamespace handleNamespace(OMNamespace ns) {
        String namespaceURI = ns == null ? "" : ns.getNamespaceURI();
        String prefix = ns == null ? "" : ns.getPrefix();
        if (namespaceURI.length() == 0 && prefix != null && prefix.length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        if (namespaceURI.length() == 0) {
            // Special case: no namespace; we need to generate a namespace declaration only if
            // there is a conflicting namespace declaration (i.e. a declaration for the default
            // namespace with a non empty URI) is in scope
            if (getDefaultNamespace() != null) {
                declareDefaultNamespace("");
            }
            return null;
        } else {
            OMNamespace namespace = findNamespace(namespaceURI, prefix);
            if (namespace == null) {
                namespace = declareNamespace(ns);
            }
            return namespace;
        }
    }

    OMNamespace handleNamespace(String namespaceURI, String prefix) {
        if (prefix.length() == 0 && namespaceURI.length() == 0) {
            OMNamespace namespace = getDefaultNamespace();
            if (namespace != null) {
                declareDefaultNamespace("");
            }
            return null;
        } else {
            OMNamespace namespace = findNamespace(namespaceURI,
                                                  prefix);
            if (namespace == null) {
                namespace = declareNamespace(namespaceURI, prefix.length() > 0 ? prefix : null);
            }
            return namespace;
        }
    }

    /**
     * Adds child to the element. One can decide whether to append the child or to add to the front
     * of the children list.
     */
    public void addChild(OMNode child) {
        if (child.getOMFactory() instanceof OMLinkedListImplFactory) {
            addChild((OMNodeImpl) child);
        } else {
            addChild(importNode(child));
        }
    }


    /**
     * Searches for children with a given QName and returns an iterator to traverse through the
     * OMNodes. This QName can contain any combination of prefix, localname and URI.
     *
     * @throws OMException
     */
    public Iterator getChildrenWithName(QName elementQName) {
        OMNode firstChild = getFirstOMChild();
        Iterator it =  new OMChildrenQNameIterator(firstChild, elementQName);
        
        // The getChidrenWithName method used to tolerate an empty namespace
        // and interpret that as getting any element that matched the local
        // name.  There are custmers of axiom that have hard-coded dependencies
        // on this semantic.
        // The following code falls back to this legacy behavior only if
        // (a) elementQName has no namespace, (b) the new iterator finds no elements
        // and (c) there are children.
        if (elementQName.getNamespaceURI().length() == 0 &&
            firstChild != null &&
            !it.hasNext()) {
            if (log.isTraceEnabled()) {
                log.trace("There are no child elements that match the unqualifed name: " +
                          elementQName);
                log.trace("Now looking for child elements that have the same local name.");
            }
            it = new OMChildrenLegacyQNameIterator(getFirstOMChild(), elementQName);
        }
        
        return it;
    }
    

    public Iterator getChildrenWithLocalName(String localName) {
        return new OMChildrenLocalNameIterator(getFirstOMChild(),
                                               localName);
    }


    public Iterator getChildrenWithNamespaceURI(String uri) {
        return new OMChildrenNamespaceIterator(getFirstOMChild(),
                                               uri);
    }


    /**
     * Method getFirstChildWithName.
     *
     * @throws OMException
     */
    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
        OMChildrenQNameIterator omChildrenQNameIterator =
                new OMChildrenQNameIterator(getFirstOMChild(),
                                            elementQName);
        OMNode omNode = null;
        if (omChildrenQNameIterator.hasNext()) {
            omNode = (OMNode) omChildrenQNameIterator.next();
        }

        return ((omNode != null) && (OMNode.ELEMENT_NODE == omNode.getType())) ?
                (OMElement) omNode : null;

    }

    /** Method addChild. */
    private void addChild(OMNodeImpl child) {
        if (child.parent == this && child == lastChild && done) {
            // The child is already the last node. 
            // We don't need to detach and re-add it.
        } else {
            // Normal Case
            
            if (child.parent != null) {
                child.detach();
            }
            
            child.parent = this;

            if (firstChild == null) {
                firstChild = child;
                child.previousSibling = null;
            } else {
                child.previousSibling = (OMNodeImpl) lastChild;
                ((OMNodeImpl) lastChild).nextSibling = child;
            }

            child.nextSibling = null;
            lastChild = child;
        }

        // For a normal OMNode, the incomplete status is
        // propogated up the tree.  
        // However, a OMSourcedElement is self-contained 
        // (it has an independent parser source).
        // So only propogate the incomplete setting if this
        // is a normal OMNode
        if (!child.isComplete() && 
            !(child instanceof OMSourcedElement)) {
            this.setComplete(false);
        }

    }

    /**
     * Gets the next sibling. This can be an OMAttribute or OMText or OMELement for others.
     *
     * @throws OMException
     */
    public OMNode getNextOMSibling() throws OMException {
        while (!done && builder != null ) {
            if (builder.isCompleted()) {
                log.debug("Builder is complete.  Setting OMElement to complete.");
                setComplete(true);
            } else {
                int token = builder.next();
                if (token == XMLStreamConstants.END_DOCUMENT) {
                    throw new OMException(
                    "Parser has already reached end of the document. No siblings found");
                }
            }
        }
        return super.getNextOMSibling();
    }

    /**
     * Returns a collection of this element. Children can be of types OMElement, OMText.
     *
     * @return Returns children.
     */
    public Iterator getChildren() {
        return new OMChildrenIterator(getFirstOMChild());
    }

    public Iterator getDescendants(boolean includeSelf) {
        return new OMDescendantsIterator(this, includeSelf);
    }

    /**
     * Returns a filtered list of children - just the elements.
     *
     * @return Returns an iterator of the child elements.
     */
    public Iterator getChildElements() {
        return new OMChildElementIterator(getFirstElement());
    }

    public OMNamespace declareNamespace(String uri, String prefix) {
        if ("".equals(prefix)) {
            log.warn("Deprecated usage of OMElement#declareNamespace(String,String) with empty prefix");
            prefix = OMSerializerUtil.getNextNSPrefix();
        }
        OMNamespaceImpl ns = new OMNamespaceImpl(uri, prefix);
        return declareNamespace(ns);
    }

    public OMNamespace declareDefaultNamespace(String uri) {
        if (ns == null && uri.length() > 0
                || ns != null && ns.getPrefix().length() == 0 && !ns.getNamespaceURI().equals(uri)) {
            throw new OMException("Attempt to add a namespace declaration that conflicts with " +
            		"the namespace information of the element");
        }

        OMNamespaceImpl namespace = new OMNamespaceImpl(uri == null ? "" : uri, "");

        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        namespaces.put("", namespace);
        return namespace;
    }

    public OMNamespace getDefaultNamespace() {
        OMNamespace defaultNS;
        if (namespaces != null && (defaultNS = (OMNamespace) namespaces.get("")) != null) {
            return defaultNS.getNamespaceURI().length() == 0 ? null : defaultNS;
        }
        if (parent instanceof OMElementImpl) {
            return ((OMElementImpl) parent).getDefaultNamespace();

        }
        return null;
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        OMNamespace ns = new OMNamespaceImpl(uri, prefix);
        namespaces.put(prefix, ns);
        return ns;
    }

    /** @return Returns namespace. */
    public OMNamespace declareNamespace(OMNamespace namespace) {
        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        String prefix = namespace.getPrefix();
        if (prefix == null) {
            prefix = OMSerializerUtil.getNextNSPrefix();
            namespace = new OMNamespaceImpl(namespace.getNamespaceURI(), prefix);
        }
        if (prefix.length() > 0 && namespace.getNamespaceURI().length() == 0) {
            throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
        }
        namespaces.put(prefix, namespace);
        return namespace;
    }

    public void undeclarePrefix(String prefix) {
        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        namespaces.put(prefix, new OMNamespaceImpl("", prefix));
    }

    /**
     * Finds a namespace with the given uri and prefix, in the scope of the document. Starts to find
     * from the current element and goes up in the hiararchy until one is found. If none is found,
     * returns null.
     */
    public OMNamespace findNamespace(String uri, String prefix) {

        // check in the current element
        OMNamespace namespace = findDeclaredNamespace(uri, prefix);
        if (namespace != null) {
            return namespace;
        }

        // go up to check with ancestors
        if (parent != null) {
            //For the OMDocumentImpl there won't be any explicit namespace
            //declarations, so going up the parent chain till the document
            //element should be enough.
            if (parent instanceof OMElement) {
                namespace = ((OMElementImpl) parent).findNamespace(uri, prefix);
                // If the prefix has been redeclared, then ignore the binding found on the ancestors
                if (prefix == null && namespace != null && findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                    namespace = null;
                }
            }
        }

        return namespace;
    }

    public OMNamespace findNamespaceURI(String prefix) {
        OMNamespace ns = this.namespaces == null ?
                null :
                (OMNamespace) this.namespaces.get(prefix);

        if (ns == null) {
            if (this.parent instanceof OMElement) {
                // try with the parent
                return ((OMElement) this.parent).findNamespaceURI(prefix);
            } else {
                return null;
            }
        } else if (prefix != null && prefix.length() > 0 && ns.getNamespaceURI().length() == 0) {
            // Prefix undeclaring case (XML 1.1 only)
            return null;
        } else {
            return ns;
        }
    }

    // Constant
    static final OMNamespaceImpl xmlns =
            new OMNamespaceImpl(OMConstants.XMLNS_URI,
                                OMConstants.XMLNS_PREFIX);

    /**
     * Checks for the namespace <B>only</B> in the current Element. This is also used to retrieve
     * the prefix of a known namespace URI.
     */
    private OMNamespace findDeclaredNamespace(String uri, String prefix) {
        if (uri == null) {
            return namespaces == null ? null : (OMNamespace)namespaces.get(prefix);
        }

        //If the prefix is available and uri is available and its the xml namespace
        if (prefix != null && prefix.equals(OMConstants.XMLNS_PREFIX) &&
                uri.equals(OMConstants.XMLNS_URI)) {
            return xmlns;
        }

        if (namespaces == null) {
            return null;
        }

        if (prefix == null || "".equals(prefix)) {

            OMNamespace defaultNamespace = this.getDefaultNamespace();
            if (defaultNamespace != null && uri.equals(defaultNamespace.getNamespaceURI())) {
                return defaultNamespace;
            }
            Iterator namespaceListIterator = namespaces.values().iterator();

            String nsUri;

            while (namespaceListIterator.hasNext()) {
                OMNamespace omNamespace =
                        (OMNamespace) namespaceListIterator.next();
                nsUri = omNamespace.getNamespaceURI();
                if (nsUri != null &&
                        nsUri.equals(uri)) {
                    return omNamespace;
                }
            }
        } else {
            OMNamespace namespace = (OMNamespace) namespaces.get(prefix);
            if (namespace != null && uri.equals(namespace.getNamespaceURI())) {
                return namespace;
            }
        }
        return null;
    }


    /**
     * Method getAllDeclaredNamespaces.
     *
     * @return Returns Iterator.
     */
    public Iterator getAllDeclaredNamespaces() {
        if (namespaces == null) {
            return EMPTY_ITERATOR;
        }
        return namespaces.values().iterator();
    }

    public Iterator getNamespacesInScope() {
        return new NamespaceIterator(this);
    }

    public NamespaceContext getNamespaceContext(boolean detached) {
        return OMElementImplUtil.getNamespaceContext(this, detached);
    }

    /**
     * Returns a List of OMAttributes.
     *
     * @return Returns iterator.
     */
    public Iterator getAllAttributes() {
        if (attributes == null) {
            return EMPTY_ITERATOR;
        }
        return attributes.values().iterator();
    }

    /**
     * Returns a named attribute if present.
     *
     * @param qname the qualified name to search for
     * @return Returns an OMAttribute with the given name if found, or null
     */
    public OMAttribute getAttribute(QName qname) {
        return attributes == null ? null : (OMAttribute) attributes.get(qname);
    }

    /**
     * Returns a named attribute's value, if present.
     *
     * @param qname the qualified name to search for
     * @return Returns a String containing the attribute value, or null.
     */
    public String getAttributeValue(QName qname) {
        OMAttribute attr = getAttribute(qname);
        return (attr == null) ? null : attr.getAttributeValue();
    }

    /**
     * Inserts an attribute to this element. Implementor can decide as to insert this in the front
     * or at the end of set of attributes.
     *
     * <p>The owner of the attribute is set to be the particular <code>OMElement</code>.
     * If the attribute already has an owner then the attribute is cloned (i.e. its name,
     * value and namespace are copied to a new attribute) and the new attribute is added
     * to the element. It's owner is then set to be the particular <code>OMElement</code>.
     * 
     * @return The attribute that was added to the element. Note: The added attribute
     * may not be the same instance that was given to add. This can happen if the given
     * attribute already has an owner. In such case the returned attribute and the given
     * attribute are <i>equal</i> but not the same instance.
     *
     * @see OMAttributeImpl#equals(Object)
     */
    public OMAttribute addAttribute(OMAttribute attr){
        // If the attribute already has an owner element then clone the attribute (except if it is owned
        // by the this element)
        OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == this) {
                return attr;
            }
            attr = new OMAttributeImpl(
                    attr.getLocalName(), attr.getNamespace(), attr.getAttributeValue(), attr.getOMFactory());
        }

        if (attributes == null) {
            this.attributes = new LinkedHashMap(5);
        }
        OMNamespace namespace = attr.getNamespace();
        if (namespace != null) {
            String uri = namespace.getNamespaceURI();
            if (uri.length() > 0) {
                String prefix = namespace.getPrefix();
                OMNamespace ns2 = findNamespaceURI(prefix);
                if (ns2 == null || !uri.equals(ns2.getNamespaceURI())) {
                    declareNamespace(uri, prefix);
                }
            }
        }

        // Set the owner element of the attribute
        ((OMAttributeImpl)attr).owner = this;
        OMAttributeImpl oldAttr = (OMAttributeImpl)attributes.put(attr.getQName(), attr);
        // Did we replace an existing attribute?
        if (oldAttr != null) {
            oldAttr.owner = null;
        }
        return attr;
    }

    /** Method removeAttribute. */
    public void removeAttribute(OMAttribute attr) {
        if (attributes != null) {
            // Remove the owner from this attribute
            ((OMAttributeImpl)attr).owner = null;
            attributes.remove(attr.getQName());
        }
    }

    public OMAttribute addAttribute(String attributeName, String value,
                                    OMNamespace ns) {
        OMNamespace namespace = null;
        if (ns != null) {
            String namespaceURI = ns.getNamespaceURI();
            String prefix = ns.getPrefix();
            namespace = findNamespace(namespaceURI, prefix);
            if (namespace == null) {
                namespace = new OMNamespaceImpl(namespaceURI, prefix);
            }
        }
        return addAttribute(new OMAttributeImpl(attributeName, namespace, value, this.factory));
    }

    /** Method setBuilder. */
    public void setBuilder(OMXMLParserWrapper wrapper) {
        this.builder = wrapper;
    }

    /**
     * Method getBuilder.
     *
     * @return Returns OMXMLParserWrapper.
     */
    public OMXMLParserWrapper getBuilder() {
        return builder;
    }

    /** Forces the parser to proceed, if parser has not yet finished with the XML input. */
    public void buildNext() {
        if (builder != null) {
            if (!builder.isCompleted()) {
                builder.next();
            } else {
                this.setComplete(true);
                log.debug("Builder is complete.  Setting OMElement to complete.");
            }         
        }
    }

    /**
     * Method getFirstOMChild.
     *
     * @return Returns child.
     */
    public OMNode getFirstOMChild() {
        while ((firstChild == null) && !done) {
            buildNext();
        }
        return firstChild;
    }

    public OMNode getFirstOMChildIfAvailable() {
        return firstChild;
    }


    /** Method setFirstChild. */
    public void setFirstChild(OMNode firstChild) {
        if (firstChild != null) {
            ((OMNodeEx) firstChild).setParent(this);
        }
        this.firstChild = firstChild;
    }


    public void setLastChild(OMNode omNode) {
         this.lastChild = omNode;
    }

    /**
     * Removes this information item and its children, from the model completely.
     *
     * @throws OMException
     */
    public OMNode detach() throws OMException {
        if (!done) {
            build();
        }
        super.detach();
        return this;
    }

    /** Gets the type of node, as this is the super class of all the nodes. */
    public int getType() {
        return OMNode.ELEMENT_NODE;
    }

    public void build() throws OMException {
        /**
         * builder is null. Meaning this is a programatical created element but it has children which are not completed
         * Build them all.
         */
        if (builder == null && !done) {
            for (Iterator childrenIterator = this.getChildren(); childrenIterator.hasNext();) {
                OMNode omNode = (OMNode) childrenIterator.next();
                omNode.build();
            }
        } else {
            super.build();
        }

    }

    public XMLStreamReader getXMLStreamReader() {
        return getXMLStreamReader(true);
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache) {
        return OMContainerHelper.getXMLStreamReader(this, cache);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        return OMContainerHelper.getXMLStreamReader(this, cache, configuration);
    }

    /**
     * Sets the text of the given element. caution - This method will wipe out all the text elements
     * (and hence any mixed content) before setting the text.
     */
    public void setText(String text) {

        OMNode child = this.getFirstOMChild();
        while (child != null) {
            if (child.getType() == OMNode.TEXT_NODE) {
                child.detach();
            }
            child = child.getNextOMSibling();
        }

        getOMFactory().createOMText(this, text);
    }

    public void setText(QName text) {

        OMNode child = this.getFirstOMChild();
        while (child != null) {
            if (child.getType() == OMNode.TEXT_NODE) {
                child.detach();
            }
            child = child.getNextOMSibling();
        }

        getOMFactory().createOMText(this, text);
    }

    public String getText() {
        return OMElementImplUtil.getText(this);
    }

    public Reader getTextAsStream(boolean cache) {
        return OMElementImplUtil.getTextAsStream(this, cache);
    }

    public QName getTextAsQName() {
        String childText = getText().trim();
        return childText.length() == 0 ? null : resolveQName(childText);
    }

    public void writeTextTo(Writer out, boolean cache) throws IOException {
        OMElementImplUtil.writeTextTo(this, out, cache);
    }

///////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

    public void internalSerialize(XMLStreamWriter writer, boolean cache)
            throws XMLStreamException {

        if (cache || this.done || (this.builder == null)) {
            OMSerializerUtil.serializeStartpart(this, writer);
            OMSerializerUtil.serializeChildren(this, writer, cache);
            OMSerializerUtil.serializeEndpart(writer);
        } else {
            OMSerializerUtil.serializeByPullStream(this, writer, cache);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets first element.
     *
     * @return Returns element.
     */
    public OMElement getFirstElement() {
        OMNode node = getFirstOMChild();
        while (node != null) {
            if (node.getType() == OMNode.ELEMENT_NODE) {
                return (OMElement) node;
            } else {
                node = node.getNextOMSibling();
            }
        }
        return null;
    }

    /**
     * Method getLocalName.
     *
     * @return Returns local name.
     */
    public String getLocalName() {
        return localName;
    }

    /** Method setLocalName. */
    public void setLocalName(String localName) {
        this.localName = localName;
        this.qName = null;
    }

    public OMNamespace getNamespace() {
        return ns;
    }

    public String getPrefix() {
        OMNamespace ns = getNamespace();
        if (ns == null) {
            return null;
        } else {
            String prefix = ns.getPrefix();
            return prefix.length() == 0 ? null : prefix;
        }
    }

    public String getNamespaceURI() {
        OMNamespace ns = getNamespace();
        if (ns == null) {
            return null;
        } else {
            String namespaceURI = ns.getNamespaceURI();
            return namespaceURI.length() == 0 ? null : namespaceURI;
        }
    }

    public void setNamespace(OMNamespace namespace) {
        this.ns = handleNamespace(namespace);
        this.qName = null;
    }

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        this.ns = namespace;
        this.qName = null;
    }

    /**
     * Method getQName.
     *
     * @return Returns QName.
     */
    public QName getQName() {
        if (qName != null) {
            return qName;
        }

        if (ns != null) {
            qName = new QName(ns.getNamespaceURI(), localName, ns.getPrefix());
        } else {
            qName = new QName(localName);
        }
        return qName;
    }

    public String toStringWithConsume() throws XMLStreamException {
        StringWriter writer = new StringWriter();
        XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
        try {
            this.serializeAndConsume(writer2);
            writer2.flush();
        } finally {
            writer2.close();
        }
        return writer.toString();
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
            try {
                this.serialize(writer2);
                writer2.flush();
            } finally {
                writer2.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Can not serialize OM Element " + this.getLocalName(), e);
        }
        return writer.toString();
    }

    /**
     * Method discard.
     *
     * @throws OMException
     */
    public void discard() throws OMException {
        if (done || builder == null) {
            this.detach();
        } else {
            builder.discard(this);
        }
    }

    public QName resolveQName(String qname) {
        int idx = qname.indexOf(':');
        if (idx == -1) {
            OMNamespace ns = getDefaultNamespace();
            return ns == null ? new QName(qname) : new QName(ns.getNamespaceURI(), qname, "");
        } else {
            String prefix = qname.substring(0, idx);
            OMNamespace ns = findNamespace(null, prefix);
            return ns == null ? null : new QName(ns.getNamespaceURI(), qname.substring(idx+1), prefix);
        }
    }

    public OMElement cloneOMElement() {
        
        if (log.isDebugEnabled()) {
            log.debug("cloneOMElement start");
            log.debug("  element string =" + getLocalName());
            log.debug(" isComplete = " + isComplete());
            log.debug("  builder = " + builder);
        }
        // Make sure the source (this node) is completed
        if (!isComplete()) {
            this.build();
        }
        
        // Now get a parser for the full tree
        XMLStreamReader xmlStreamReader = this.getXMLStreamReader(true);
        if (log.isDebugEnabled()) {
            log.debug("  reader = " + xmlStreamReader);
        }
        
        // Build the (target) clonedElement from the parser
        OMElement clonedElement =
                new StAXOMBuilder(xmlStreamReader).getDocumentElement();
        clonedElement.build();
        return clonedElement;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMNode#buildAll()
      */
    public void buildWithAttachments() {
        if (!done) {
            this.build();
        }
        Iterator iterator = getChildren();
        while (iterator.hasNext()) {
            OMNode node = (OMNode) iterator.next();
            node.buildWithAttachments();
        }
    }

    /** This method will be called when one of the children becomes complete. */
    void notifyChildComplete() {
        if (!this.done && builder == null) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }

    public SAXSource getSAXSource(boolean cache) {
        return new OMSource(this);
    }
}

