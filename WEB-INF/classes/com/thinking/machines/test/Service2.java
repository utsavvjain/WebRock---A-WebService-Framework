package com.thinking.machines.test;
import com.thinking.machines.webrock.annotation.*;
@Path("/test/service2")
public class Service2
{
@InjectRequestParameter("user")
private String user;
public void setUser(String user)
{
this.user=user;
}
@Get
@Path("/two")
public String Two()
{
return this.user;
}
}