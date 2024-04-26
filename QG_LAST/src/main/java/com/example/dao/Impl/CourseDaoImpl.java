package com.example.dao.Impl;

import com.example.dao.CourseDao;
import com.example.po.*;
import com.example.utils.JDBCUtils;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseDaoImpl implements CourseDao {
    List<Course> courses=new ArrayList<>();
    @Override
    public boolean creatCourse(int userId, Course course) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO courses(teacher_id,name,description,start_date,end_date,max_students)VALUES(?,?,?,?,?,?)",
                    userId,course.getName(),course.getDescription(),course.getStartDate(),course.getEndDate(),course.getMaxStudents()); // 执行插入操作
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM courses WHERE teacher_id = ?",userId);
            //获取课程id
            if (rs.next()) {
                course.setId(rs.getInt("id"));
            }
            JDBCUtils.closeResultSet(rs);
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean creatChapter(Chapter chapter) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO chapters(course_id,title,content)VALUES(?,?,?)",
                    chapter.getCourseId(),chapter.getTitle(),chapter.getContent()); // 执行插入操作
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM chapters WHERE course_id = ?",chapter.getCourseId());
            //获取章节id
            if (rs.next()) {
                chapter.setId(rs.getInt("id"));
            }
            JDBCUtils.closeResultSet(rs);
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Course> getAllTeacherCourses(int userId) {

        try{
            //读取课程
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM courses WHERE teacher_id = ?",userId);
            //获取课程
            while(rs.next()) {
                Course course=new Course();
                course.setTeacherId(rs.getInt("teacher_id"));
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDescription(rs.getString("description"));
                course.setStartDate(rs.getDate("start_date"));
                course.setEndDate(rs.getDate("end_date"));
                course.setMaxStudents(rs.getInt("max_students"));
                //获取课程章节
                ResultSet rst = JDBCUtils.executeQuery("SELECT * FROM chapters WHERE course_id = ?",course.getId());
                while(rst.next()){
                    Chapter chapter=new Chapter();
                    chapter.setCourseId(rst.getInt("course_id"));
                    chapter.setId(rst.getInt("id"));
                    chapter.setTitle(rst.getString("title"));
                    chapter.setContent(rst.getString("content"));
                    //还差一个问题的内容
                    //添加章节到课程中
                    course.addChapter(chapter);
                }
                JDBCUtils.closeResultSet(rst);
                //还差一个报名学生内容
                //报名学生信息
                ResultSet resultSet= JDBCUtils.executeQuery("SELECT * FROM enrollments WHERE course_id = ?",course.getId());
                while(resultSet.next()){
                    Student student=new Student();
                    int id=resultSet.getInt("student_id");
                    ResultSet re=JDBCUtils.executeQuery("SELECT * FROM students WHERE id = ?",id);
                    //获取学生信息
                    while (re.next()){
                        student.setId(id);
                        student.setStudentId(re.getString("student_id"));
                        student.setGrade(re.getString("grade"));
                        int UID=re.getInt("user_id");
                        //查找信息
                        ResultSet r=JDBCUtils.executeQuery("SELECT * FROM users WHERE id = ?",UID);
                        if(r.next()){
                            student.setUsername(r.getString("username"));
                        }
                        //关闭连接
                        JDBCUtils.closeResultSet(r);
                        //将学生信息存入选课名单中
                        course.enrollStudent(student);
                    }
                    JDBCUtils.closeResultSet(re);
                }
                JDBCUtils.closeResultSet(resultSet);
                //添加课程到集合中
                courses.add(course);
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //将课程集合返回
        return courses;
    }

    @Override
    public boolean creatQuestion(Question question) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO questions(chapter_id,question_type,description,score,answer)VALUES(?,?,?,?,?)",
                    question.getChapterId(),question.getQuestionType(),question.getDescription(),question.getScore(),question.getAnswer()); // 执行插入操作
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM questions WHERE chapter_id = ?",question.getChapterId());

            //获取章节id
           while (rs.next()) {
                question.setId(rs.getInt("id"));
            }
            JDBCUtils.closeResultSet(rs);
            if(question.getQuestionType().equals("choice")){
                String option;
                for(int i=0;i<question.options.size();i++){
                    option=question.options.get(i);
                    JDBCUtils.executeUpdate("Insert INTO choice_questions(question_id,options)VALUES(?,?)",question.getId(),option);
                }
            }
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public List<Course> getAllSelectCourses() {
        try{
            //读取课程
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM courses WHERE start_date <= CURDATE() AND end_date >= CURDATE()");
            //获取课程
            while(rs.next()) {
                Course course=new Course();
                course.setTeacherId(rs.getInt("teacher_id"));
                //获取教师名字
                ResultSet re = JDBCUtils.executeQuery("SELECT * FROM teachers WHERE id=?",course.getTeacherId());
                if(re.next()){

                    int id= re.getInt("user_id");
                    ResultSet r = JDBCUtils.executeQuery("SELECT * FROM users WHERE id=?",id);
                    if(r.next()){
                        course.setTeacher(r.getString("username"));
                    }
                    JDBCUtils.closeResultSet(r);
                }
                JDBCUtils.closeResultSet(re);
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDescription(rs.getString("description"));
                course.setStartDate(rs.getDate("start_date"));
                course.setEndDate(rs.getDate("end_date"));
                course.setMaxStudents(rs.getInt("max_students"));
                //获取课程章节
                ResultSet rst = JDBCUtils.executeQuery("SELECT * FROM chapters WHERE course_id = ?",course.getId());
                while(rst.next()){
                    Chapter chapter=new Chapter();
                    chapter.setCourseId(rst.getInt("course_id"));
                    chapter.setId(rst.getInt("id"));
                    chapter.setTitle(rst.getString("title"));
                    chapter.setContent(rst.getString("content"));
                    //还差一个问题的内容
                    ResultSet ques=JDBCUtils.executeQuery("SELECT * FROM questions WHERE chapter_id = ?",chapter.getId());
                    while(ques.next()){
                        Question question=new Question();
                        question.setAnswer(ques.getString("answer"));
                        question.setScore(ques.getInt("score"));
                        question.setId(ques.getInt("id"));
                        question.setDescription(ques.getString("description"));
                        question.setQuestionType(ques.getString("question_type"));
                        question.setChapterId(ques.getInt("chapter_id"));
                        //判断是否为选择题，如果为选择题，需要读取选项内容
                    if(question.getQuestionType().equals("choice")){
                        ResultSet choice=JDBCUtils.executeQuery("SELECT * FROM choice_questions WHERE question_id = ?",question.getId());
                       //读取选项内容
                        while(choice.next()){
                            String option=choice.getString("options");
                            question.addOptions(option);
                        }
                        JDBCUtils.closeResultSet(choice);
                    }
                    chapter.addQuestion(question);
                    }
                    JDBCUtils.closeResultSet(ques);
                    //添加章节到课程中
                    course.addChapter(chapter);
                }
                JDBCUtils.closeResultSet(rst);
                //还差一个报名学生内容
                //添加课程到集合中
                courses.add(course);
                int size=0;
                //判断选课人数是否已满
                ResultSet resultSet=JDBCUtils.executeQuery("SELECT * FROM enrollments WHERE course_id = ?",course.getId());
                while(resultSet.next()){
                    size++;
                }
                if(size>= course.getMaxStudents()){
                    courses.remove(course);
                }
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //将课程集合返回
        return courses;
    }

    @Override
    public boolean selectCourse(int userId, int courseId) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO enrollments(student_id,course_id,enroll_date)VALUES(?,?,?)",
                    userId,courseId, LocalDate.now()); // 执行插入操作
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Course learnCourse(int courseId) {
        Course course=new Course();
        try{
            //读取课程
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM courses WHERE id=?",courseId);
            //获取课程
            while(rs.next()) {
                course.setTeacherId(rs.getInt("teacher_id"));
                //获取教师名字
                ResultSet re = JDBCUtils.executeQuery("SELECT * FROM teachers WHERE id=?",course.getTeacherId());
                if(re.next()){
                    int id= re.getInt("user_id");
                    ResultSet r = JDBCUtils.executeQuery("SELECT * FROM users WHERE id=?",id);
                    if(r.next()){
                        course.setTeacher(r.getString("username"));
                    }
                    JDBCUtils.closeResultSet(r);
                }
                JDBCUtils.closeResultSet(re);
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDescription(rs.getString("description"));
                course.setStartDate(rs.getDate("start_date"));
                course.setEndDate(rs.getDate("end_date"));
                course.setMaxStudents(rs.getInt("max_students"));
                //获取课程章节
                ResultSet rst = JDBCUtils.executeQuery("SELECT * FROM chapters WHERE course_id = ?",course.getId());
                while(rst.next()){
                    Chapter chapter=new Chapter();
                    chapter.setCourseId(rst.getInt("course_id"));
                    chapter.setId(rst.getInt("id"));
                    chapter.setTitle(rst.getString("title"));
                    chapter.setContent(rst.getString("content"));
                    //还差一个问题的内容
                    ResultSet ques=JDBCUtils.executeQuery("SELECT * FROM questions WHERE chapter_id = ?",chapter.getId());
                    while(ques.next()){
                        Question question=new Question();
                        question.setAnswer(ques.getString("answer"));
                        question.setScore(ques.getInt("score"));
                        question.setId(ques.getInt("id"));
                        question.setDescription(ques.getString("description"));
                        question.setQuestionType(ques.getString("question_type"));
                        question.setChapterId(ques.getInt("chapter_id"));
                        //判断是否为选择题，如果为选择题，需要读取选项内容
                        if(question.getQuestionType().equals("choice")){
                            ResultSet choice=JDBCUtils.executeQuery("SELECT * FROM choice_questions WHERE question_id = ?",question.getId());
                            //读取选项内容
                            while(choice.next()){
                                String option=choice.getString("options");
                                question.addOptions(option);
                            }
                            JDBCUtils.closeResultSet(choice);
                        }
                        chapter.addQuestion(question);
                    }
                    JDBCUtils.closeResultSet(ques);
                    //添加章节到课程中
                    course.addChapter(chapter);
                }
                JDBCUtils.closeResultSet(rst);
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //将课程返回
        return course;
    }

   @Override
    public List<Course> getLearningCourses(int userId) {
        List<Course> courses1=new ArrayList<>();
        try{
          ResultSet rs=JDBCUtils.executeQuery("SELECT * FROM enrollments WHERE student_id = ?",userId);
          while(rs.next()){
              Course course=new Course();
              int courseId=rs.getInt("course_id");
              course=learnCourse(courseId);
              // 获取当前日期
              Date currentDate = new Date();
              if (course.getEndDate().compareTo(currentDate) >= 0) {
                  // 课程还未结束，执行相关操作
                  courses1.add(course);
              }

          }
          JDBCUtils.closeResultSet(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses1;
    }

    @Override
    public Chapter getQuestion(int chapterId) {
        Chapter chapter=new Chapter();
        try{
            ResultSet rst = JDBCUtils.executeQuery("SELECT * FROM chapters WHERE id = ?",chapterId);
            while(rst.next()){
                chapter.setCourseId(rst.getInt("course_id"));
                chapter.setId(rst.getInt("id"));
                chapter.setTitle(rst.getString("title"));
                chapter.setContent(rst.getString("content"));
                //还差一个问题的内容
                ResultSet ques=JDBCUtils.executeQuery("SELECT * FROM questions WHERE chapter_id = ?",chapter.getId());
                while(ques.next()){
                    Question question=new Question();
                    question.setAnswer(ques.getString("answer"));
                    question.setScore(ques.getInt("score"));
                    question.setId(ques.getInt("id"));
                    question.setDescription(ques.getString("description"));
                    question.setQuestionType(ques.getString("question_type"));
                    question.setChapterId(ques.getInt("chapter_id"));
                    //判断是否为选择题，如果为选择题，需要读取选项内容
                    if(question.getQuestionType().equals("choice")){
                        ResultSet choice=JDBCUtils.executeQuery("SELECT * FROM choice_questions WHERE question_id = ?",question.getId());
                        //读取选项内容
                        while(choice.next()){
                            String option=choice.getString("options");
                            question.addOptions(option);
                        }
                        JDBCUtils.closeResultSet(choice);
                    }
                    chapter.addQuestion(question);
                }
                JDBCUtils.closeResultSet(ques);
        }
            JDBCUtils.closeResultSet(rst);
    }catch (Exception e){
            e.printStackTrace();
        }
        return chapter;
}

    @Override
    public boolean setAnswer(Answer answer) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO answer_situation(question_id,question_description,isCorrect,answer,student_id)VALUES(?,?,?,?,?)",
                    answer.getQuestionId(),answer.getDescription(), answer.isCorrect(),answer.getAnswerText(),answer.getStudentId()); // 执行插入操作
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public boolean setLearningProgress(LearningProgress learningProgress) {
        try{
            ResultSet resultSet=JDBCUtils.executeQuery("SELECT * FROM chapters WHERE id = ?",learningProgress.getChapterId());
            if(resultSet.next()){
                learningProgress.setCourseId(resultSet.getInt("course_id"));
            }
            JDBCUtils.closeResultSet(resultSet);
            int rowsAffected;
            //判断是否有记录
            ResultSet re=JDBCUtils.executeQuery("SELECT * FROM learning_progress WHERE chapter_id = ?",learningProgress.getChapterId());
            if(re.next()){
                rowsAffected = JDBCUtils.executeUpdate("update learning_progress set questions_attempted=?,accuracy=? where chapter_id=?",
                        learningProgress.getQuestionsAttempted(),learningProgress.getAccuracy(),learningProgress.getChapterId()); // 执行更新操作
            }else {
                rowsAffected = JDBCUtils.executeUpdate("Insert INTO learning_progress(student_id,chapter_id,course_id,questions_attempted,accuracy)VALUES(?,?,?,?,?)",
                        learningProgress.getStudentId(), learningProgress.getChapterId(), learningProgress.getCourseId(), learningProgress.getQuestionsAttempted(), learningProgress.getAccuracy()); // 执行插入操作
            }
            JDBCUtils.closeResultSet(re);
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public List<Answer> getAnswerSituation() {
        List<Answer> answers=new ArrayList<>();
        try{

            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM answer_situation");

            //获取章节id
            while (rs.next()) {
                Answer answer=new Answer();
                answer.setAnswerText(rs.getString("answer"));
                answer.setDescription(rs.getString("question_description"));
                answer.setCorrect(rs.getBoolean("isCorrect"));
                answers.add(answer);
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return answers;
    }

    @Override
    public List<LearningProgress> getLearningSituation() {
        List<LearningProgress> learningProgresses=new ArrayList<>();
        try{

            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM learning_progress");
            int index=0;
            //获取章节id
            while (rs.next()) {
                LearningProgress learningProgress=new LearningProgress();
                learningProgress.setQuestionsAttempted(rs.getInt("questions_attempted"));
                learningProgress.setAccuracy(rs.getDouble("accuracy"));
                int id=rs.getInt("chapter_id");
                ResultSet rst = JDBCUtils.executeQuery("SELECT * FROM chapters where id=?",id);
                if(rst.next()){
                    learningProgress.setChapterName(rst.getString("title"));
                }
                learningProgresses.add(learningProgress);
                JDBCUtils.closeResultSet(rst);
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return learningProgresses;
    }
    public  totalSituation getTotalLearningSituation() {
        totalSituation totalSituation=new totalSituation();
        try{
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM learning_progress");
            int chapters=0,questions=0;

            double rate=0,correct=0;
            //获取章节id
            while (rs.next()) {
                chapters++;
                questions+=rs.getInt("questions_attempted");
                correct+=(rs.getInt("questions_attempted"))*(rs.getDouble("accuracy"));
            }
            rate=correct/questions;

            JDBCUtils.closeResultSet(rs);
            totalSituation.setTotalQuestions(questions);
            totalSituation.setTotalChapters(chapters);
            totalSituation.setCorrectRate(rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSituation;
    }
    @Override
    public List<LearningProgress> getStuLearningSituation(int courseId) {
        List<LearningProgress> learningProgresses=new ArrayList<>();
        try{
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM learning_progress where course_id=?",courseId);

            while (rs.next()) {
                LearningProgress learningProgress=new LearningProgress();
                learningProgress.setQuestionsAttempted(rs.getInt("questions_attempted"));
                learningProgress.setAccuracy(rs.getDouble("accuracy"));
                int id=rs.getInt("student_id");
                ResultSet rst = JDBCUtils.executeQuery("SELECT * FROM students where id=?",id);
                if(rst.next()){
                    int userId=rst.getInt("user_id");
                    ResultSet resultSet=JDBCUtils.executeQuery("SELECT * FROM users where id=?",userId);
                    if(resultSet.next()){
                        learningProgress.setStuName(resultSet.getString("username"));
                    }
                    JDBCUtils.closeResultSet(resultSet);
                    }
                learningProgresses.add(learningProgress);
                JDBCUtils.closeResultSet(rst);
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return learningProgresses;
    }

    @Override
    public totalSituation getTotalCourseSituation(int courseId) {
        totalSituation totalSituation=new totalSituation();
        try{
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM learning_progress where course_id=?",courseId);
            int chapters=0,questions=0,stuNum=0;

            double rate=0,correct=0;
            //获取章节id
            while (rs.next()) {
                chapters++;
                questions+=rs.getInt("questions_attempted");
                correct+=(rs.getInt("questions_attempted"))*(rs.getDouble("accuracy"));
            }
            rate=correct/questions;

            JDBCUtils.closeResultSet(rs);
            ResultSet resultSet=JDBCUtils.executeQuery("select * from enrollments where course_id=?",courseId);
            while(resultSet.next()){
                stuNum++;
            }
            JDBCUtils.closeResultSet(resultSet);
            totalSituation.setTotalQuestions(questions);
            totalSituation.setTotalChapters(chapters);
            totalSituation.setCorrectRate(rate);
            totalSituation.setTotalStudents(stuNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSituation;
    }

    @Override
    public boolean creatPost(Post post) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO posts(id,title,content)VALUES(?,?,?)",
                    post.getId(),post.getTitle(),post.getContent()); // 执行插入操作
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public List<Post> getPosts() {
        List<Post> posts=new ArrayList<>();
        try{
            ResultSet rs = JDBCUtils.executeQuery("SELECT * FROM posts");
            while (rs.next()){
                Post post=new Post();
                post.setContent(rs.getString("content"));
                post.setTitle(rs.getString("title"));
                int id= rs.getInt("id");
                ResultSet rst=JDBCUtils.executeQuery("select * from comments where post_id=?",id);
                while (rst.next()){
                    post.addComment(rst.getString("content"));
                }
                JDBCUtils.closeResultSet(rst);
                posts.add(post);
            }
            JDBCUtils.closeResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public boolean creatComment(int postId, String comment) {
        try{
            // JDBCUtils.initConnectionPool();
            //String sql = "INSERT INTO users (username, password,user_type) VALUES (?, ?,?)";
            int rowsAffected = JDBCUtils.executeUpdate("Insert INTO comments(post_id,content)VALUES(?,?)",
                    postId,comment); // 执行插入操作
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }
}
