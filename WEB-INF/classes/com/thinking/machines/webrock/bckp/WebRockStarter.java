package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.annotation.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.enums.*;
import com.thinking.machines.webrock.containers.*;

import java.text.MessageFormat;
import java.io.*;
import java.lang.annotation.*;
import java.util.*;
import java.lang.reflect.*;
public class WebRockStarter implements ServletContextListener
{
private WebRockModel webRockModel;
private String packagePrefix;
PriorityQueue<Service> startupInvcQueue;
private ServletContext servletContext;
public WebRockStarter()
{
webRockModel=new WebRockModel();
this.packagePrefix="";
startupInvcQueue=new PriorityQueue<>((service1,service2)->{return service1.getPriorityFactor()-service2.getPriorityFactor();});
}
private void invokeStartupService(Service service) throws Exception
{
Method method=service.getMethod();
Class cls=service.getClassReference();
Object obj=cls.newInstance();
for(Injection injection:service.getInjections()){
if(injection==Injection.APPLICATION_SCOPE) {
ApplicationScope applicationScope=new ApplicationScope(servletContext);
cls.getMethod("set"+injection.toString(),applicationScope.getClass()).invoke(obj,applicationScope);
}
}
method.invoke(obj);
}
private void onStartup()
{
try
{
while(!startupInvcQueue.isEmpty()) invokeStartupService(startupInvcQueue.poll());
}catch(Exception exception)
{
exception.printStackTrace();
}
}

public void contextInitialized(ServletContextEvent servletContextEvent)
{
this.servletContext=servletContextEvent.getServletContext();
String packagePrefix=servletContext.getInitParameter("SERVICE_PACKAGE_PREFIX");
this.packagePrefix=packagePrefix.replaceAll("\\.","\\\\");
String directory=servletContext.getRealPath(File.separator)+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+this.packagePrefix;
try
{
scanPackage(new File(directory).listFiles());
servletContext.setAttribute("webRockModel",webRockModel);
onStartup();
}catch(Exception exception)
{
exception.printStackTrace();
}
}
public void contextDestroyed()
{
}
private void scanFunctions(Class cls)
{
for(Method method: cls.getDeclaredMethods())
{
requestMethods=new ArrayList<>();
if(cls.isAnnotationPresent(Get.class)) requestMethods.add(RequestMethod.GET);
if(cls.isAnnotationPresent(Post.class)) requestMethods.add(RequestMethod.POST);
if(method.isAnnotationPresent(OnStartup.class) && method.getReturnType().getName().equalsIgnoreCase("void") && method.getParameterCount()==0)
{
initallizeAsStartup();
}
if(method.isAnnotationPresent(OnStartup.class) && method.getReturnType().getName().equalsIgnoreCase("void") && method.getParameterCount()==0) 
{
service=new Service();
service.setClassReference(cls);
service.setMethod(method);
service.setIsStartupService(true);
service.setPriorityFactor(((OnStartup)method.getDeclaredAnnotation(OnStartup.class)).priority());
startupInvcQueue.add(service);

}
private void scanPackage(File[] files) throws Exception
{
String path;
String className;
Service service=null;
String url="",fieldName;
Path classPath,methodPath;
List<RequestMethod> requestMethods;
Class cls,methodReturnType;
int paramsCount;
Forward forwardTo;
boolean isStartupService=false;
List<Injection> injections;
List<AutoWire> autoWires=null;
List<RequestParam> requestParameters;
RequestParameter requestParameter;
RequestParam requestParam;
AutoWired autoWired;
SecuredAccess securedAccess;
Gaurd gaurd;
AutoWire autoWire;
Field fields[];
Parameter []parameters;
for(File file: files)
{
if(file.isDirectory()) scanPackage(file.listFiles()) ;
else
{
path=file.getPath();
if(!file.getName().endsWith(".class")) continue;
className=MessageFormat.format("{0}.{1}",this.packagePrefix,file.getName()).replaceAll("\\\\",".");
cls=Class.forName(className.substring(0,className.indexOf(".class")));
classPath=(Path)cls.getAnnotation(Path.class);
scanMethods(cls)

for(Method method: cls.getDeclaredMethods())
{
requestMethods=new ArrayList<>();
if(cls.isAnnotationPresent(Get.class)) requestMethods.add(RequestMethod.GET);
if(cls.isAnnotationPresent(Post.class)) requestMethods.add(RequestMethod.POST);
if(method.isAnnotationPresent(OnStartup.class) && method.getReturnType().getName().equalsIgnoreCase("void") && method.getParameterCount()==0) 
{
service=new Service();
service.setClassReference(cls);
service.setMethod(method);
service.setIsStartupService(true);
service.setPriorityFactor(((OnStartup)method.getDeclaredAnnotation(OnStartup.class)).priority());
startupInvcQueue.add(service);
isStartupService=true;
}
if(!isStartupService)
{
methodPath=(Path)method.getAnnotation(Path.class);
if(methodPath!=null) url=classPath.value()+methodPath.value();
service=new Service();
service.setClassReference(cls);
service.setMethod(method);
service.setPath(url);
System.out.println(cls.getName()+" , "+method.getName()+" , "+url);
if(method.isAnnotationPresent(Get.class) && requestMethods.contains(RequestMethod.GET)==false) requestMethods.add(RequestMethod.GET);
if(method.isAnnotationPresent(Post.class) && requestMethods.contains(RequestMethod.POST)==false) requestMethods.add(RequestMethod.POST);
service.setRequestMethods(requestMethods);
parameters=method.getParameters();
requestParameters=new ArrayList<>();
service.setRequestParameters(requestParameters);
int i=0;
for(Parameter param:parameters)
{
requestParameter=param.getAnnotation(RequestParameter.class);
if(requestParameter!=null)
{
requestParam=new RequestParam();
requestParam.setClassReference(param.getType());
requestParam.setPropertyName(requestParameter.name());
requestParam.setArgsNumber(i);
requestParameters.add(requestParam);
}
i++;
}
forwardTo=(Forward)method.getAnnotation(Forward.class);
if(forwardTo!=null) service.setForwardTo(forwardTo.value());
webRockModel.addService(url,service);
}
if(service!=null){
injections=new ArrayList<>();
if(cls.isAnnotationPresent(InjectSessionScope.class)) injections.add(Injection.SESSION_SCOPE);
if(cls.isAnnotationPresent(InjectRequestScope.class)) injections.add(Injection.REQUEST_SCOPE);
if(cls.isAnnotationPresent(InjectApplicationScope.class)) injections.add(Injection.APPLICATION_SCOPE);
if(cls.isAnnotationPresent(InjectApplicationDirectory.class)) injections.add(Injection.APPLICATION_DIRECTORY);
service.setInjections(injections);
autoWires=new ArrayList<>();
fields=cls.getDeclaredFields();
for(int fIndex=0;fIndex<fields.length;fIndex++)
{
autoWired=(AutoWired)fields[fIndex].getAnnotation(AutoWired.class);
if(autoWired!=null)
{
autoWire=new AutoWire();
autoWire.setClassReference(fields[fIndex].getType());
fieldName=autoWired.name();
if(fieldName.length()==0) fieldName=fields[fIndex].getName();
autoWire.setPropertyName(fieldName);
autoWires.add(autoWire);
}
} // for loop for methods
}//fields for loop
service.setAutoWires(autoWires);
securedAccess=(SecuredAccess)method.getAnnotation(SecuredAccess.class);
if(securedAccess!=null)
{
gaurd=new Gaurd();
gaurd.setCheckPost(securedAccess.checkPost());
gaurd.setGaurd(securedAccess.gaurd());
service.setGaurd(gaurd);
}
}//if service
} //else block
}// file for

}
}