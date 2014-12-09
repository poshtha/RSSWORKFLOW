package org.wso2.carbon.rssmanager.core.config;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.wso2.carbon.rssmanager.core.workflow.WorkflowConstants;
import org.wso2.carbon.rssmanager.core.workflow.WorkflowExecutor;

@XmlRootElement(name = WorkflowConstants.WORKFLOW_CONFIG)
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowExecutorConfig {

	@XmlElement(name = WorkflowConstants.WORKFLOW_ENDPOINT)
	private String serviceEndpoint;

	@XmlElement(name = WorkflowConstants.WORKFLOW_CALLBACK)
	private String callbackURL;

	@XmlElement(name = WorkflowConstants.WORKFLOW_GLOBAL_USER)
	private String globalUsername;

	@XmlElement(name = WorkflowConstants.WORKFLOW_GLOBAL_PASSWORD)
	private String globalPassword;
	
	@XmlElement(name = WorkflowConstants.WORKFLOW_GLOBAL_ENABLED)
	private boolean globalEnabled;

	@XmlElementWrapper(name = WorkflowConstants.WORKFLOW_TASKS, nillable = false)
	@XmlElement(name = WorkflowConstants.WORKFLOW_TASK)
	private List<WorkflowType> tasks;

	public List<WorkflowType> getTasks() {
		return tasks;
	}

	public void setTasks(List<WorkflowType> tasklist) {
		tasks = tasklist;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String endp) {
		this.serviceEndpoint = endp;
	}
	
	public String getCallbackURL() {
		return callbackURL;
	}

	public void setCallbackURL(String call) {
		this.callbackURL = call;
	}
	
	public String getGlobalUsername() {
		return globalUsername;
	}
	
	public void setGlobalUsername(String uname) {
		this.globalUsername = uname;
	}

	public String getGlobalPassword() {
		return globalPassword;
	}
	
	public void setGlobalPassword(String pwd) {
		this.globalPassword = pwd;
	}
	
	public boolean getGlobalEnabled() {
		return globalEnabled;
	}
	
	public void setGlobalEnabled(boolean en) {
		this.globalEnabled = en;
	}
	
	
	public WorkflowExecutor getWFExecutor(String task){
		WorkflowExecutor wfExec = null;
		for(WorkflowType taskI : tasks){
			if(taskI.getName().equals(task)){
				wfExec = taskI.getExecutor();
			}
		}
		return wfExec;
	}
	
	public boolean getWFEnabled(String task){
		boolean enabled = false;
		for(WorkflowType taskI : tasks){
			if(taskI.getName().equals(task)){
				enabled = taskI.getEnabled();
			}
		}
		return enabled;
	}

}

