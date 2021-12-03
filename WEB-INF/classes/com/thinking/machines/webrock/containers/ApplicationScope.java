package com.thinking.machines.webrock.containers;
import javax.servlet.*;
import javax.servlet.http.*;
public class ApplicationScope
{
private ServletContext servletContext;
public ApplicationScope(ServletContext servletContext)
{
this.servletContext=servletContext;
}
public Object getAttribute(String key)
{
return this.servletContext.getAttribute(key);
}
public void setAttribute(String key,Object value)
{
this.servletContext.setAttribute(key,value);
}
}