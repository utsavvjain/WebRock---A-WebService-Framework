package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.google.gson.*;
public class JSFileServer extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String fileName=request.getParameter("name");
if(fileName==null) {
response.sendError(HttpServletResponse.SC_BAD_REQUEST);
return;
}
String jsPath=request.getServletContext().getRealPath(".")+File.separator+"WEB-INF"+File.separator+"js"+File.separator+fileName;
File file=new File(jsPath);
if(!file.exists())
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
PrintWriter pw=response.getWriter();
while(randomAccessFile.getFilePointer()<randomAccessFile.length()) pw.print(randomAccessFile.readLine());
randomAccessFile.close();
}catch(Exception exception)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(IOException ioException)
{
ioException.printStackTrace();
}
exception.printStackTrace();
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
}