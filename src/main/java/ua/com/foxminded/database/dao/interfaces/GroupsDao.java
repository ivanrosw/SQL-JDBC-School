package ua.com.foxminded.database.dao.interfaces;

import ua.com.foxminded.models.Group;

import java.util.List;

public interface GroupsDao {

    void add(Group group);

    Group get(long id);

    List<Group> getAll();

    String getByStudentAmount(int count);
}
