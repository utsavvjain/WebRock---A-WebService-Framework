package com.thinking.machines.webrock;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.annotation.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.enums.*;
import com.thinking.machines.webrock.containers.*;
import com.thinking.machines.webrock.exceptions.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.google.gson.*;
public class WebRock extends HttpServlet
{
private WebRockModel webRockModel;
public void init(ServletConfig config)
{
this.webRockModel=(WebRockModel)config.getServletContext().getAttribute("webRockModel");
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
String paths[]=request.getRequestURI().split(request.getContextPath());
ApplicationScope applicationScope;
SessionScope sessionScope;
RequestScope requestScope;
ApplicationDirectory applicationDirectory;
if(paths.length==1)
{
response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
return;
}
ForwardedFrom forwardedFrom;
String path=paths[1];
PrintWriter printWriter=null;
Service service;
ServletContext servletContext=request.getServletContext();
HttpSession session=request.getSession();
try
{
printWriter=response.getWriter();
service=webRockModel.getService(path);
if(service==null)
{
response.setStatus(HttpServletResponse.SC_NOT_FOUND);
return;
}
List<RequestMethod> requestMethods=service.getRequestMethods();
List<Injection> injections=service.getInjections();
List<RequestParam> requestParameters=service.getRequestParameters();
if(requestMethods.contains(RequestMethod.valueOf(request.getMethod()))==false && requestMethods.size()>0)  response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
Object result,autoWiredObj;
String autoWirePropertyName;
Class cls=service.getClassReference();
Method method=service.getMethod();
Object obj=cls.newInstance();
InjectRequestParameter injectRequestParameter;
String parameterName,className;
Object args=null;
String reqParam;
for(Field field : cls.getDeclaredFields())
{
injectRequestParameter=field.getAnnotation(InjectRequestParameter.class);
if(injectRequestParameter!=null)
{
reqParam=request.getParameter(injectRequestParameter.value());
className=field.getType().getName();
if(className.equals("java.lang.Long") || className.equals("long")) args=Long.parseLong(reqParam); 
if(className.equals("java.lang.Integer") || className.equals("int")) args=Integer.parseInt(reqParam); 
if(className.equals("java.lang.Short") || className.equals("short")) args=Short.parseShort(reqParam); 
if(className.equals("java.lang.Byte") || className.equals("byte")) args=Byte.parseByte(reqParam); 
if(className.equals("java.lang.Double") || className.equals("double")) args=Double.parseDouble(reqParam);
if(className.equals("java.lang.Float") || className.equals("float")) args=Float.parseFloat(reqParam); 
if(className.equals("java.lang.Character") || className.equals("char")) args=reqParam.charAt(0); 
if(className.equals("java.lang.Boolean") || className.equals("boolean")) args=Boolean.parseBoolean(reqParam); 
if(className.equals("java.lang.String")) args=reqParam;
cls.getMethod("set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1),field.getType()).invoke(obj,args);
}
}
for(Injection injection:injections){
if(injection==Injection.APPLICATION_SCOPE) {
applicationScope=new ApplicationScope(servletContext);
cls.getMethod("set"+injection.toString(),applicationScope.getClass()).invoke(obj,applicationScope);
}
if(injection==Injection.SESSION_SCOPE) {
sessionScope=new SessionScope(session);
cls.getMethod("set"+injection.toString(),sessionScope.getClass()).invoke(obj,sessionScope);
}
if(injection==Injection.REQUEST_SCOPE){
requestScope=new RequestScope(request);
cls.getMethod("set"+injection.toString(),requestScope.getClass()).invoke(obj,requestScope);
}
if(injection==Injection.APPLICATION_DIRECTORY){
applicationDirectory=new ApplicationDirectory(request.getServletContext().getRealPath("/"));
cls.getMethod("set"+injection.toString(),applicationDirectory.getClass()).invoke(obj,applicationDirectory);
}
}
Gson gson=new Gson();
RequestParameter requestParameter;
Parameter methodParameter;
BufferedReader br=request.getReader();
StringBuffer sb=new StringBuffer();
String x;
int paramCount=method.getParameterCount();
Object reqArgs[]=new Object[paramCount];
int i=0;
Object jsonObject=null;
if(request.getContentType()!=null && request.getContentType().equals("application/json"))
{
for(Parameter mPara: method.getParameters())
{
className=mPara.getType().getName();
if(className.equals("ApplicationScope") || className.equals("com.thinking.machines.webrock.containers.ApplicationScope")) reqArgs[i]=new ApplicationScope(servletContext);
else if(className.equals("SessionScope") || className.equals("com.thinking.machines.webrock.containers.SessionScope")) reqArgs[i]=new SessionScope(session);
else if(className.equals("RequestScope") || className.equals("com.thinking.machines.webrock.containers.RequestScope")) reqArgs[i]=new RequestScope(request);
else if(className.equals("ApplicationDirectory") || className.equals("com.thinking.machines.webrock.containers.ApplicationDirectory")) reqArgs[i]=new ApplicationDirectory(request.getServletContext().getRealPath("/"));
else 
{
if(jsonObject!=null) throw new WebRockException("Cannot parse the passed json as there are two vars");
while(true)
{
x=br.readLine();
if(x==null) break;
sb.append(x);
}
String rawData=sb.toString();
jsonObject=gson.fromJson(rawData,mPara.getType());
reqArgs[i]=jsonObject;
}
i++;
}
if(service.getGaurd()!=null) checkSecurity(service,request,response);
result=method.invoke(obj,reqArgs);
response.setContentType("application/json");
printWriter.print(new Gson().toJson(result));
return;
}
else 
{
for(Parameter mPara: method.getParameters())
{
className=mPara.getType().getName();
if(className.equals("ApplicationScope") || className.equals("com.thinking.machines.webrock.containers.ApplicationScope")) reqArgs[i]=new ApplicationScope(servletContext);
if(className.equals("SessionScope") || className.equals("com.thinking.machines.webrock.containers.SessionScope")) reqArgs[i]=new SessionScope(session);
if(className.equals("RequestScope") || className.equals("com.thinking.machines.webrock.containers.RequestScope")) reqArgs[i]=new RequestScope(request);
if(className.equals("ApplicationDirectory") || className.equals("com.thinking.machines.webrock.containers.ApplicationDirectory")) reqArgs[i]=new ApplicationDirectory(request.getServletContext().getRealPath("/"));
i++;
}
}
for(RequestParam requestParam:requestParameters)
{
className=requestParam.getClassReference().getName();
reqParam=request.getParameter(requestParam.getPropertyName());
if(reqParam==null) continue;
i=requestParam.getArgsNumber();
if(className.equals("java.lang.Long") || className.equals("long")) reqArgs[i]=Long.parseLong(reqParam); 
if(className.equals("java.lang.Integer") || className.equals("int")) reqArgs[i]=Integer.parseInt(reqParam); 
if(className.equals("java.lang.Short") || className.equals("short")) reqArgs[i]=Short.parseShort(reqParam); 
if(className.equals("java.lang.Byte") || className.equals("byte")) reqArgs[i]=Byte.parseByte(reqParam); 
if(className.equals("java.lang.Double") || className.equals("double")) reqArgs[i]=Double.parseDouble(reqParam);
if(className.equals("java.lang.Float") || className.equals("float")) reqArgs[i]=Float.parseFloat(reqParam); 
if(className.equals("java.lang.Character") || className.equals("char")) reqArgs[i]=reqParam.charAt(0); 
if(className.equals("java.lang.Boolean") || className.equals("boolean")) reqArgs[i]=Boolean.parseBoolean(reqParam); 
if(className.equals("java.lang.String")) reqArgs[i]=reqParam;
}
for(AutoWire autoWire : service.getAutoWires()){
autoWirePropertyName=autoWire.getPropertyName();
autoWiredObj=request.getAttribute(autoWirePropertyName);
if(autoWiredObj==null) autoWiredObj=session.getAttribute(autoWirePropertyName);
if(autoWiredObj==null)  autoWiredObj=servletContext.getAttribute(autoWirePropertyName);
Class classRef=autoWire.getClassReference();
if(!classRef.isInstance(autoWiredObj)) continue;
cls.getMethod("set"+autoWirePropertyName.substring(0,1).toUpperCase()+autoWirePropertyName.substring(1),autoWire.getClassReference()).invoke(obj,autoWiredObj);
}
forwardedFrom=service.getForwardedFrom();
if(forwardedFrom!=null)
{
args=forwardedFrom.getResult();
if(service.getGaurd()!=null) checkSecurity(service,request,response);
if(args!=null) 
{
Class param1Type=method.getParameterTypes()[0];
if(!param1Type.isInstance(args)) throw new WebRockException("Type of object returned by service and argument of forwarded service doesn't match");
result=method.invoke(obj,args);
}
else result=method.invoke(obj);
}
else
{
if(service.getGaurd()!=null) checkSecurity(service,request,response);
if(paramCount>0) result=method.invoke(obj,reqArgs);
else result=method.invoke(obj);
}
String forwardTo=service.getForwardTo();
if(forwardTo==null)
{
if(result!=null)
{
response.setContentType("application/json");
printWriter.print(new Gson().toJson(result));
}
}
else
{
forwardedFrom=new ForwardedFrom();
forwardedFrom.setResult(result);
service=webRockModel.getService(forwardTo);
if(service!=null) service.setForwardedFrom(forwardedFrom);
RequestDispatcher requestDispatcher=request.getRequestDispatcher(forwardTo);
requestDispatcher.forward(request,response);
}
}catch(WebRockException exception)
{
response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
printWriter.println(exception.getMessage());
}
catch(Exception exception)
{
exception.printStackTrace();	
}
}
public void checkSecurity(Service service,HttpServletRequest request,HttpServletResponse response) 
{
ServletContext servletContext=request.getServletContext();
HttpSession session=request.getSession();
Gaurd gaurd=service.getGaurd();
ApplicationDirectory applicationDirectory;
ApplicationScope applicationScope;
RequestScope requestScope;
SessionScope sessionScope;
Object obj;
List<Injection> injections=service.getInjections();
try
{
Class cls=Class.forName(gaurd.getCheckPost());
obj=cls.newInstance();
/*
for(Injection injection:injections){
if(injection==Injection.APPLICATION_SCOPE) {
applicationScope=new ApplicationScope(servletContext);
cls.getMethod("set"+injection.toString(),applicationScope.getClass()).invoke(obj,applicationScope);
}
if(injection==Injection.SESSION_SCOPE) {
sessionScope=new SessionScope(session);
cls.getMethod("set"+injection.toString(),sessionScope.getClass()).invoke(obj,sessionScope);
}
if(injection==Injection.REQUEST_SCOPE){
requestScope=new RequestScope(request);
cls.getMethod("set"+injection.toString(),requestScope.getClass()).invoke(obj,requestScope);
}
if(injection==Injection.APPLICATION_DIRECTORY){
applicationDirectory=new ApplicationDirectory(request.getServletContext().getRealPath("/"));
cls.getMethod("set"+injection.toString(),applicationDirectory.getClass()).invoke(obj,applicationDirectory);
}
}
*/

Method method=null;
for(Method m:cls.getDeclaredMethods())
{
if(m.getName().equals(gaurd.getGaurd()))
{
method=m;
break;
}
}
Object reqArgs[]=new Object[method.getParameterCount()];
int i=0;
String className;
for(Parameter mPara: method.getParameters())
{
className=mPara.getType().getName();
if(className.equals("ApplicationScope") || className.equals("com.thinking.machines.webrock.containers.ApplicationScope")) reqArgs[i]=new ApplicationScope(servletContext);
if(className.equals("SessionScope") || className.equals("com.thinking.machines.webrock.containers.SessionScope")) reqArgs[i]=new SessionScope(session);
if(className.equals("RequestScope") || className.equals("com.thinking.machines.webrock.containers.RequestScope")) reqArgs[i]=new RequestScope(request);
if(className.equals("ApplicationDirectory") || className.equals("com.thinking.machines.webrock.containers.ApplicationDirectory")) reqArgs[i]=new ApplicationDirectory(request.getServletContext().getRealPath("/"));
i++;
}
method.invoke(obj,reqArgs);
}catch(InvocationTargetException ite){
Throwable t=ite.getCause();
try{
if(t instanceof com.thinking.machines.webrock.exceptions.SecurityException) response.sendError(404);
}catch(IOException exception)
{
exception.printStackTrace();
}
}catch(Exception exception)
{
exception.printStackTrace();
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
}