package com.thinking.machines.test;
import com.thinking.machines.webrock.annotation.*;
import com.thinking.machines.webrock.containers.*;
@InjectApplicationScope
public class Service3
{
private ApplicationScope applicationScope;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
}
@OnStartup(priority=1)
public void second()
{
System.out.println("On Startup 1");
}
@OnStartup(priority=5)
public void third()
{
System.out.println("On Startup 5");
}
@OnStartup(priority=5)
public void fourth()
{
System.out.println("On Startup 5");
}
}