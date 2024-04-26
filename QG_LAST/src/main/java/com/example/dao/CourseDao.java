package com.example.dao;

import com.example.po.*;

import java.util.List;

public interface CourseDao {
    boolean creatCourse(int userId, Course course);
    boolean creatChapter(Chapter chapter);
    List<Course> getAllTeacherCourses(int userId);
    boolean creatQuestion(Question question);
    List<Course> getAllSelectCourses();
    //选课操作
    boolean selectCourse(int userId,int courseId);
    //选择学习的课程
    Course learnCourse(int courseId);
    //获取可学习课程
    List<Course> getLearningCourses(int userId);
    //获取章节问题
    Chapter getQuestion(int chapterId);
    //将答题情况存入数据库
    boolean setAnswer(Answer answer);
    //将学习情况存入数据库
    boolean setLearningProgress(LearningProgress learningProgress);
    //获取答题情况
    List<Answer> getAnswerSituation();
    //获取学习情况
    List<LearningProgress> getLearningSituation();
    //获取学生总体学习情况
    totalSituation getTotalLearningSituation();
    //获取课程下的学生学习情况
    List<LearningProgress> getStuLearningSituation(int courseId);
    //获取课程学生总体学习情况
    totalSituation getTotalCourseSituation(int courseId);
    //创建帖子
    boolean creatPost(Post post);
    //读取帖子
    List<Post> getPosts();
    //创建评论
    boolean creatComment(int postId,String comment);
}
