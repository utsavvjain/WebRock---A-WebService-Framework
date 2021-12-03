package com.thinking.machines.webrock.pojo;
public class AutoWire
{
private Class classReference;
private String propertyName;
public AutoWire()
{
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