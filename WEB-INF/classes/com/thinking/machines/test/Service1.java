package com.thinking.machines.test;
import com.thinking.machines.webrock.annotation.*;
import com.thinking.machines.webrock.containers.*;
import java.util.*;
class User
{
public String name;
public int age;
}
@Path("/test/service1")
@InjectSessionScope
public class Service1
{
private SessionScope sessionScope;
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}
@Get
@Path("/one")
public String One(ApplicationScope applicationScope,ApplicationDirectory applicationDirectory,@RequestParameter(name="name") String name,RequestScope requestScope,ApplicationScope as)
{
System.out.println("Application Scope : "+applicationScope);
System.out.println("Application Directorty : "+applicationDirectory);
System.out.println("Request Scope : "+requestScope);
return name;
}
@Get
@Path("/login")
public String login(SessionScope sessionScope)
{
UUID uuid=UUID.randomUUID();
sessionScope.setAttribute("identity",uuid.toString());
System.out.println("Generated UUID : "+uuid.toString());
return "Welcome";
}
@Get
@Path("/check")
@SecuredAccess(checkPost="com.thinking.machines.test.Inspector",gaurd="inspect")
public String check()
{
return "Secured";
}

@Get
@Path("/two")
public String Two(@RequestParameter(name="age")int age,@RequestParameter(name="indian") boolean indian,@RequestParameter(name="username")String name)
{
System.out.println("Two");
return "Name : "+name+" Is Indian : "+indian+" Age : "+age;
}
@SecuredAccess(checkPost="com.thinking.machines.test.Inspector",gaurd="inspect")
@Get
@Path("/three")
public String Three(User user,ApplicationScope applicationScope,ApplicationDirectory applicationDirectory,RequestScope requestScope,ApplicationScope as)
{
System.out.println("Three");
System.out.println("Application Scope : "+applicationScope);
System.out.println("Application Directorty : "+applicationDirectory);
System.out.println("Request Scope : "+requestScope);
System.out.println(user);
return "hello";
}
}