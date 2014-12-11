package org.wso2.carbon.rssmanager.core.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.utils.UUIDGenerator;
import org.apache.axiom.om.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.rssmanager.core.config.WFMessage;
import org.wso2.carbon.rssmanager.core.config.WorkflowExecutorConfig;
import org.wso2.carbon.rssmanager.core.config.WorkflowType;
import org.wso2.carbon.rssmanager.core.dto.restricted.Workflow;
import org.wso2.carbon.rssmanager.core.internal.RSSManagerDataHolder;
import org.wso2.carbon.rssmanager.core.util.RSSManagerUtil;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class WorkflowManager {

	private static final Log log = LogFactory.getLog(WorkflowManager.class);

	private static WorkflowManager instance;

	private static DataSource dataSource;

	private WorkflowConfigFactory config;

	private WorkflowManager() {
		dataSource = RSSManagerUtil.getDataSource();
	}

	public static WorkflowManager getInstance() {
		if (instance == null) {
			instance = new WorkflowManager();
		}
		return instance;
	}

	public String generateWFID() {
		String UUID = UUIDGenerator.generateUUID();
		return UUID;
	}

	public void load() {

		int tid;

		try {
			tid = RSSManagerDataHolder.getInstance().getTenantId();
			Registry registry = RSSManagerDataHolder.getInstance().getRegistry();
			Resource resource = registry.get(WorkflowConstants.WORKFLOW_CONFIG);

			InputStream in = resource.getContentStream();

			JAXBContext context = JAXBContext.newInstance(WorkflowExecutorConfig.class);
			Unmarshaller um = context.createUnmarshaller();
			WorkflowExecutorConfig wfConfig = (WorkflowExecutorConfig) um.unmarshal(in);
			List<WorkflowType> wfTasks = wfConfig.getTasks();
			for (WorkflowType task : wfTasks) {
				task.loadExecutor();
				task.getExecutor().setWFTask(task.getName());
			}

			RSSManagerDataHolder.getInstance().addWorkFlowConfig(tid, wfConfig);

		} catch (RegistryException e) {
			log.error("Error accessing tenant registry", e);
		} catch (JAXBException e) {
			log.error("Error accessing reading configuration", e);
		} catch (Exception e) {
			log.error("Error loading executor", e);
		}
	}

	public boolean isTenentWFEnabled(int tid) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getGlobalEnabled();
	}

	public boolean isTaskWFEnabled(int tid, String task) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getWFEnabled(task);
	}

	public String getServiceEndpoint(int tid) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getServiceEndpoint();
	}

	public String getCallbackURL(int tid) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getCallbackURL();
	}

	public String getUsername(int tid) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getGlobalUsername();
	}

	public String getPassword(int tid) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getGlobalPassword();
	}

	private static void handleException(String msg) throws WorkflowException {
		log.error(msg);
		throw new WorkflowException(msg);
	}

	public WorkflowExecutor getWorkflowExecutor(int tid, String task) {
		return RSSManagerDataHolder.getInstance().getWorkFlowConfig(tid).getWFExecutor(task);
	}

	public void updateWFStatus(int wfId, String status) {

		Connection conn = null;
		PreparedStatement updateWFStatusStatement = null;
		try {
			conn = dataSource.getConnection();
			String updateWFStatusQuery =
			                             "UPDATE RM_WORKFLOW SET STATUS=?, UPDATE_TIME=NOW() WHERE WFID = ?";
			updateWFStatusStatement = conn.prepareStatement(updateWFStatusQuery);
			updateWFStatusStatement.setString(1, status);
			updateWFStatusStatement.setInt(2, wfId);
			updateWFStatusStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Error updating worklow status", e);
		} finally {
			closeStatement(updateWFStatusStatement, WorkflowConstants.WORKFLOW_CREATED);
			closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
		}
	}

	public String getWFStatus(int wfId) {

		Connection conn = null;
		PreparedStatement getWFStatusStatement = null;
		ResultSet resultSet = null;
		String status = "";
		try {
			conn = dataSource.getConnection();
			String getWFStatusQuery = "SELECT STATUS FROM RM_WORKFLOW WHERE WFID = ?";
			getWFStatusStatement = conn.prepareStatement(getWFStatusQuery);
			getWFStatusStatement.setInt(1, wfId);
			resultSet = getWFStatusStatement.executeQuery();
			while (resultSet.next()) {
				status = resultSet.getString("STATUS");
			}
		} catch (SQLException e) {
			log.error("Error retrieving worklow status", e);
		} finally {
			closeStatement(getWFStatusStatement, WorkflowConstants.WORKFLOW_CREATED);
			closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
		}
		return status;
	}

	public int getWFResourceID(int wfId) {

		Connection conn = null;
		PreparedStatement getWFStatusStatement = null;
		ResultSet resultSet = null;
		int resId = 0;
		try {
			conn = dataSource.getConnection();
			String getWFStatusQuery = "SELECT RESOURCE_ID FROM RM_WORKFLOW WHERE WFID = ?";
			getWFStatusStatement = conn.prepareStatement(getWFStatusQuery);
			getWFStatusStatement.setInt(1, wfId);
			resultSet = getWFStatusStatement.executeQuery();
			while (resultSet.next()) {
				resId = resultSet.getInt("RESOURCE_ID");
			}
		} catch (SQLException e) {
			log.error("Error retrieving worklow status", e);
		} finally {
			closeStatement(getWFStatusStatement, WorkflowConstants.WORKFLOW_CREATED);
			closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
		}
		return resId;
	}

	public String getWFStatusByRID(int resId, String resType) {

		Connection conn = null;
		PreparedStatement getWFStatusStatement = null;
		ResultSet resultSet = null;
		String status = "NONE";
		try {
			conn = dataSource.getConnection();
			String getWFStatusQuery = "SELECT STATUS FROM RM_WORKFLOW WHERE RESOURCE_ID = ? AND RESOURCE_TYPE = ?";
			getWFStatusStatement = conn.prepareStatement(getWFStatusQuery);
			getWFStatusStatement.setInt(1, resId);
			getWFStatusStatement.setString(2, resType);;
			resultSet = getWFStatusStatement.executeQuery();
			while (resultSet.next()) {
				status = resultSet.getString("STATUS");
			}
		} catch (SQLException e) {
			log.error("Error retrieving worklow status", e);
		} finally {
			closeStatement(getWFStatusStatement, WorkflowConstants.WORKFLOW_CREATED);
			closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
		}
		return status;
	}

	public void createWorkflow(Workflow wfInfo) {
		Connection conn = null;
		PreparedStatement addWFStatement = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			String addWFQuery =
			                    "INSERT INTO RM_WORKFLOW(WFID, TYPE , TENANT_ID, TENANT_DOMAIN, CREATE_TIME, UPDATE_TIME, RESOURCE_ID, STATUS, DESCRIPTION, RESOURCE_TYPE) VALUES (?,?,?,?,NOW(),NOW(),?,?,?)";
			addWFStatement = conn.prepareStatement(addWFQuery);
			addWFStatement.setInt(1, wfInfo.getId());
			addWFStatement.setString(2, wfInfo.getType());
			addWFStatement.setInt(3, wfInfo.getTenantId());
			addWFStatement.setString(4, wfInfo.getTenantDomain());
			addWFStatement.setInt(5, wfInfo.getResourceId());
			addWFStatement.setString(6, wfInfo.getStatus());
			addWFStatement.setString(7, wfInfo.getDescribtion());
			addWFStatement.setString(8, wfInfo.getResourceType());
			addWFStatement.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			rollback(conn, WorkflowConstants.WORKFLOW_CREATED);
			log.error("Error inserting workflow details", e);
		} finally {
			closeStatement(addWFStatement, WorkflowConstants.WORKFLOW_CREATED);
			closeConnection(conn, WorkflowConstants.WORKFLOW_CREATED);
		}
	}

	/**
	 * Close database connection
	 * 
	 * @param connection
	 *            database connection
	 * @param task
	 *            task which was executed before closing the connection
	 */
	public void closeConnection(Connection connection, String task) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.error("Failed to close connection after " + task, e);
			}
		}
	}

	/**
	 * Roll back database updates on error
	 * 
	 * @param connection
	 *            database connection
	 * @param task
	 *            task which was executing at the error.
	 */
	public void rollback(Connection connection, String task) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.warn("Rollback failed on " + task);
			}
		}
	}

	/**
	 * Close the prepared statement
	 * 
	 * @param preparedStatement
	 *            PreparedStatement
	 * @param task
	 *            task which was executed before closing the prepared statement.
	 */
	public void closeStatement(PreparedStatement preparedStatement, String task) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("Closing prepared statement failed after " + task, e);
			}
		}
	}

	/**
	 * Closes the result set
	 * 
	 * @param resultSet
	 *            ResultSet
	 * @param task
	 *            task which was executed before closing the result set.
	 */
	public void closeResult(ResultSet resultSet, String task) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				log.error("Closing result set failed after " + task);
			}
		}
	}

}