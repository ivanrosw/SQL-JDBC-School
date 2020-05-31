package ua.com.foxminded.database.dao;

import ua.com.foxminded.database.dao.implementations.CoursesDaoImpl;
import ua.com.foxminded.database.dao.implementations.GroupsDaoImpl;
import ua.com.foxminded.database.dao.implementations.StudentsDaoImpl;
import ua.com.foxminded.database.dao.interfaces.CoursesDao;
import ua.com.foxminded.database.dao.interfaces.GroupsDao;
import ua.com.foxminded.database.dao.interfaces.StudentsDao;

public class DaoFactory {

    private DaoFactory() {
    }

    public static StudentsDao getStudentsDao() {
        return new StudentsDaoImpl();
    }

    public static GroupsDao getGroupsDao() {
        return new GroupsDaoImpl();
    }

    public static CoursesDao getCoursesDao() {
        return new CoursesDaoImpl();
    }

}
