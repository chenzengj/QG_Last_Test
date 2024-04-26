package com.example.service.Impl;

import com.example.dao.Impl.UserDaoImpl;
import com.example.dao.UserDao;
import com.example.po.Student;

import com.example.po.Teacher;
import com.example.po.User;
import com.example.service.UserService;

public class UserServiceImpl implements UserService {
    public static UserDao userDao=new UserDaoImpl();
    public static User user=null;
    public static Student student=null;
    public static Teacher teacher=null;
    @Override
    public boolean LoginConfirm(String name, String password){
        user=userDao.findUserByName(name);//登录时则为登录对象
        if(user!=null&&user.getPassword().equals(password))
           return true;
        else
            return false;
    }
    public String getRole(){
        return userDao.Getrole();
    }

    @Override
    public boolean Register(User user) {
        String name= user.getUsername();
        String password= user.getPassword();
//等待加入验证环节
        return userDao.register(user);
    }

    @Override
    public boolean StuInfo(java.lang.String introduction, java.lang.String studentId, java.lang.String grade) {
        return userDao.SetStuInfo(user.getId(),introduction,studentId,grade);
    }

    @Override
    public boolean TeaInfo(java.lang.String introduction, java.lang.String email, java.lang.String qq) {
        return userDao.SetTeaInfo(user.getId(), introduction,email,qq);
    }

    @Override
    public String loadStuInfo() {
        student=userDao.GetStuInfo(user.getId());
        String studentInfoJson = "{\"introduction\":\"" + student.getIntroduction() + "\",\"studentId\":\"" + student.getStudentId() + "\",\"grade\":\"" + student.getGrade() + "\"}";
        return studentInfoJson;
    }
    @Override
    public String loadTeaInfo() {
        teacher=userDao.GetTeaInfo(user.getId());
        String teacherInfoJson = "{\"introduction\":\"" + teacher.getIntroduction() + "\",\"email\":\"" + teacher.getEmail() + "\",\"qq\":\"" + teacher.getQq() + "\"}";
        return teacherInfoJson;
    }
    public int getId(){
        if(userDao.Getrole().equals("student"))
            return student.getId();
        else
            return teacher.getId();
    }
}
