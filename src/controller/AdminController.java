package controller;

import service.AdminService;

import java.util.List;

import model.Course;
import model.User;

public class AdminController {

    private AdminService adminService;
    private CourseController CourseController;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    //approve statues
    public void approveCourse(String CourseID) {

        adminService.approveCourse(CourseID);
    }

    public void disapproveCourse(String CourseID) {

        adminService.disapproveCourse(CourseID);
    }

    public List<Course>  getApprovedCourses(){

        return adminService.getApprovedCourses();
/* 
    //users
    public void viewAnalytics(User s) {
        adminService.viewAnalytics(s);
    }

    public List<Course>  getDisapprovedCourses(){

        return adminService.getDisapprovedCourses();
    }

    public List<Course> getPendingCourses(){

        return adminService.getPendingCourses();
    }

    //users
    public void viewAnalytics(String userID) {

        adminService.viewAnalytics(userID);
    }

    public void removeUsers(String userID) {

        adminService.removeUser(userID);
    }


    //courses
    public void removeCourse(String CourseID) {

        adminService.removeCourse(CourseID);
    }

    public List<User> getAllUsers(){

        return adminService.getAllUsers();
    }

    public List<User> getAllStudents(){
*/

         return adminService.getAllStudents();
    }

    public List<User> getAllInstructors() {

        return adminService.getAllInstructors();
    }

    public List<User> getAllAdmins(){
        
        return adminService.getAllAdmins();
    }
    

   /* * public void viewAnalytics(String STudentID) {
        studentService.viewAnalytics(STudentID);
    } */
}
