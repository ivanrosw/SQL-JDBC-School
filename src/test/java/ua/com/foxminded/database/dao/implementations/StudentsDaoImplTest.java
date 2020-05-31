package ua.com.foxminded.database.dao.implementations;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.postgresql.util.PSQLException;
import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.DaoFactory;
import ua.com.foxminded.database.dao.interfaces.StudentsDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.models.Course;
import ua.com.foxminded.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentsDaoImplTest {

    private static Student expectedStudent = new Student("TestDbName", "TestDbName");

    @Test
    @Order(1)
    void TestAdd_ShouldAddStudentToDB_WhenInputStudent() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        studentsDao.add(expectedStudent);

        Student actual = studentsDao.get(expectedStudent.getId());
        assertEquals(expectedStudent, actual);
    }

    @Test
    @Order(2)
    void TestGet_ShouldGetStudentFromDB_WhenInputId() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        Student actual = studentsDao.get(expectedStudent.getId());

        assertEquals(expectedStudent, actual);
    }

    @Test
    @Order(3)
    void TestUpdate_ShouldUpdateStudentInDB_WhenInputExistingStudentInDb() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        expectedStudent.setFirstName("TestDbNewName");
        studentsDao.update(expectedStudent);
        Student actual = studentsDao.get(expectedStudent.getId());

        assertEquals(expectedStudent, actual);
    }

    @Test
    @Order(4)
    void TestDelete_ShouldThrowsDaoException_WhenGetAfterDeleteStudent() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        studentsDao.delete(expectedStudent.getId());

        assertThrows(DaoException.class, () -> {
            studentsDao.get(expectedStudent.getId());
        });
    }

    @Test
    @Order(5)
    void TestGetAll_ShouldReturnStudentSList() {
        List<Student> expected = new ArrayList<>();
        expected.add(new Student(1, 1, "John", "Anderson"));
        expected.add(new Student(2, 1, "Hendrik", "Hanaway"));
        expected.add(new Student(3, 1, "Nancy", "Drew"));
        expected.add(new Student(4, 2, "Sam", "Caffee"));
        expected.add(new Student(5, 2, "Carrol", "Salva"));
        expected.add(new Student(6, 0, "Salt", "Ney"));

        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        List<Student> actual = studentsDao.getAll();

        assertEquals(expected, actual);
    }

    @Test
    @Order(6)
    void TestGetAllStudentsFromCourse_ShouldReturnStudentsList_WhenInputCorrectCourseName() {
        List<Student> expected = new ArrayList<>();
        expected.add(new Student(3, 1, "Nancy", "Drew"));
        expected.add(new Student(4, 2, "Sam", "Caffee"));

        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        List<Student> actual = studentsDao.getAllStudentsFromCourse("Arts");

        assertEquals(expected, actual);
    }

    @Test
    @Order(7)
    void TestAddToCourse_ShouldAddToCourseStudent_WhenInputCorrectStudentAndCourseId() throws SQLException {
        Student student = new Student(4, 2, "Sam", "Caffee");
        Course course = new Course(1, "Physics", "Description");

        Map<String, Long> expected = new HashMap<>();
        expected.put("student_id", student.getId());
        expected.put("course_id", course.getId());

        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        studentsDao.addToCourse(student, course.getId());

        Map<String, Long> actual = new HashMap<>();

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM students_courses " +
                     "WHERE student_id=? AND course_id=?;")) {

            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                actual.put("student_id", resultSet.getLong("student_id"));
                actual.put("course_id", resultSet.getLong("course_id"));
            }
        }

        assertEquals(expected, actual);
    }

    @Test
    @Order(8)
    void TestDeleteFromCourse_ShouldThrowsPSQLException_WhenGetAfterDeleteStudentFromCourse() throws SQLException {
        Student student = new Student(4, 2, "Sam", "Caffee");
        Course course = new Course(1, "Physics", "Description");

        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        studentsDao.deleteFromCourse(student, course.getId());

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM students_courses " +
                     "WHERE student_id=? AND course_id=?;")) {

            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();

                assertThrows(PSQLException.class, () -> {
                    resultSet.getLong("student_id");
                });
            }
        }
    }

    @Test
    @Order(9)
    void TestGetStudentCourses_ShouldReturnTableStudentCourses_WhenInputCorrectStudent() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        Student student = studentsDao.get(4);

        String expected = "Id   Course Name                                         Course Description" +
                "                                                      \n" +
                "2    Arts                                                Description" +
                "                                                             \n";
        String actual = studentsDao.getStudentCourses(student);

        assertEquals(expected, actual);
    }

    @Test
    void TestAdd_ShouldThrowsDaoException_WhenInputStudentWithFirstName() {
        Student student = new Student(null, "Last Name");
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        assertThrows(DaoException.class, () -> {
            studentsDao.add(student);
        });
    }

    @Test
    void TestAdd_ShouldThrowsDaoException_WhenInputStudentWithNullLastName() {
        Student student = new Student("First Name", null);
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        assertThrows(DaoException.class, () -> {
            studentsDao.add(student);
        });
    }

    @Test
    void TestAdd_ShouldAddStudent_WhenInputStudentHaveEmptyGroup() {
        Student expected = new Student("First Name", "Last Name");
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        studentsDao.add(expected);
        Student actual = studentsDao.get(expected.getId());

        assertEquals(expected, actual);

        studentsDao.delete(expected.getId());
    }

    @Test
    void TestGet_ShouldThrowsDaoException_WhenInputNotExistingId() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        long incorrectId = 200;

        assertThrows(DaoException.class, () -> {
            studentsDao.get(incorrectId);
        });
    }

    @Test
    void TestDelete_ShouldThrowsDaoException_WhenInputNotExistingStudentId() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        long incorrectId = 200;

        assertThrows(DaoException.class, () -> {
            studentsDao.delete(incorrectId);
        });
    }

    @Test
    void TestUpdate_ShouldThrowsDaoException_WhenInputNotExistingStudent() {
        Student student = new Student(200, 1, "First Name", "Last Name");
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        assertThrows(DaoException.class, () -> {
            studentsDao.update(student);
        });
    }

    @Test
    void TestGetAllStudentsFromCourse_ShouldThrowsDaoException_WhenInputIncorrectCourseName() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        assertThrows(DaoException.class, () -> {
            studentsDao.getAllStudentsFromCourse("Junit Test");
        });
    }

    @Test
    void TestAddToCourse_ShouldThrowsDaoException_WhenInputNotExistingStudent() {
        Student student = new Student(200, 1, "First Name", "Last Name");
        long correctCourseId = 1;
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        assertThrows(DaoException.class, () -> {
            studentsDao.addToCourse(student, correctCourseId);
        });
    }

    @Test
    void TestAddToCourse_ShouldThrowsDaoException_WhenInputNotExistingCourse() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        Student student = studentsDao.get(1);
        long incorrectCourseId = 200;

        assertThrows(DaoException.class, () -> {
            studentsDao.addToCourse(student, incorrectCourseId);
        });
    }

    @Test
    void TestDeleteFromCourse_ShouldThrowsDaoException_WhenInputNotExistingCourse() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        Student student = studentsDao.get(1);
        long incorrectCourseId = 200;

        assertThrows(DaoException.class, () -> {
            studentsDao.deleteFromCourse(student, incorrectCourseId);
        });
    }

    @Test
    void TestDeleteFromCourse_ShouldThrowsDaoException_WhenInputNotExistingStudent() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        Student student = new Student(200, 2, "First name", "Last name");
        long incorrectCourseId = 1;

        assertThrows(DaoException.class, () -> {
            studentsDao.deleteFromCourse(student, incorrectCourseId);
        });
    }

    @Test
    void TestGetStudentCourses_ShouldThrowsDaoException_WhenInputNotExistingStudent() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        Student student = new Student(200, 2, "First name", "Last name");

        assertThrows(DaoException.class, () -> {
            studentsDao.getStudentCourses(student);
        });
    }
}
