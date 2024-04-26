package com.example.dao.Impl;

import com.example.dao.UserDao;
import com.example.po.Student;

import com.example.po.Teacher;
import com.example.po.User;
import com.example.utils.JDBCUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {
    public static String role;
    Student student=new Student();

    public UserDaoImpl() {
        JDBCUtils.initConnectionPool();
    }

    @Override
    public User findUserByName(String name){
        User user=null;
        try  {

            // 查询用户表
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM users WHERE username = ?",name);

            if (rs.next()) {
                // 登录成功,返回学生信息
                //Student student = new Student(rs.getInt("id"), rs.getString("name"), rs.getString("password"));
                //student.setRole("student");
                user=new User(rs.getInt("id"),rs.getString("username"), rs.getString("password"));
                user.setRole(rs.getString("user_type"));
                role= rs.getString("user_type");
                return user;
            }
            JDBCUtils.closeResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return user;
    }
    public String Getrole(){
        return role;
    }

    @Override
    public boolean register(User user) {
        try{
           // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO users(username,password,user_type)VALUES(?,?,?)", user.getUsername(), user.getPassword(), user.getRole()); // 执行插入操作

            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }// }else{
//                    System.out.println("nice");
//                    Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "hIM", null);
//                    JsonUtils.writeJsonResponse(response, jsonResponse);
//                }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    @Override
    public boolean SetStuInfo(int user_id, java.lang.String introduction, java.lang.String studentId, java.lang.String grade) {
        Student student=new Student();
        int rowsAffected=0;
        int index=0;
        try{
            JDBCUtils.initConnectionPool();
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM students WHERE user_id = ?",user_id);
            if(rs.next()){

                index=rs.getInt("user_id");
            }
            //JDBCUtils.closeResultSet(rs);
            if(index==0) {
//                student.setGrade(grade);
//                student.setStudentId(studentId);
//                student.setIntroduction(introduction);
                //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
                rowsAffected = JDBCUtils.executeUpdate("Insert INTO students(user_id,student_id,grade,introduction) VALUES (?,?,?,?)", user_id,studentId, grade, introduction); // 执行插入操作
            }else{
//                student.setGrade(grade);
//                student.setStudentId(studentId);
//                student.setIntroduction(introduction);
                //已有信息则执行更新语句
                rowsAffected=JDBCUtils.executeUpdate("update students set student_id=?,grade=?,introduction=? where user_id=?",studentId,grade,introduction,user_id);
            }
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }// }else{
//                    System.out.println("nice");
//                    Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "hIM", null);
//                    JsonUtils.writeJsonResponse(response, jsonResponse);
//                }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    @Override
    public boolean SetTeaInfo(int user_id, java.lang.String introduction, java.lang.String email, java.lang.String qq) {

        int rowsAffected=0;
        int index=0;
        try{
            //JDBCUtils.initConnectionPool();
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM teachers WHERE user_id = ?",user_id);
            if(rs.next()){
                index=rs.getInt("user_id");
            }
            JDBCUtils.closeResultSet(rs);
            if(index==0) {
//                student.setGrade(grade);
//                student.setStudentId(studentId);
//                student.setIntroduction(introduction);
                //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
                rowsAffected = JDBCUtils.executeUpdate("Insert INTO teachers(user_id,email,qq,introduction) VALUES (?,?,?,?)", user_id,email,qq,introduction); // 执行插入操作
            }else{
//                student.setGrade(grade);
//                student.setStudentId(studentId);
//                student.setIntroduction(introduction);
                //已有信息则执行更新语句
                rowsAffected=JDBCUtils.executeUpdate("update teachers set email=?,qq=?,introduction=? where user_id=?",email,qq,introduction,user_id);
            }
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }// }else{
//                    System.out.println("nice");
//                    Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "hIM", null);
//                    JsonUtils.writeJsonResponse(response, jsonResponse);
//                }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    @Override
    public Student GetStuInfo(int id) {
        Student student=new Student();
        try{
            //JDBCUtils.initConnectionPool();
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM students WHERE user_id = ?",id);
            if(rs.next()){
                student.setId(rs.getInt("id"));
                student.setGrade(rs.getString("grade"));
                student.setStudentId(rs.getString("student_id"));
                student.setIntroduction(rs.getString("introduction"));
               // index=rs.getInt("user_id");
            }
            JDBCUtils.closeResultSet(rs);

    }catch (Exception e){
            e.printStackTrace();
        }
        return student;
    }

    @Override
    public Teacher GetTeaInfo(int id) {
        Teacher teacher=new Teacher();
        try{
            //JDBCUtils.initConnectionPool();
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM teachers WHERE user_id = ?",id);
            if(rs.next()){
                teacher.setId(rs.getInt("id"));
                teacher.setEmail(rs.getString("email"));
                teacher.setQq(rs.getString("qq"));
                teacher.setIntroduction(rs.getString("introduction"));
                // index=rs.getInt("user_id");
            }
            JDBCUtils.closeResultSet(rs);

        }catch (Exception e){
            e.printStackTrace();
        }
        return teacher;
    }
}
