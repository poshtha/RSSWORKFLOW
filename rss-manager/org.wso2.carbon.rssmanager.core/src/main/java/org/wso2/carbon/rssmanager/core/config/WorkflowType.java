package org.wso2.carbon.rssmanager.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.wso2.carbon.rssmanager.core.workflow.WorkflowConstants;
import org.wso2.carbon.rssmanager.core.workflow.WorkflowExecutor;

@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowType {

	@XmlElement(name = WorkflowConstants.WORKFLOW_TASK_NAME)
	private String name;

	@XmlElement(name = WorkflowConstants.WORKFLOW_TASK_ENABLED)
	private boolean enabled;

	@XmlElement(name = WorkflowConstants.WORKFLOW_TASK_EXECUTOR)
	private String executor;

	WorkflowExecutor wfExecutor;
	
	public WorkflowType(){
		
	}
	
	public WorkflowExecutor getExecutor() {
		return wfExecutor;
	}
	
	public String getExecutorName() {
		return executor;
	}
	
	public void setExecutorName(String exec) {
		this.executor = exec;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
	    this.name = name;
	}

	public boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}
	
	public void loadExecutor() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(executor == null || executor.isEmpty() ){
			executor = WorkflowConstants.WORKFLOW_GENERIC_EXEC;
		}
		 Class clazz = WorkflowType.class.getClassLoader().loadClass(executor);
		 wfExecutor = (WorkflowExecutor)clazz.newInstance();
	}

}