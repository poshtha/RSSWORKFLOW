package org.wso2.carbon.rssmanager.core.workflow;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.utils.UUIDGenerator;
import org.wso2.carbon.rssmanager.core.config.RSSConfigurationManager;
import org.wso2.carbon.rssmanager.core.config.RSSManagementRepository;
import org.wso2.carbon.rssmanager.core.dao.DatabaseDAO;
import org.wso2.carbon.rssmanager.core.dao.RSSDAO;
import org.wso2.carbon.rssmanager.core.dao.RSSDAOFactory;
import org.wso2.carbon.rssmanager.core.dao.exception.RSSDAOException;
import org.wso2.carbon.rssmanager.core.dao.impl.DatabaseDAOImpl;
import org.wso2.carbon.rssmanager.core.dao.impl.DatabaseUserDAOImpl;
import org.wso2.carbon.rssmanager.core.dto.restricted.Database;
import org.wso2.carbon.rssmanager.core.dto.restricted.RSSInstance;
import org.wso2.carbon.rssmanager.core.dto.restricted.Workflow;
import org.wso2.carbon.rssmanager.core.environment.Environment;
import org.wso2.carbon.rssmanager.core.environment.EnvironmentManager;
import org.wso2.carbon.rssmanager.core.exception.RSSManagerException;
import org.wso2.carbon.rssmanager.core.internal.RSSManagerDataHolder;
import org.wso2.carbon.rssmanager.core.manager.AbstractRSSManager;
import org.wso2.carbon.rssmanager.core.manager.RSSManager;
import org.wso2.carbon.rssmanager.core.manager.adaptor.EnvironmentAdaptor;
import org.wso2.carbon.rssmanager.core.manager.adaptor.RSSManagerAdaptor;
import org.wso2.carbon.rssmanager.core.manager.adaptor.RSSManagerAdaptorImpl;

import javax.sql.DataSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WorkflowExecutor implements Serializable {
	protected String callbackURL;

	private String contentType = "application/soap+xml; charset=UTF-8";

	protected ServiceClient client;

	protected String wfTaksName;

	protected String serviceEndpoint;

	protected String username;

	protected String password;

	protected Options options;

	protected String payload;

	private static final Log log = LogFactory.getLog(WorkflowExecutor.class);

	public WorkflowExecutor() {
		try {
			client =
			         new ServiceClient(RSSManagerDataHolder.getInstance().getContextService()
			                                               .getClientConfigContext(), null);
		} catch (AxisFault e) {
			log.error("Error creating service client");
		}
	}

	protected int getTenentId() {
		return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
	}

	protected String getEndpoint() {
		int tid = getTenentId();
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getServiceEndpoint();
	}

	public abstract void buildSOAPMessage(Workflow wflow);

	public void execute(Workflow workflow) throws WorkflowException {

	}

	public void complete(Workflow workflow, String status) throws WorkflowException {

	}

	public void setOptions() {
		options.setAction("http://workflow." + getWFTask().toLowerCase() +
		                  ".ss.carbon.wso2.org/initiate");
		options.setTo(new EndpointReference(serviceEndpoint));

		if (contentType != null) {
			options.setProperty(Constants.Configuration.MESSAGE_TYPE, contentType);
		} else {
			options.setProperty(Constants.Configuration.MESSAGE_TYPE,
			                    HTTPConstants.MEDIA_TYPE_APPLICATION_XML);
		}

		HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();

		if (username != null && password != null) {
			auth.setUsername(username);
			auth.setPassword(password);
			auth.setPreemptiveAuthentication(true);
			List<String> authSchemes = new ArrayList<String>();
			authSchemes.add(HttpTransportProperties.Authenticator.BASIC);
			auth.setAuthSchemes(authSchemes);
			options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
			options.setManageSession(true);
		}

		client.setOptions(options);
	}

	public abstract List<Workflow> getWorkflowDetails(String workflowStatus)
	                                                                        throws WorkflowException;

	public String generateUUID() {
		String UUID = UUIDGenerator.generateUUID();
		return UUID;
	}

	public String getWFTask() {
		return wfTaksName;
	}

	public void setWFTask(String task) {
		wfTaksName = task;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
