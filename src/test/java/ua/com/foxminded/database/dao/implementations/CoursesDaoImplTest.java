package ua.com.foxminded.database.dao.implementations;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.DaoFactory;
import ua.com.foxminded.database.dao.interfaces.CoursesDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoursesDaoImplTest {

    private static Course expectedCourse = new Course("Test course", "Description");

    @Test
    @Order(1)
    void TestAdd_ShouldAddCourse_WhenInputCorrectCourse() {
        CoursesDao coursesDao = DaoFactory.getCoursesDao();
        coursesDao.add(expectedCourse);

        Course actual = coursesDao.get(expectedCourse.getId());
        assertEquals(expectedCourse, actual);
    }

    @Test
    @Order(2)
    void TestGet_ShouldGetCourse_WhenInputCorrectId() throws SQLException {
        CoursesDao coursesDao = DaoFactory.getCoursesDao();

        Course actual = coursesDao.get(expectedCourse.getId());
        assertEquals(expectedCourse, actual);

        try(Connection connection = DatabaseUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM courses WHERE id=?;")) {
            statement.setLong(1, expectedCourse.getId());
            statement.executeUpdate();
        }
    }

    @Test
    void TestGetAll_ShouldReturnCourses() {
        List<Course> expected = new ArrayList<>();
        expected.add(new Course(1,"Physics", "Description"));
        expected.add(new Course(2,"Arts", "Description"));

        CoursesDao coursesDao = DaoFactory.getCoursesDao();
        List<Course> actual = coursesDao.getAll();

        assertEquals(expected, actual);
    }

    @Test
    void TestAdd_ShouldAddCourse_WhenInputCurseWithEmptyName() {
        CoursesDao coursesDao = DaoFactory.getCoursesDao();
        Course course = new Course(null, "Description");

        assertThrows(DaoException.class, () -> {
            coursesDao.add(course);
        });
    }

    @Test
    void TestAdd_ShouldAddCourse_WhenInputCurseWithEmptyDescription() {
        CoursesDao coursesDao = DaoFactory.getCoursesDao();
        Course course = new Course("Test course", null);

        assertThrows(DaoException.class, () -> {
            coursesDao.add(course);
        });
    }

    @Test
    void TestGet_ShouldThrowsDaoException_WhenInputNotExistingCourseId() {
        CoursesDao coursesDao = DaoFactory.getCoursesDao();
        int incorrectInd = 200;

        assertThrows(DaoException.class, () -> {
            coursesDao.get(incorrectInd);
        });
    }
}
