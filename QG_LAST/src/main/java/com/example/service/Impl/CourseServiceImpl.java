package com.example.service.Impl;

import com.example.controller.myServlet;
import com.example.dao.CourseDao;
import com.example.dao.Impl.CourseDaoImpl;
import com.example.po.*;
import com.example.service.CourseService;
import com.example.service.UserService;

import java.lang.String;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseServiceImpl implements CourseService {
    UserService userService= myServlet.userService;
    CourseDao courseDao=new CourseDaoImpl();
    int userId=userService.getId();
    Course course=new Course();
    List<Course> courses=new ArrayList<>();
    @Override
    public boolean creatCourse(String courseName, String description, Date startDate, Date endtDate, int maxStudents) {
        course.setName(courseName);
        course.setDescription(description);
        course.setStartDate(startDate);
        course.setEndDate(endtDate);
        course.setMaxStudents(maxStudents);
        return courseDao.creatCourse(userId,course);


    }

    @Override
    public boolean creatChapter(Chapter chapter) {
        if(courseDao.creatChapter(chapter)){
            course.addChapter(chapter);
            return true;
        }
        else
            return false;
    }

    public List<Course> getAllTeacherCourses() {
        return courseDao.getAllTeacherCourses(userId);
    }

    @Override
    public boolean creatQuestion(Question question) {
        return courseDao.creatQuestion(question);
    }

    @Override
    public boolean selectCourse(int courseId) {

        return courseDao.selectCourse(userId,courseId);
    }

    @Override
    public List<Course> getLearningCourses() {
        return courseDao.getLearningCourses(userId);
    }

    @Override
    public boolean setAnswer(Answer answer) {
        answer.setStudentId(userId);
        return courseDao.setAnswer(answer);
    }

    @Override
    public boolean setLearningProgress(LearningProgress learningProgress) {
        learningProgress.setStudentId(userId);
        return courseDao.setLearningProgress(learningProgress);
    }
}
