package com.example.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {

// 覆写service方法

    @Override

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

// 1. 获取URL或者URI

        String requestURI = req.getRequestURI();

// 2. 获取最后`/`的索引

        int beginIndex = requestURI.lastIndexOf("/");

// 3. 使用substring，获取方法名称

        String methodName = requestURI.substring(beginIndex + 1);

        try {


            Method method = this.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);

// 5. 使用this调用该方法。
            method.invoke(this, req, resp);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            e.printStackTrace();

        }

    }

}

