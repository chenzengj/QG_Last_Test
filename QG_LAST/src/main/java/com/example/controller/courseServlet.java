package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dao.CourseDao;
import com.example.dao.Impl.CourseDaoImpl;
import com.example.po.*;
import com.example.service.CourseService;
import com.example.service.Impl.CourseServiceImpl;
import com.example.service.UserService;
import com.example.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/course/*")
public class courseServlet extends BaseServlet {
    UserService userService=myServlet.userService;
    CourseService courseService=new CourseServiceImpl();
    CourseDao courseDao=new CourseDaoImpl();
    public void creatCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取请求体
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        //读取json请求数据
        String courseName=jsonObject.getString("name");
        Date startDate=jsonObject.getDate("startDate");
        Date endtDate=jsonObject.getDate("endDate");
        int maxStudents=jsonObject.getInteger("maxStudents");
        String description=jsonObject.getString("description");
        boolean status= courseService.creatCourse(courseName,description,startDate,endtDate,maxStudents);
        if(status){
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "创建课程成功！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "创建课程失败！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    public void creatChapter(HttpServletRequest request, HttpServletResponse response) throws IOException{
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        //读取json请求数据
        String title=jsonObject.getString("title");
        String content=jsonObject.getString("content");
        int courseId=jsonObject.getInteger("courseId");
        Chapter chapter=new Chapter();
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setCourseId(courseId);
        boolean status=courseService.creatChapter(chapter);
        if(status){
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "创建章节成功！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "章节创建失败！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    public void getAllCourses(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");

        List<Course> courses = courseService.getAllTeacherCourses();

        String coursesJson = JSON.toJSONString(courses);

        PrintWriter out = response.getWriter();
//        out.print("var courses = ");
        out.print(coursesJson);
//        out.flush();
    }

    public void setQuestion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Question question=new Question();
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        question.setChapterId(jsonObject.getInteger("chapterId"));
        question.setQuestionType(jsonObject.getString("type"));
        question.setDescription(jsonObject.getString("text"));
        question.setAnswer(jsonObject.getString("answer"));
        question.setScore(jsonObject.getInteger("score"));
        if(question.getQuestionType().equals("choice")){
            JSONArray optionsJsonArray = jsonObject.getJSONArray("options");
            for (Object obj : optionsJsonArray) {
                String option = (String) obj;
                question.options.add(option);
            }
        }
        boolean status=courseService.creatQuestion(question);
        if(status){
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "题目添加成功！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "题目创建失败！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }

    }
    public void getAllSelectCourses(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");

        List<Course> courses = courseDao.getAllSelectCourses();

        String coursesJson = JSON.toJSONString(courses);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(coursesJson);
        //out.flush();
    }


    protected void SelectCourse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        int courseId= jsonObject.getInteger("courseId");
        boolean status=courseService.selectCourse(courseId);
        if(status){
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(true, "选课成功！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }else{
            Map<String, Object> jsonResponse = JsonUtils.createJsonResponse(false, "选课失败！", null);
            JsonUtils.writeJsonResponse(response, jsonResponse);
        }
    }
    protected void LearnCourse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        int courseId= jsonObject.getInteger("courseId");
        Course course=courseDao.learnCourse(courseId);
        String coursesJson = JSON.toJSONString(course);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(coursesJson);
    }
    public void getLearningCourses(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");

        List<Course> courses = courseService.getLearningCourses();

        String coursesJson = JSON.toJSONString(courses);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(coursesJson);
        //out.flush();
    }
    public void getQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        int chapterId= jsonObject.getInteger("chapterId");
        Chapter chapter=courseDao.getQuestion(chapterId);
        String chapterJson = JSON.toJSONString(chapter);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(chapterJson);
    }
    public void answerSituation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        Answer answer=new Answer();

        answer.setAnswerText(jsonObject.getString("answer"));
        answer.setCorrect(jsonObject.getBoolean("isCorrect"));
        answer.setQuestionId(jsonObject.getInteger("questionId"));
        answer.setDescription(jsonObject.getString("description"));
        boolean status=courseService.setAnswer(answer);
    }
    public void learningSituation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        LearningProgress learningProgress=new LearningProgress();
        learningProgress.setChapterId(jsonObject.getInteger("chapterId"));
        learningProgress.setAccuracy(jsonObject.getDouble("accuracy"));
        learningProgress.setQuestionsAttempted(jsonObject.getInteger("complete"));
        courseService.setLearningProgress(learningProgress);
    }
    public void getAnswerSituation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");

        List<Answer> answers = courseDao.getAnswerSituation();

        String answersJson = JSON.toJSONString(answers);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(answersJson);
    }
    public void getLearningSituation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");

        List<LearningProgress> learningProgresses= courseDao.getLearningSituation();

        String learningProgressesJson = JSON.toJSONString(learningProgresses);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(learningProgressesJson);
    }
    public void getTotalLearningSituation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");

        totalSituation totalSituation= courseDao.getTotalLearningSituation();

        String totalSituationJson = JSON.toJSONString(totalSituation);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(totalSituationJson);
    }
    public void StudentsLearningSituation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        int courseId=jsonObject.getInteger("courseId");
        List<LearningProgress> learningProgresses= courseDao.getStuLearningSituation(courseId);

        String learningProgressesJson = JSON.toJSONString(learningProgresses);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(learningProgressesJson);
    }
    public void total(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        int courseId=jsonObject.getInteger("courseId");
        totalSituation totalSituation= courseDao.getTotalCourseSituation(courseId);

        String totalSituationJson = JSON.toJSONString(totalSituation);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(totalSituationJson);
    }
    private static final long serialVersionUID = 1L;

    private List<Post> posts = new ArrayList<>();
    private int postIdCounter = 1;

    protected void getPosts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 返回所有帖子
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        posts=courseDao.getPosts();
        String postsJson = JSON.toJSONString(posts);

        PrintWriter out = response.getWriter();
        //out.print("var courses = ");
        out.println(postsJson);
    }

    protected void creatPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 创建新帖子
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
        //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        String title= jsonObject.getString("title");
        String content= jsonObject.getString("content");
        Post newPost = new Post(postIdCounter++, title, content);
        posts.add(newPost);
        boolean status=courseDao.creatPost(newPost);
        String postsJson = JSON.toJSONString(newPost);

        PrintWriter out = response.getWriter();
        out.println(postsJson);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(JsonConverter.convertToJson(newPost));
    }

    protected void addComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 评论帖子
        response.setContentType("application/javascript");
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String payload = buffer.toString();
    //解析json请求数据
        JSONObject jsonObject = JSON.parseObject(payload);
        int postId = jsonObject.getInteger("postId");
        String comment = jsonObject.getString("comment");
        boolean status=courseDao.creatComment(postId,comment);
//        for (Post post : posts) {
//            if (post.getId() == postId) {
//                post.addComment(comment);
//                break;
//            }
//        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
