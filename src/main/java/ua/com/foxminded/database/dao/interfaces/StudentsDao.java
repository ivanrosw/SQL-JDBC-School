package ua.com.foxminded.database.dao.interfaces;

import ua.com.foxminded.models.Student;

import java.util.List;

public interface StudentsDao {

    void add(Student student);

    void delete(long id);

    Student get(long id);

    void update(Student student);

    List<Student> getAll();

    List<Student> getAllStudentsFromCourse(String courseName);

    void addToCourse(Student student, long courseId);

    void deleteFromCourse(Student student, long courseId);

    String getStudentCourses(Student student);
}
