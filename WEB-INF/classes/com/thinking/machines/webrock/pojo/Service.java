package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
import com.thinking.machines.webrock.enums.*;
import java.util.*;
public class Service
{
private Class classReference;
private Method method;
private String path;
private List<RequestMethod> requestMethods;
private String forwardTo;
private boolean isStartupService;
private int priorityFactor;
private List<Injection> injections; 
private List<AutoWire> autoWires;
private ForwardedFrom forwardedFrom;
private List<RequestParam> requestParameters;
private Gaurd gaurd;
public Service()
{
}
public void setGaurd(Gaurd gaurd)
{
this.gaurd=gaurd;
}
public Gaurd getGaurd()
{
return this.gaurd;
}
public void setRequestParameters(List<RequestParam> requestParameters)
{
this.requestParameters=requestParameters;
}
public List<RequestParam> getRequestParameters()
{
return this.requestParameters;
}

public void setForwardedFrom(ForwardedFrom forwardedFrom)
{
this.forwardedFrom=forwardedFrom;
}
public ForwardedFrom getForwardedFrom()
{
return this.forwardedFrom;
}
public void setAutoWires(List<AutoWire> autoWires)
{
this.autoWires=autoWires;
}
public List<AutoWire> getAutoWires()
{
return this.autoWires;
}
public List<Injection> getInjections()
{
return this.injections;
}
public void setInjections(List<Injection> injections)
{
this.injections=injections;
}
public boolean isStartupService()
{
return this.isStartupService;
}
public void setIsStartupService(boolean isStartupService)
{
this.isStartupService=isStartupService;
}
public void setPriorityFactor(int priorityFactor)
{
this.priorityFactor=priorityFactor;
}
public int getPriorityFactor()
{
return this.priorityFactor;
}
public void setClassReference(Class classReference)
{
this.classReference=classReference;
}
public Class getClassReference()
{
return this.classReference;
}
public void setMethod(Method method)
{
this.method=method;
}
public Method getMethod()
{
return this.method;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setForwardTo(String forwardTo)
{
this.forwardTo=forwardTo;
}
public String getForwardTo()
{
return this.forwardTo;
}
public void setRequestMethods(List<RequestMethod> requestMethods)
{
this.requestMethods=requestMethods;
}
public List<RequestMethod> getRequestMethods()
{
return this.requestMethods;
}
}