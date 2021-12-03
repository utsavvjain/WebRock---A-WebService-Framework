package com.thinking.machines.webrock.enums;
public enum Injection{
SESSION_SCOPE("SessionScope"),APPLICATION_SCOPE("ApplicationScope"),APPLICATION_DIRECTORY("ApplicationDirectory"),REQUEST_SCOPE("RequestScope");
public String injection;
private Injection(String injection)
{
this.injection=injection;
}
public String toString()
{
return this.injection;
}
}