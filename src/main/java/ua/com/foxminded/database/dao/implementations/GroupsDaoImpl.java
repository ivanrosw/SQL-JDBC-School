package ua.com.foxminded.database.dao.implementations;

import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.interfaces.GroupsDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.models.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupsDaoImpl implements GroupsDao {

    private static final String NEXT_STRING_PATTERN = "\n";

    private static final String GROUP_INSERT_QUERY = "INSERT INTO groups(name) VALUES (?);";
    private static final String GROUP_GET_QUERY = "SELECT * FROM groups WHERE groups.id = ?;";
    private static final String GROUP_GET_ALL_QUERY = "SELECT * FROM groups;";
    private static final String GROUP_UPDATE_QUERY = "UPDATE groups SET name = ? WHERE id = ?;";
    private static final String GROUP_GET_BY_STUDENT_AMOUNT_QUERY = "SELECT groups.id, groups.name, COUNT(students.id) " +
            "FROM groups LEFT JOIN students " +
            "ON students.group_id = groups.id " +
            "GROUP BY groups.id " +
            "HAVING COUNT(*) <= ?";

    @Override
    public void add(Group group) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(GROUP_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, group.getName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    group.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Put group to database failed", e);
        }
    }

    @Override
    public Group get(long id) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(GROUP_GET_QUERY)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return new Group(resultSet.getLong("id"), resultSet.getString("name"));
            }

        } catch (SQLException e) {
            throw new DaoException("Get group from database failed", e);
        }
    }

    @Override
    public List<Group> getAll() {
        try (Connection connection = DatabaseUtils.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GROUP_GET_ALL_QUERY)) {

            List<Group> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new Group(resultSet.getLong("id"), resultSet.getString("name")));
            }
            return result;

        } catch (SQLException e) {
            throw new DaoException("Get all groups from database failed", e);
        }
    }

    @Override
    public String getByStudentAmount(int amount) {
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(GROUP_GET_BY_STUDENT_AMOUNT_QUERY)) {

            statement.setInt(1, amount);

            StringBuilder result = new StringBuilder();
            result.append(String.format("%-5s", "Id"));
            result.append(String.format("%-12s", "Group name"));
            result.append(String.format("%-7s", "Count"));
            result.append(NEXT_STRING_PATTERN);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.append(String.format("%-4s", resultSet.getString("id")));
                    result.append(String.format("%-12s", resultSet.getString("name")));
                    result.append(String.format("%-7s", resultSet.getString("count")));
                    result.append(NEXT_STRING_PATTERN);
                }
            }
            return result.toString();

        } catch (SQLException e) {
            throw new DaoException("Get groups by Student amount from database failed", e);
        }
    }
}
