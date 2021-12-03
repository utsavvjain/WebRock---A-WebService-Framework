package com.thinking.machines.webrock.pojo;
public class RequestParam
{
private Class classReference;
private String propertyName;
private int argsNumber;
public RequestParam()
{
}
public void setArgsNumber(int argsNumber)
{
this.argsNumber=argsNumber;
}
public int getArgsNumber()
{
return this.argsNumber;
}
public void setClassReference(Class classReference)
{
this.classReference=classReference;
}
public void setPropertyName(String name)
{
this.propertyName=name;
}
public Class getClassReference()
{
return this.classReference;
}
public String getPropertyName()
{
return this.propertyName;
}
}