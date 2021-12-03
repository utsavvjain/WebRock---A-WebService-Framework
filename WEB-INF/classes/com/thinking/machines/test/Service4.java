package com.thinking.machines.test;
import com.thinking.machines.webrock.annotation.*;
import com.thinking.machines.webrock.containers.*;
@Path("/test/service4")
public class Service4
{
@Path("/one")
public String one()
{
return "service4-one";
}
}