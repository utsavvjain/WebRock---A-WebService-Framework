package com.thinking.machines.webrock.containers;
import java.io.*;
public class ApplicationDirectory
{
private File file;
public ApplicationDirectory(String path)
{
file=new File(path);
}
public File getDirectory()
{
return this.file;
}
}