package com.thinking.machines.test;
import com.thinking.machines.webrock.exceptions.*;
import com.thinking.machines.webrock.containers.*;
public class Inspector
{
public void inspect(SessionScope sessionScope) throws com.thinking.machines.webrock.exceptions.SecurityException
{
if(sessionScope.getAttribute("identity")==null) throw new com.thinking.machines.webrock.exceptions.SecurityException("Not secured");
System.out.println("InDS : "+sessionScope.getAttribute("identity"));
}
}