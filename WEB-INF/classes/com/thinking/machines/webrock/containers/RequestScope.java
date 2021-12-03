package com.thinking.machines.webrock.containers;
import javax.servlet.*;
import javax.servlet.http.*;
public class RequestScope
{
private HttpServletRequest httpServletRequest;
public RequestScope(HttpServletRequest httpServletRequest)
{
this.httpServletRequest=httpServletRequest;
}
public Object getAttribute(String key)
{
return this.httpServletRequest.getAttribute(key);
}
public void setAttribute(String key,Object value)
{
this.httpServletRequest.setAttribute(key,value);
}
}