package org.wso2.carbon.rssmanager.core.dto.restricted;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wso2.carbon.rssmanager.core.config.WFMessage;

public class Workflow {

	private Integer wfID;

	private String wfStatus;

	private String description;

	private int tenantId;

	private String tenantDomain;

	private String wfRefference;
	
	private String callbackURL;

	private String serviceEndpoint;

	private String type;
	
	private int resourceId;
		
	private long createdtime;

	private long updatetime;
	
    protected Map<String,String> parameters = new  HashMap(); 

	public Workflow() {
	}
	
	public String getStatus() {
		return wfStatus;
	}

	public void setStatus(String status) {
		this.wfStatus = status;
	}

	public int getTenantId() {
		return tenantId;
	}
	
	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getTenantDomain() {
		return tenantDomain;
	}
	
	public void getTenantDomain(String tenantDom) {
		this.tenantDomain= tenantDom;
	}

	public void setId(Integer id) {
		this.wfID = id;
	}

	public Integer getId() {
		return wfID;
	}

	public void setId(String id) {
		this.wfID = Integer.parseInt(id);
	}

	public String getDescribtion() {
		return description;
	}

	public void setDescribtion(String describtion) {
		this.description = describtion;
	}

	public String getWfRefference() {
		return wfRefference;
	}

	public void setWfRefference(String wfRefference) {
		this.wfRefference = wfRefference;
	}

	public String getCallbackURL() {
		return callbackURL;
	}

	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}
	
	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEP) {
		this.serviceEndpoint = serviceEP;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public long getCreateTime() {
		return createdtime;
	}

	public void setCreateTime(long time) {
		this.createdtime = time;
	}
	
	public long getUpdateTime() {
		return updatetime;
	}

	public void setUpdateTime(long time) {
		this.updatetime = time;
	}
	
	public int getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(int resId) {
		this.resourceId = resId;
	}
	
	public void addParameter(String name,String value) {
		this.parameters.put(name, value);
	}
	
	public String getParameter(String param) {
		return parameters.get(param);
	}
	
	public Map<String,String>  getParameterList() {
		return parameters;
	}
	
}
