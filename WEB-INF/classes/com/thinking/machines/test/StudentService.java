package com.thinking.machines.test;
import com.thinking.machines.webrock.annotation.*;
import java.sql.*;
import java.util.*;
@Path("/test/students")
@JSService
public class StudentService
{
private Connection getDBConnection()
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/hrdb","hr","hr");
}catch(Exception exception)
{
exception.printStackTrace();
}
return connection;
} 
@Get
@Path("/welcome")
public String welcome()
{
return "welcome to students api";
}
@ServiceMethod
@Post
@Path("/delete")
public Map<String,Object> delete(@RequestParameter(name="rollNumber") int rollNumber)
{
PreparedStatement preparedStatement;
ResultSet resultSet;
Map<String,Object> response=new HashMap<>();
try
{
Connection connection=getDBConnection();
preparedStatement=connection.prepareStatement("select gender from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next()) 
{
response.put("success",false);
response.put("error","Roll number does not exists");
resultSet.close();
preparedStatement.close();
connection.close();
return response;
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("delete from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
preparedStatement.executeUpdate();
response.put("success",true);
return response;
}catch(Exception exception)
{
exception.printStackTrace();
response.put("success",false);
response.put("error",exception.getMessage());
return response;
}
}
@ServiceMethod
@Get
@Path("/get")
public Map<String,Object> get(@RequestParameter(name="rollNumber") int rollNumber)
{
PreparedStatement preparedStatement;
ResultSet resultSet;
Map<String,Object> response=new HashMap<>();
try
{
Connection connection=getDBConnection();
preparedStatement=connection.prepareStatement("select * from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next()) 
{
response.put("success",false);
response.put("error","Roll number does not exists");
resultSet.close();
preparedStatement.close();
connection.close();
return response;
}
Student student=new Student();
student.setRollNumber(resultSet.getInt("roll_number"));
student.setName(resultSet.getString("name"));
student.setGender(resultSet.getString("gender").charAt(0));
resultSet.close();
preparedStatement.close();
connection.close();
response.put("success",true);
response.put("student",student);
return response;
}catch(Exception exception)
{
exception.printStackTrace();
response.put("success",false);
response.put("error",exception.getMessage());
return response;
}
}
@ServiceMethod
@Post
@Path("/add")
public boolean add(Student student)
{
try
{
Connection connection=getDBConnection();
PreparedStatement preparedStatement=connection.prepareStatement("insert into student (name,roll_number,gender) values (?,?,?)");
preparedStatement.setString(1,student.getName());
preparedStatement.setInt(2,student.getRollNumber());
preparedStatement.setString(3,student.getGender()+"");
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
return true;
}catch(Exception exception)
{
exception.printStackTrace();
return false;
}
}
@ServiceMethod
@Post
@Path("/update")
public Map<String,Object> update(Student student)
{
PreparedStatement preparedStatement;
ResultSet resultSet;
Map<String,Object> response=new HashMap<>();
try
{
Connection connection=getDBConnection();
preparedStatement=connection.prepareStatement("select gender from student where roll_number=?");
preparedStatement.setInt(1,student.getRollNumber());
resultSet=preparedStatement.executeQuery();
if(!resultSet.next()) 
{
response.put("success",false);
response.put("error","Roll number does not exists");
resultSet.close();
preparedStatement.close();
connection.close();
return response;
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("update student set name=?,gender=? where roll_number=?");
preparedStatement.setString(1,student.getName());
preparedStatement.setInt(3,student.getRollNumber());
preparedStatement.setString(2,student.getGender()+"");
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
response.put("success",true);
return response;
}catch(Exception exception)
{
exception.printStackTrace();
response.put("success",false);
response.put("error",exception.getMessage());
return response;
}
}
@ServiceMethod
@Get
@Path("/all")
public List<Student> getAll()
{
List<Student> students=new ArrayList<>();
try
{
String name;
int rollNumber;
char gender;
Student student;
Connection connection=getDBConnection();
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select * from student");
while(resultSet.next())
{
student=new Student();
student.setRollNumber(resultSet.getInt("roll_number"));
student.setName(resultSet.getString("name"));
student.setGender(resultSet.getString("gender").charAt(0));
students.add(student);
}
resultSet.close();
statement.close();
connection.close();
}catch(Exception exception)
{
exception.printStackTrace();
}
return students;
}
}