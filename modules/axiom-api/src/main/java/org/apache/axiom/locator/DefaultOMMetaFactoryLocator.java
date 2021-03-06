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
package org.apache.axiom.locator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;

/**
 * The default {@link OMMetaFactoryLocator} implementation used in non OSGi environments.
 */
public final class DefaultOMMetaFactoryLocator extends PriorityBasedOMMetaFactoryLocator {
    public DefaultOMMetaFactoryLocator() {
        ClassLoader classLoader = DefaultOMMetaFactoryLocator.class.getClassLoader();
        
        Loader loader = new DefaultLoader(classLoader);
        
        List/*<Implementation>*/ implementations = new ArrayList();
        
        // If a meta factory is specified using the system property, we register it as an
        // implementation with feature "default" and maximum priority, so that it will always
        // be used as default implementation. Note that even if the system property is specified
        // we still need to run a discovery because there may be other non default implementations
        // in the classpath.
        String metaFactoryClassName = null;
        try {
            metaFactoryClassName = System.getProperty(OMAbstractFactory.META_FACTORY_NAME_PROPERTY);
            if ("".equals(metaFactoryClassName)) {
                metaFactoryClassName = null;
            }
        } catch (SecurityException e) {
            // Ignore and continue
        }
        if (metaFactoryClassName != null) {
            Implementation implementation = ImplementationFactory.createDefaultImplementation(loader, metaFactoryClassName);
            if (implementation != null) {
                implementations.add(implementation);
            }
        }

        // Now discover the available implementations by looking for the axiom.xml descriptor.
        try {
            Enumeration e = classLoader.getResources(ImplementationFactory.DESCRIPTOR_RESOURCE);
            while (e.hasMoreElements()) {
                implementations.addAll(ImplementationFactory.parseDescriptor(loader, (URL)e.nextElement()));
            }
        } catch (IOException ex) {
            // Ignore and continue
        }
        
        loadImplementations(implementations);
    }
}
