package org.wso2.carbon.rssmanager.core.workflow;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.rssmanager.common.RSSManagerConstants;
import org.wso2.carbon.rssmanager.core.config.RSSConfig;
import org.wso2.carbon.rssmanager.core.config.WFMessage;
import org.wso2.carbon.rssmanager.core.config.WorkflowExecutorConfig;
import org.wso2.carbon.rssmanager.core.config.WorkflowType;
import org.wso2.carbon.rssmanager.core.config.WFMessage.Parameter;
import org.wso2.carbon.rssmanager.core.dto.restricted.Workflow;
import org.wso2.carbon.rssmanager.core.exception.RSSManagerException;
import org.wso2.carbon.rssmanager.core.internal.RSSManagerDataHolder;
import org.wso2.carbon.rssmanager.core.util.RSSManagerUtil;
import org.wso2.carbon.rssmanager.core.workflow.DatabaseCreationWSWorkflowExecutor.CreateDBApprovalWorkFlowProcessRequest;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowConfigFactory implements Serializable {

	private static final Log log = LogFactory.getLog(WorkflowConfigFactory.class);

	private static final QName PROP_Q = new QName("Property");

	private static final QName ATT_NAME = new QName("name");

	private Map<String, WorkflowExecutor> workflowExecutorMap = new HashMap(); ;

	private static WorkflowConfigFactory thisInstance = new WorkflowConfigFactory();

	public WorkflowConfigFactory() {
		
	}

	public WorkflowExecutor getWorkflowExecutor(String workflowExecutorType) {
		return workflowExecutorMap.get(workflowExecutorType);
	}

	public static WorkflowConfigFactory getInstance() {
		return thisInstance;
	}
	
	public void addWorkflow(Workflow wfDTO){
		
	}

	public void load(CarbonContext carbonContext) {

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
			wfConfig.setTasks(wfTasks);

		//	RSSManagerDataHolder.getInstance().addWorkFlowConfig(Integer.toString(tid), wfConfig);

		} catch (RegistryException e) {
			log.error("Error accessing tenant registry", e);
		} catch (JAXBException e) {
			log.error("Error accessing reading configuration", e);
		} catch (Exception e) {
			log.error("Error loading executor", e);
		}
	}


	private static void handleException(String msg) throws WorkflowException {
		log.error(msg);
		throw new WorkflowException(msg);
	}

	private static void handleException(String msg, Exception e) throws WorkflowException {
		log.error(msg, e);
		throw new WorkflowException(msg, e);
	}
	
	
	public static void main(String args[]){
		
		DatabaseCreationWSWorkflowExecutor db = new DatabaseCreationWSWorkflowExecutor();
		Workflow w = new Workflow();
		w.setCallbackURL("http://");
		w.setTenantId(2134321);
		w.setType("RSS");
		w.addParameter("user", "bob");
	//	db.buildSOAPMessage(w);
		
		WorkflowType wft ;
		List<WorkflowType> wftasks = new ArrayList<WorkflowType>();

		wft = new WorkflowType();
		wft.setName("createdb");
		wft.setExecutorName("org.exec");
		wft.setEnabled(true);
		wftasks.add(wft);
		
		wft = new WorkflowType();
		wft.setName("deletdb");
		wft.setExecutorName("org.exec.del");
		wft.setEnabled(true);
		wftasks.add(wft);
		
		WorkflowExecutorConfig wfconf = new WorkflowExecutorConfig();
		wfconf.setCallbackURL("http://local");
		wfconf.setServiceEndpoint("http://end");
		wfconf.setGlobalUsername("user");
		wfconf.setGlobalPassword("pass");
        wfconf.setTasks(wftasks);
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(WorkflowExecutorConfig.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    StringWriter wr = new StringWriter();
			m.marshal(wfconf, wr);
            System.out.println(wr.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}
}
