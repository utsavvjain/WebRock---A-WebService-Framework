package com.thinking.machines.test;
import com.thinking.machines.webrock.annotation.*;
@Pojo
public class Student
{
private int rollNumber;
private String name;
private char gender;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
public void setGender(char gender)
{
this.gender=gender;
}
public char getGender()
{
return this.gender;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
}