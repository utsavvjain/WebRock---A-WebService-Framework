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
private void generateJSEqService(Class cls)
{
String path=((Path)cls.getAnnotation(Path.class)).value();
List<String> list=new LinkedList<>();
list.add(MessageFormat.format("class {0}",cls.getSimpleName()));
list.add("{");
list.add("constructor(){}");
String commaSepParamNames;
boolean isGetWithReqParam;
String requestString;
int i=0;
Parameter params[];
RequestParameter requestParameter;
String fullPath;
boolean isJson;
for(Method method : cls.getDeclaredMethods())
{
commaSepParamNames="";
requestString="";
if(!method.isAnnotationPresent(ServiceMethod.class)) continue;
isGetWithReqParam=false;
isJson=true;
params=method.getParameters();
if(method.isAnnotationPresent(Get.class)){
for(i=0;i<params.length;i++)
{
requestParameter=(RequestParameter)params[i].getAnnotation(RequestParameter.class);
if(requestParameter==null) continue;
isGetWithReqParam=true;
commaSepParamNames+=requestParameter.name();
requestString+=requestParameter.name()+"=${encodeURI("+requestParameter.name()+")}";
if(i!=params.length-1)
{
commaSepParamNames+=",";
requestString+="&";
}
}
list.add(method.getName()+"("+commaSepParamNames+"){");
list.add("return new Promise((resolve,reject)=>{");
fullPath=path+((Path)method.getAnnotation(Path.class)).value();
list.add(MessageFormat.format("fetch(`{0}?{1}`).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));",fullPath.substring(1),requestString));
list.add("})");
list.add("}");
}
if(method.isAnnotationPresent(Post.class))
{
for(Parameter param:method.getParameters()){
isJson=(!param.isAnnotationPresent(RequestParameter.class));
}
if(!isJson)
{
for(i=0;i<params.length;i++)
{
requestParameter=(RequestParameter)params[i].getAnnotation(RequestParameter.class);
if(requestParameter==null) continue;
isGetWithReqParam=true;
commaSepParamNames+=requestParameter.name();
requestString+=requestParameter.name()+"=${encodeURI("+requestParameter.name()+")}";
if(i!=params.length-1)
{
commaSepParamNames+=",";
requestString+="&";
}
}
list.add(method.getName()+"("+commaSepParamNames+"){");
list.add("return new Promise((resolve,reject)=>{");
fullPath=path+((Path)method.getAnnotation(Path.class)).value();
list.add("fetch(`"+fullPath.substring(1)+"`,{method : 'POST',headers : {'Content-Type' : 'application/x-www-form-urlencoded'},body : `"+requestString+"`}).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));");
list.add("})");
list.add("}");
}
else{
list.add(method.getName()+"(args0){");
list.add("return new Promise((resolve,reject)=>{");
fullPath=path+((Path)method.getAnnotation(Path.class)).value();
list.add("fetch(`"+fullPath.substring(1)+"`,{method : 'POST',headers : {'Content-Type' : 'application/json'},body : JSON.stringify(args0)}).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));");
list.add("})");
list.add("}");
}
}
//System.out.println(method.getName());
}
list.add("}");
try
{
String directoryPath=servletContext.getRealPath(".")+File.separator+"WEB-INF"+File.separator+"js";
File directory=new File(directoryPath);
if(!directory.exists()) directory.mkdirs();
String fileName=servletContext.getInitParameter("jsfile");
if(fileName==null) fileName=cls.getSimpleName()+".js";
path=directoryPath+File.separator+fileName;
File file=new File(path);
String fileContents="";
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
while(randomAccessFile.getFilePointer()<randomAccessFile.length()) fileContents+=randomAccessFile.readLine()+"";
if(fileContents.indexOf(cls.getSimpleName())!=-1){
randomAccessFile.close();
return;
}
randomAccessFile.seek(randomAccessFile.length());
for(String line : list)randomAccessFile.writeBytes(line);
randomAccessFile.close();
}catch(Exception exception)
{
exception.printStackTrace();
}
}
private void generateJSEqPojo(Class cls)
{
List<String> list=new LinkedList<>();
list.add(MessageFormat.format("class {0}",cls.getSimpleName()));
list.add("{");
Field []fields=cls.getDeclaredFields();
String fieldNameCommaSeperated="";
for(int i=0;i<fields.length;i++)
{
fieldNameCommaSeperated+=fields[i].getName();
if(i!=fields.length-1) fieldNameCommaSeperated+=",";
}
list.add(MessageFormat.format("constructor({0})",fieldNameCommaSeperated));
list.add("{");
for(int i=0;i<fields.length;i++) list.add(MessageFormat.format("this.{0}={0};",fields[i].getName(),fields[i].getName()));
list.add("}");
for(int i=0;i<fields.length;i++)
{
list.add(MessageFormat.format("get{0}()",fields[i].getName().substring(0,1).toUpperCase()+fields[i].getName().substring(1)));
list.add("{");
list.add(MessageFormat.format("return this.{0};",fields[i].getName()));
list.add("}");
}
list.add("}");
try{

String directoryPath=servletContext.getRealPath(".")+File.separator+"WEB-INF"+File.separator+"js";
File directory=new File(directoryPath);
if(!directory.exists()) directory.mkdirs();
String fileName=servletContext.getInitParameter("jsfile");
if(fileName==null) fileName=cls.getSimpleName()+".js";
String  path=directoryPath+File.separator+fileName;
File file=new File(path);
String fileContents="";
String data="";
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
while(randomAccessFile.getFilePointer()<randomAccessFile.length()) fileContents+=randomAccessFile.readLine();
for(String line : list) data+=line;
if(fileContents.indexOf(data)!=-1){
randomAccessFile.close();
return;
}
randomAccessFile.seek(randomAccessFile.length());
randomAccessFile.writeBytes(data);
randomAccessFile.close();
}catch(Exception exception){
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
if(file.isDirectory())
{
scanPackage(file.listFiles()) ;
continue;
}
path=file.getPath();
if(!file.getName().endsWith(".class")) continue;
className=MessageFormat.format("{0}.{1}",this.packagePrefix,file.getName()).replaceAll("\\\\",".");
cls=Class.forName(className.substring(0,className.indexOf(".class")));
if(cls.isAnnotationPresent(Pojo.class)) generateJSEqPojo(cls);
if(cls.isAnnotationPresent(JSService.class)) generateJSEqService(cls);
classPath=(Path)cls.getAnnotation(Path.class);
for(Method method: cls.getDeclaredMethods())
{
isStartupService=false;
service=new Service();
service.setClassReference(cls);
service.setMethod(method);
injections=new ArrayList<>();
if(cls.isAnnotationPresent(InjectApplicationScope.class)) injections.add(Injection.APPLICATION_SCOPE);
if(cls.isAnnotationPresent(InjectApplicationDirectory.class)) injections.add(Injection.APPLICATION_DIRECTORY);
if(cls.isAnnotationPresent(InjectSessionScope.class)) injections.add(Injection.SESSION_SCOPE);
if(cls.isAnnotationPresent(InjectRequestScope.class)) injections.add(Injection.REQUEST_SCOPE);
service.setInjections(injections);
// if a startup service
if(method.isAnnotationPresent(OnStartup.class) && method.getReturnType().getName().equalsIgnoreCase("void") && method.getParameterCount()==0) 
{
service.setIsStartupService(true);
service.setPriorityFactor(((OnStartup)method.getDeclaredAnnotation(OnStartup.class)).priority());
startupInvcQueue.add(service);
isStartupService=true;
}
// not a startup service
if(!isStartupService)
{
methodPath=(Path)method.getAnnotation(Path.class);
if(methodPath==null) continue;
requestMethods=new ArrayList<>();
if(cls.isAnnotationPresent(Get.class)) requestMethods.add(RequestMethod.GET);
if(cls.isAnnotationPresent(Post.class)) requestMethods.add(RequestMethod.POST);
if(methodPath!=null) url=classPath.value()+methodPath.value();
service.setPath(url);
if(method.isAnnotationPresent(Get.class) && requestMethods.contains(RequestMethod.GET)==false) requestMethods.add(RequestMethod.GET);
if(method.isAnnotationPresent(Post.class) && requestMethods.contains(RequestMethod.POST)==false) requestMethods.add(RequestMethod.POST);
service.setRequestMethods(requestMethods);
parameters=method.getParameters();
requestParameters=new ArrayList<>();
service.setRequestParameters(requestParameters);
int i=0;
//scanning params
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
}// for loop for fields
securedAccess=(SecuredAccess)method.getAnnotation(SecuredAccess.class);
if(securedAccess!=null)
{
gaurd=new Gaurd();
gaurd.setCheckPost(securedAccess.checkPost());
gaurd.setGaurd(securedAccess.gaurd());
service.setGaurd(gaurd);
}
service.setAutoWires(autoWires);
webRockModel.addService(url,service);
System.out.println("Path : "+service.getPath()+" Class Name : "+cls.getName()+" Method name : "+method.getName());
}// is not a startup service
}//methods for loop
}// file for



}//scan packages
}//class ends