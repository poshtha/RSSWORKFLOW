package org.wso2.carbon.rssmanager.core.workflow;

import org.apache.axiom.om.util.AXIOMUtil;
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
import org.wso2.carbon.rssmanager.core.config.WFMessage;
import org.wso2.carbon.rssmanager.core.config.WFMessage;
import org.wso2.carbon.rssmanager.core.dao.exception.RSSDAOException;
import org.wso2.carbon.rssmanager.core.dto.restricted.Database;
import org.wso2.carbon.rssmanager.core.dto.restricted.Workflow;
import org.wso2.carbon.rssmanager.core.environment.dao.EnvironmentManagementDAOFactory;
import org.wso2.carbon.rssmanager.core.exception.RSSManagerException;
import org.wso2.carbon.rssmanager.core.internal.RSSManagerDataHolder;
import org.wso2.carbon.rssmanager.core.util.RSSManagerUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCreationWSWorkflowExecutor extends WorkflowExecutor {

	private static final Log log = LogFactory.getLog(DatabaseCreationWSWorkflowExecutor.class);

	@XmlRootElement(name = "CreateDBApprovalWorkFlowProcessRequest")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class CreateDBApprovalWorkFlowProcessRequest extends WFMessage {

	}

	WorkflowManager wm;

	public DatabaseCreationWSWorkflowExecutor() {
		wm = WorkflowManager.getInstance();
	}

	@Override
	public void execute(Workflow workflow) throws WorkflowException {
		if (log.isDebugEnabled()) {
			log.info("Executing Database creation Workflow..");
		}
		workflow.setStatus(WorkflowConstants.WORKFLOW_CREATED);
		buildSOAPMessage(workflow);
		serviceEndpoint = wm.getServiceEndpoint(workflow.getTenantId());
		username = wm.getUsername(workflow.getTenantId());
		password = wm.getPassword(workflow.getTenantId());
		setOptions();
		try {
			client.fireAndForget(AXIOMUtil.stringToOM(payload));
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void complete(Workflow workflow, String status) throws WorkflowException {

		Connection conn = null;
		PreparedStatement getWFStatusStatement = null;
		ResultSet resultSet = null;
		String rssEnv = "";
		String rssDBname = "";
		String rssInstance = "";
		int rssServerId = -1;
		int tenantId = -1;
		String rssDBType = "";
		WorkflowManager wm = WorkflowManager.getInstance();

		if (WorkflowConstants.WORKFLOW_APPROVED.equals(status)) {
			try {
				conn = RSSManagerUtil.getDataSource().getConnection();
				String getWFStatusQuery =
				                          "SELECT RM_ENVIRONMENT.NAME, RM_DATABASE.NAME, RM_DATABASE.RSS_INSTANCE_ID, RM_SERVER_INSTANCE.NAME, RM_DATABASE.TENANT_ID, RM_DATABASE.TYPE FROM RM_DATABASE INNER JOIN RM_SERVER_INSTANCE ON RM_DATABASE.RSS_INSTANCE_ID = RM_SERVER_INSTANCE.ID INNER JOIN RM_ENVIRONMENT ON RM_ENVIRONMENT.ID = RM_SERVER_INSTANCE.ENVIRONMENT_ID WHERE RM_DATABASE.ID = ?";
				getWFStatusStatement = conn.prepareStatement(getWFStatusQuery);
				getWFStatusStatement.setInt(1, wm.getWFResourceID(workflow.getId()));
				resultSet = getWFStatusStatement.executeQuery();
				while (resultSet.next()) {
					rssEnv = resultSet.getString("RM_ENVIRONMENT.NAME");
					rssDBname = resultSet.getString("RM_DATABASE.NAME");
					rssServerId = resultSet.getInt("RM_DATABASE.RSS_INSTANCE_ID");
					rssInstance = resultSet.getString("RM_SERVER_INSTANCE.NAME");
					tenantId = resultSet.getInt("RM_DATABASE.TENANT_ID");
					rssDBType = resultSet.getString("RM_DATABASE.TYPE");
				}
				wm.updateWFStatus(workflow.getId(), WorkflowConstants.WORKFLOW_APPROVED);
				Database database = new Database();
				database.setRssInstanceName(rssInstance);
				database.setType(rssDBType);
				database.setTenantId(tenantId);
				database.setName(rssDBname);
				PrivilegedCarbonContext.startTenantFlow();
				PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId);
				EnvironmentManagementDAOFactory.getEnvironmentManagementDAO().getEnvironmentDAO()
				                               .getEnvironment(rssEnv).getRSSManagerAdaptor()
				                               .addDatabase(database);
			} catch (SQLException e) {
				log.error("Error retrieving worklow status", e);
			} catch (RSSManagerException e) {
				log.error("RSS Manager error", e);
			} catch (RSSDAOException e) {
				log.error("RSS Data access error", e);
			} finally {
				wm.closeStatement(getWFStatusStatement, WorkflowConstants.WORKFLOW_CREATED);
				wm.closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
			}

		}
		if (WorkflowConstants.WORKFLOW_REJECT.equals(status)) {
			wm.updateWFStatus(workflow.getId(), WorkflowConstants.WORKFLOW_REJECT);
			try {
				conn = RSSManagerUtil.getDataSource().getConnection();
				String delQuery = "DELETE FROM RM_DATABASE WHERE ID=?";
				PreparedStatement delQueryStatement = conn.prepareStatement(delQuery);
				delQueryStatement.setInt(1, wm.getWFResourceID(workflow.getId()));
				delQueryStatement.executeUpdate();
			} catch (SQLException e) {
				log.error("Error deleting metadata table entry", e);
			} finally {
				wm.closeStatement(getWFStatusStatement, WorkflowConstants.WORKFLOW_CREATED);
				wm.closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
			}
		}
	}

	@Override
	public List<Workflow> getWorkflowDetails(String workflowStatus) throws WorkflowException {
		return null;
	}

	@Override
	public void buildSOAPMessage(Workflow wflow) {

		CreateDBApprovalWorkFlowProcessRequest cdbWf = new CreateDBApprovalWorkFlowProcessRequest();
		cdbWf.setEnvironment(wflow.getType());
		cdbWf.setCallbackURL(wflow.getCallbackURL());
		cdbWf.setParameters(wflow.getParameterList());

		JAXBContext context;
		try {
			context = JAXBContext.newInstance(CreateDBApprovalWorkFlowProcessRequest.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			StringWriter w = new StringWriter();
			m.marshal(cdbWf, w);
			payload = w.toString();
			System.out.println(payload);
		} catch (JAXBException e) {
			log.error("Error building SOAP message", e);
		}

	}

}
