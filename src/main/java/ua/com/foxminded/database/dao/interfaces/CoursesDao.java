package ua.com.foxminded.database.dao.interfaces;

import ua.com.foxminded.models.Course;

import java.util.List;

public interface CoursesDao {

    void add(Course course);

    Course get(long id);

    List<Course> getAll();
}
