package com.ruoyi.framework.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Jimu报表 X-Frame-Options 过滤器
 * 允许积木报表页面在 iframe 中嵌入
 * 
 * @author ruoyi
 */
public class JimuReportFrameOptionsFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // 无需初始化
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        // 如果是积木报表路径，移除 X-Frame-Options 头，允许在 iframe 中嵌入
        if (requestURI != null && requestURI.startsWith("/jmreport/"))
        {
            // 使用 ResponseWrapper 来拦截并移除 X-Frame-Options 头
            HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpResponse)
            {
                @Override
                public void setHeader(String name, String value)
                {
                    // 如果是 X-Frame-Options 头，不设置（移除）
                    if (!"X-Frame-Options".equalsIgnoreCase(name))
                    {
                        super.setHeader(name, value);
                    }
                }

                @Override
                public void addHeader(String name, String value)
                {
                    // 如果是 X-Frame-Options 头，不添加（移除）
                    if (!"X-Frame-Options".equalsIgnoreCase(name))
                    {
                        super.addHeader(name, value);
                    }
                }
            };
            
            chain.doFilter(request, responseWrapper);
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy()
    {
        // 无需清理
    }
}

