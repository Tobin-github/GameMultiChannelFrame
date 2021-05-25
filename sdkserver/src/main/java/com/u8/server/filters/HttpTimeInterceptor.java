package com.u8.server.filters;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpTimeInterceptor extends AbstractInterceptor {

    private static final Log log = LogFactory.getLog(HttpTimeInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();//计算开始日期
        String result = invocation.invoke();
        long executionTime = System.currentTimeMillis() - startTime;
        StringBuffer message = new StringBuffer(100);
        message.append("Executed action [");
        String namespace = invocation.getProxy().getNamespace();
        if ((namespace != null) && (namespace.trim().length() > 0)) {
            message.append(namespace).append("/");
        }
        message.append(invocation.getProxy().getActionName());
        message.append("!");
        message.append(invocation.getProxy().getMethod());
        message.append("] took ").append(executionTime).append(" ms.");
        if (log.isInfoEnabled()) {
            log.info(message.toString());
        }
        return result;
    }
}
