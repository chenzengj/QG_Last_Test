package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dao.Impl.UserDaoImpl;
import com.example.dao.UserDao;
import com.example.service.Impl.UserServiceImpl;
import com.example.po.User;
import com.example.service.UserService;
import com.example.utils.JDBCUtils;
import com.example.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/myServlet/*")
public class myServlet extends BaseServlet{
    public static UserService userService=new UserServiceImpl();
//注册
    public void register(HttpServletRequest request, HttpServletResponse response) throws  IOException {

        BufferedReader reader = request.getReader();
        String line;
        StringBuilder requestBody = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        String payload=requestBody.toString();


            JSONObject jsonObject = JSON.parseObject(payload);
            // if(jsonObject!=null){
//                    System.out.println("hhh");
//                    System.out.println(jsonObject.getString("name"));
            String name=jsonObject.getString("name");
            String password=jsonObject.getString("password");
            String role=jsonObject.getString("role");
            User user=new User(name,password,role);
            boolean status=userService.Register(user);
        if(status){
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "注册成功", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "该用户名已被注册", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    //登录
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();

        JSONObject jsonObject = JSON.parseObject(payload);
        String name=jsonObject.getString("name");
        String password=jsonObject.getString("password");
        boolean status=userService.LoginConfirm(name,password);
        String role=userService.getRole();

        if(status){
            if(role.equals("student")){
                Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "登录成功", "student");
                JsonUtils.writeJsonResponse(response, jsonResponse);
            }else if(role.equals("teacher")){
                Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "登录成功", "teacher");
                JsonUtils.writeJsonResponse(response, jsonResponse);
            }
        }else {
            // 登录失败
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "用户名或密码错误", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    //编辑学生个人信息
    public void student_introduction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求体
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        String payload = requestBody.toString();
        reader.close();

        // 解析请求体 JSON
        JSONObject jsonObject = JSON.parseObject(payload);
        String introduction = jsonObject.getString("introduction");
        String grade = jsonObject.getString("grade");
        //String email= jsonObject.getString("email");
        String studentId = jsonObject.getString("studentId");
        boolean status = userService.StuInfo(introduction, studentId, grade);

        // 发送响应
        if (status) {
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "学生信息保存成功！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "学生信息保存失败！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    //编辑教师个人信息
    public void teacher_introduction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求体
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        String payload = requestBody.toString();
        reader.close();

        // 解析请求体 JSON
        JSONObject jsonObject = JSON.parseObject(payload);
        String introduction = jsonObject.getString("introduction");
        String email= jsonObject.getString("email");
        String qq = jsonObject.getString("qq");
        boolean status = userService.TeaInfo(introduction, email, qq);

        // 发送响应
        if (status) {
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "教师信息保存成功！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "教师信息保存失败！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    //读取学生个人信息
    public void getStuInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String stu=userService.loadStuInfo();
        // 设置响应内容类型为 JSON
        response.setContentType("application/json;charset=UTF-8");
        // 将学生信息的 JSON 字符串写入响应中
        response.getWriter().write(stu);
    }
    //读取教师个人信息
    public void getTeaInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tea=userService.loadTeaInfo();
        // 设置响应内容类型为 JSON
        response.setContentType("application/json;charset=UTF-8");
        // 将学生信息的 JSON 字符串写入响应中
        response.getWriter().write(tea);
    }
}
