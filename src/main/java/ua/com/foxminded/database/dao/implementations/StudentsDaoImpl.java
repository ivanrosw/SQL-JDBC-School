package ua.com.foxminded.database.dao.implementations;

import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.interfaces.StudentsDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentsDaoImpl implements StudentsDao {

    private static final String NEXT_STRING_PATTERN = "\n";

    private static final String STUDENT_INSERT_QUERY = "INSERT INTO students(group_id, first_name, last_name) VALUES (?,?,?);";
    private static final String STUDENT_GET_QUERY = "SELECT * FROM students WHERE students.id = ?;";
    private static final String STUDENT_GET_ALL_QUERY = "SELECT * FROM students;";
    private static final String STUDENT_DELETE_QUERY = "DELETE FROM students WHERE students.id = ?;";
    private static final String STUDENT_UPDATE_QUERY = "UPDATE students SET group_id = ?, first_name = ?, last_name = ? WHERE id = ?;";

    private static final String STUDENT_GET_ALL_FROM_COURSE_QUERY = "SELECT students.id, students.group_id, students.first_name, " +
            "students.last_name, courses.name FROM students " +
            "INNER JOIN students_courses ON students.id = students_courses.student_id " +
            "INNER JOIN courses ON courses.id = students_courses.course_id " +
            "WHERE courses.name = ?;";
    private static final String STUDENT_ADD_TO_COURSE_QUERY = "INSERT INTO students_courses VALUES(?,?);";
    private static final String STUDENT_DELETE_FROM_COURSE_QUERY = "DELETE FROM students_courses " +
            "WHERE student_id = ? AND course_id = ?;";
    private static final String STUDENT_GET_STUDENT_COURSES_QUERY = "SELECT courses.id, courses.name, courses.description " +
            "FROM courses INNER JOIN students_courses ON courses.id = students_courses.course_id " +
            "WHERE students_courses.student_id = ?;";

    @Override
    public Student get(long id) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_GET_QUERY)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return new Student(resultSet.getLong("id"), resultSet.getLong("group_id"),
                        resultSet.getString("first_name"), resultSet.getString("last_name"));
            }
        } catch (SQLException e) {
            throw new DaoException("Get student from database failed", e);
        }
    }

    @Override
    public void add(Student student) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            if (student.getGroupId() != 0) {
                statement.setLong(1, student.getGroupId());
            } else {
                statement.setNull(1, Types.INTEGER);
            }

            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    student.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Put student to database failed", e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_DELETE_QUERY)) {

            statement.setLong(1, id);
            int deleted = statement.executeUpdate();

            if(deleted == 0) {
                throw new DaoException("Incorrect id");
            }

        } catch (SQLException e) {
            throw new DaoException("Delete student from database failed", e);
        }
    }

    @Override
    public void update(Student student) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_UPDATE_QUERY)) {

            if (student.getGroupId() == 0) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setLong(1, student.getGroupId());
            }

            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setLong(4, student.getId());
            int updated = statement.executeUpdate();

            if(updated == 0) {
                throw new DaoException("Incorrect student");
            }

        } catch (SQLException e) {
            throw new DaoException("Update student in database failed", e);
        }
    }

    @Override
    public List<Student> getAll() {
        try (Connection connection = DatabaseUtils.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(STUDENT_GET_ALL_QUERY)) {

            List<Student> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(new Student(resultSet.getLong("id"), resultSet.getLong("group_id"),
                        resultSet.getString("first_name"), resultSet.getString("last_name")));
            }
            return result;

        } catch (SQLException e) {
            throw new DaoException("Get students from database failed", e);
        }
    }

    @Override
    public List<Student> getAllStudentsFromCourse(String courseName) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_GET_ALL_FROM_COURSE_QUERY)) {

            statement.setString(1, courseName);
            List<Student> result = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {
                int rowCount = 0;

                while (resultSet.next()) {
                    result.add(new Student(resultSet.getLong("id"), resultSet.getLong("group_id"),
                            resultSet.getString("first_name"), resultSet.getString("last_name")));
                    rowCount++;
                }

                if (rowCount == 0) {
                    throw new DaoException("Incorrect course name");
                }

                return result;
            }
        } catch (SQLException e) {
            throw new DaoException("Get all students in course from database failed", e);
        }
    }

    @Override
    public void addToCourse(Student student, long courseId) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_ADD_TO_COURSE_QUERY)) {

            statement.setLong(1, student.getId());
            statement.setLong(2, courseId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Add student to course in database failed", e);
        }
    }

    @Override
    public void deleteFromCourse(Student student, long courseId) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_DELETE_FROM_COURSE_QUERY)) {

            statement.setLong(1, student.getId());
            statement.setLong(2, courseId);
            int deleted = statement.executeUpdate();

            if (deleted == 0) {
                throw new DaoException("Incorrect Student or Course Id");
            }

        } catch (SQLException e) {
            throw new DaoException("Delete student from course in database failed", e);
        }
    }

    @Override
    public String getStudentCourses(Student student) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUDENT_GET_STUDENT_COURSES_QUERY)) {

            statement.setLong(1, student.getId());

            StringBuilder result = new StringBuilder();
            result.append(String.format("%-5s", "Id"));
            result.append(String.format("%-52s", "Course Name"));
            result.append(String.format("%-72s", "Course Description"));
            result.append(NEXT_STRING_PATTERN);

            try (ResultSet resultSet = statement.executeQuery()) {
                int rowCount = 0;
                while (resultSet.next()) {
                    result.append((String.format("%-5s", resultSet.getLong("id"))));
                    result.append((String.format("%-52s", resultSet.getString("name"))));
                    result.append((String.format("%-72s", resultSet.getString("description"))));
                    result.append(NEXT_STRING_PATTERN);
                    rowCount++;
                }

                if (rowCount == 0) {
                    throw new DaoException("Student not have courses or Input incorrect student");
                }
            }
            return result.toString();

        } catch (SQLException e) {
            throw new DaoException("Get student's courses failed", e);
        }
    }
}
