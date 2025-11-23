package service;

import java.util.List;
import java.util.Optional;

import dao.AdminDAO;
import dao.CourseDAO;
import dao.UserDAO;
import model.Course;
import model.User;
import utils.HashUtil;

public class AdminService {

    private CourseDAO courseDAO;
    private AdminDAO adminDAO;
    private StudentService studentService;

    public AdminService(CourseDAO courseDAO, AdminDAO AdminDAO) {
        this.courseDAO = courseDAO;
        this.adminDAO = AdminDAO;
    }

    public void approveCourse(String courseID) {
        courseDAO.updateCourseStatus(courseID, "approved");
    }

    public void disapproveCourse(String courseID) {
        courseDAO.updateCourseStatus(courseID ,"disapproved");
        
    }   

    public void removeCourse(String CourseID) {
        courseDAO.deleteCourse(CourseID);
    }

    public List<User> getAllUsers() {
        return adminDAO.loadAll();
    }

    public List<User> getAllStudents() {
        return adminDAO.loadAll().stream()
                .filter(u -> u.getRole().equals("student"))
                .toList();
    }

    public List<User> getAllInstructors() {
        return adminDAO.loadAll().stream()
                .filter(u -> u.getRole().equals("instructor"))
                .toList();
    }

    public List<User> getAllAdmins() {
        return adminDAO.loadAll().stream()
                .filter(u -> u.getRole().equals("admin"))
                .toList();
    }

    public List<Course> getApprovedCourses() {
        return courseDAO.loadAll().stream()
                .filter(c -> c.getApproveStatus().equals("approved"))
                .toList();
    }

    public List<Course> getDisapprovedCourses() {
        return courseDAO.loadAll().stream()
                .filter(c -> c.getApproveStatus().equals("disapproved"))
                .toList();
    }

    public List<Course> getPendingCourses() {
        return courseDAO.loadAll().stream()
                .filter(c -> c.getApproveStatus().equals("pending"))
                .toList();
    }

   


public void removeUser(String userId) {
        adminDAO.deleteUser(userId);
    }

    public void updateUser(String userId, String username, String email, String password, String role) {
        Optional<User> userOpt = adminDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUsername(username);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                // Update password hash
                String hash = HashUtil.sha256(password);
                user.setPassword(hash);
            }
            if (role != null && !role.isEmpty()) {
                user.setRole(role);
            }
            adminDAO.updateUser(user);
        }
    }

    
    //public void viewAnalytics(String STudentID) {
     //   studentService.viewAnalytics(STudentID);
   // }
}
