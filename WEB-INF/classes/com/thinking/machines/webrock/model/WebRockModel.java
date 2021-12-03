package com.thinking.machines.webrock.model;
import com.thinking.machines.webrock.pojo.*;
import java.util.*;
public class WebRockModel
{
private Map<String,Service> services;
public WebRockModel()
{
services=new HashMap<>();
}
public void addService(String path,Service service)
{
this.services.put(path,service);
}
public Service getService(String path)
{
return this.services.get(path);
}
}