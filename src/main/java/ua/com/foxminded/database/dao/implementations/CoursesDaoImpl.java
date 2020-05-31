package ua.com.foxminded.database.dao.implementations;

import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.interfaces.CoursesDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursesDaoImpl implements CoursesDao {

    private static final String COURSE_INSERT_QUERY = "INSERT INTO courses(name, description) VALUES (?,?);";
    private static final String COURSE_GET_QUERY = "SELECT * FROM courses WHERE courses.id = ?;";
    private static final String COURSE_GET_ALL_QUERY = "SELECT * FROM courses;";

    @Override
    public void add(Course course) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(COURSE_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, course.getName());
            statement.setString(2, course.getDescription());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    course.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Put course to database failed", e);
        }
    }

    @Override
    public Course get(long id) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(COURSE_GET_QUERY)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                resultSet.next();
                return new Course(resultSet.getLong("id"), resultSet.getString("name"),
                        resultSet.getString("description"));
            }
        } catch (SQLException e) {
            throw new DaoException("Get course from database failed", e);
        }
    }

    @Override
    public List<Course> getAll() {
        try (Connection connection = DatabaseUtils.getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery(COURSE_GET_ALL_QUERY)) {
                List<Course> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(new Course(resultSet.getLong("id"), resultSet.getString("name"),
                            resultSet.getString("description")));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoException("Get all courses from database failed", e);
        }
    }
}
