package com.thinking.machines.webrock.containers;
import javax.servlet.*;
import javax.servlet.http.*;
public class SessionScope
{
private HttpSession httpSession;
public SessionScope(HttpSession httpSession)
{
this.httpSession=httpSession;
}
public Object getAttribute(String key)
{
return this.httpSession.getAttribute(key);
}
public void setAttribute(String key,Object value)
{
this.httpSession.setAttribute(key,value);
}
}