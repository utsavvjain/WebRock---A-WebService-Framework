package com.thinking.machines.webrock.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SecuredAccess
{
public String checkPost();
public String gaurd();
}