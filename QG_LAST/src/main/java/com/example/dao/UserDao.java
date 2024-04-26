package com.example.dao;

import com.example.po.Student;

import com.example.po.Teacher;
import com.example.po.User;

public interface UserDao {
    //登录功能
    User findUserByName(String name);
    String Getrole();
    //注册功能
    boolean register(User user);
    //编辑学生信息
    boolean SetStuInfo(int id, String introduction, String studentId,String grade);
    //编辑教师信息
    boolean SetTeaInfo(int id, String introduction, String email, String qq);
    //获取教师学生个人信息
    Student GetStuInfo(int id);
    Teacher GetTeaInfo(int id);
}
