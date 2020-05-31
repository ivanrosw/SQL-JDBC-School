package ua.com.foxminded.database.dao.implementations;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.DaoFactory;
import ua.com.foxminded.database.dao.interfaces.GroupsDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.models.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GroupsDaoImplTest {

    private static Group expectedGroup = new Group("Name");

    @Test
    @Order(1)
    void TestAdd_ShouldAddGroup_WhenInputCorrectGroup() {
        GroupsDao groupsDao = DaoFactory.getGroupsDao();
        groupsDao.add(expectedGroup);

        Group actual = groupsDao.get(expectedGroup.getId());
        assertEquals(expectedGroup, actual);
    }

    @Test
    @Order(2)
    void TestGet_ShouldReturnGroup_WhenInputCorretId() throws SQLException {
        GroupsDao groupsDao = DaoFactory.getGroupsDao();

        Group actual = groupsDao.get(expectedGroup.getId());
        assertEquals(expectedGroup, actual);

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM groups WHERE id=?;")) {
            statement.setLong(1, expectedGroup.getId());
            statement.execute();
        }
    }

    @Test
    void TestGetAll_ShouldReturnGroupsList() {
        List<Group> expected = new ArrayList<>();
        expected.add(new Group(1,"NM-11"));
        expected.add(new Group(2,"JK-76"));

        GroupsDao groupsDao = DaoFactory.getGroupsDao();
        List<Group> actual = groupsDao.getAll();

        assertEquals(expected, actual);
    }

    @Test
    void TestGetByStudentAmount_ShouldReturnGroupsTable() {
        String expected = "Id   Group name  Count  \n" +
                "2   JK-76       2      \n" +
                "1   NM-11       3      \n";

        GroupsDao groupsDao = DaoFactory.getGroupsDao();
        String actual = groupsDao.getByStudentAmount(30);

        assertEquals(expected, actual);
    }

    @Test
    void TestAdd_ShouldThrowsDaoException_WhenInputGroupWithEmptyName() {
        Group group = new Group();

        GroupsDao groupsDao = DaoFactory.getGroupsDao();
        assertThrows(DaoException.class, () -> {
            groupsDao.add(group);
        });
    }

    @Test
    void TestGet_ShouldThrowsDaoException_WhenInputNotExistingGroupId() {
        int incorrectId = 200;

        GroupsDao groupsDao = DaoFactory.getGroupsDao();
        assertThrows(DaoException.class, () -> {
            groupsDao.get(incorrectId);
        });
    }
}