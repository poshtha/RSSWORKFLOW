package org.wso2.carbon.rssmanager.core.workflow;

/**
 * Created by msffayaza on 10/18/14.
 */
public class WorkflowException extends Exception{

    public WorkflowException(String message){
        super(message);
    }

    public WorkflowException(String message, Throwable e){
        super(message, e);
    }
}
