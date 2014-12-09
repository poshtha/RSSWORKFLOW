/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.rssmanager.core.internal;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.rssmanager.core.config.WorkflowExecutorConfig;
import org.wso2.carbon.rssmanager.core.exception.RSSManagerException;
import org.wso2.carbon.rssmanager.core.workflow.WorkflowConstants;
import org.wso2.carbon.rssmanager.core.workflow.WorkflowConfigFactory;
import org.wso2.carbon.rssmanager.core.workflow.WorkflowException;
import org.wso2.carbon.rssmanager.core.workflow.WorkflowManager;
import org.wso2.carbon.utils.Axis2ConfigurationContextObserver;


public class RSSAdminAxis2ConfigContextObserver implements Axis2ConfigurationContextObserver {
	private static final Log log = LogFactory.getLog(RSSAdminAxis2ConfigContextObserver.class);
	
	@Override
	public void creatingConfigurationContext(int tenantId) {
		
	}

	@Override
	public void createdConfigurationContext(ConfigurationContext configurationContext) {
	    WorkflowManager.getInstance().load();
	}

	@Override
	public void terminatingConfigurationContext(ConfigurationContext configurationContext) {
		
	}

	@Override
	public void terminatedConfigurationContext(ConfigurationContext arg0) {
	}
}
