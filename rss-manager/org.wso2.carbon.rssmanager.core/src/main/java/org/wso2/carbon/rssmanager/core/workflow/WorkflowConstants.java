package org.wso2.carbon.rssmanager.core.workflow;

import java.sql.PreparedStatement;

import org.wso2.carbon.rssmanager.core.util.RSSManagerUtil;

/**
 * Created by msffayaza on 10/18/14.
 */
public class WorkflowConstants {

	public static final String WORKFLOW_CONFIG = "workflowconfig";

	public static final String WORKFLOW_ENDPOINT = "serviceendpoint";

	public static final String WORKFLOW_CALLBACK = "callbackurl";

	public static final String WORKFLOW_GLOBAL_USER = "username";

	public static final String WORKFLOW_GLOBAL_PASSWORD = "password";

	public static final String WORKFLOW_GLOBAL_ENABLED = "enabled";

	public static final String WORKFLOW_TASK = "task";

	public static final String WORKFLOW_TASKS = "tasks";

	public static final String WORKFLOW_TASK_NAME = "name";

	public static final String WORKFLOW_TASK_ENABLED = "enabled";

	public static final String WORKFLOW_TASK_EXECUTOR = "executor";

	public static final String WORKFLOW_TASK_ENDPOINT = "endpoint";

	public static final String WORKFLOW_TASK_CALLBACK = "callback";

	public static final String WORKFLOW_TASK_USERNAME = "username";

	public static final String WORKFLOW_TASK_PASSWORD = "password";

	public static final String PASSWORD = "Password";

	public static final String PASSWORD_ = "password";

	public static final String WORKFLOW_CREATED = "CREATED";

	public static final String WORKFLOW_APPROVED = "APPROVED";

	public static final String WORKFLOW_REJECT = "REJECT";

	public static final String WORKFLOW_MES_DESC = "description";

	public static final String WORKFLOW_MES_WFID = "workflowID";

	public static final String WORKFLOW_MES_WFREF = "workflowExtReference";

	public static final String WORKFLOW_MES_ENV = "wor:environment";

	public static final String WORKFLOW_MES_CALL = "callBackURL";

	public static final String WORKFLOW_GENERIC_EXEC =
	                                                   "org.wso2.carbon.rssmanager.core.workflow.GenericWFExecutor";

	public static final String WORKFLOW_RSS_DB_ADD = "rss_adddatabase";

	public static final String WF_PAR_TDOM = "Tenant Domain";

	public static final String WF_PAR_RSS_INS_NAME = "RSS Instance Name";

	public static final String WF_PAR_RSS_INS_TYPE = "RSS Instance Type";

	public static final String WF_PAR_USERNAME = "Username";

	public static final PreparedStatement WORKFLOW_NATIVE_STMNT = null;

}
