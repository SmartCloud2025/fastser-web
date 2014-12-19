/**
 * Copyright@xiaocong.tv 2012
 */
package org.fastser.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.fastser.web.constant.RestConstant;

/**
 * @author weijun.ye
 * @version
 * @date 2012-3-31
 */
public class ResourceFilter implements Filter {
	
	private static boolean domainCross = false;

    /*
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // ignore
    }

    /*
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if(domainCross){
        	response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,token");
            response.addHeader("Access-Control-Max-Age", "600000");
        }

        // jsonp
        if (RestConstant.REQ_METHOD_GET.equals(request.getMethod())) {
            String callBack = request.getParameter(RestConstant.RESP_CALLBACK);
            if (StringUtils.isNotEmpty(callBack)) {
                ResourceResponseWrapper wapper = new ResourceResponseWrapper(response);
                chain.doFilter(req, wapper);
                byte[] json = wapper.getResponseData();
                StringBuffer jsonStr = new StringBuffer(new String(json, "UTF-8"));
                jsonStr.insert(0, callBack + "(");
                jsonStr.append(")");
                ServletOutputStream output = response.getOutputStream();
                output.write(jsonStr.toString().getBytes("UTF-8"));
                output.flush();
            } else {
                chain.doFilter(req, response);
            }
        } else {
            chain.doFilter(req, response);
        }

    }

    /*
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig cf) throws ServletException {
    	String domain = cf.getInitParameter("domainCross");
    	if(StringUtils.isNotEmpty(domain)){
    		domainCross = Boolean.valueOf(domain);
    	}
    }

}
