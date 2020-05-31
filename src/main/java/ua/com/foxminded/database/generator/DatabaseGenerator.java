package ua.com.foxminded.database.generator;

import ua.com.foxminded.database.DatabaseUtils;
import ua.com.foxminded.database.dao.DaoFactory;
import ua.com.foxminded.database.dao.interfaces.CoursesDao;
import ua.com.foxminded.database.dao.interfaces.GroupsDao;
import ua.com.foxminded.database.dao.interfaces.StudentsDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.database.exceptions.DatabaseException;
import ua.com.foxminded.models.Course;
import ua.com.foxminded.models.Group;
import ua.com.foxminded.models.Student;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseGenerator {

    private static final String NEXT_STRING_PATTERN = "\n";

    private static final String DELETE_TABLES_FILE_PATH = "sqlscripts/DeleteTables.sql";
    private static final String CREATE_TABLES_FILE_PATH = "sqlscripts/CreateTables.sql";

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final List<String> COURSES;
    private static final String[] FIRST_NAMES;
    private static final String[] LAST_NAMES;

    private static final int MAX_GROUP_SIZE = 20;
    private static final int MIN_GROUP_SIZE = 10;
    private static final int MAX_COURSE_COUNT = 2;
    private static final int MIN_COURSE_COUNT = 1;
    private static final int MAX_GROUP_NUMBER = 88;
    private static final int MIN_GROUP_NUMBER = 11;

    private static Logger log = Logger.getLogger(DatabaseGenerator.class.getName());

    private Random random = new Random();

    static {
        COURSES = Arrays.asList("English", "Algebra", "Statistics",
                "Biology", "Physics", "Chemistry", "World History", "Geography", "Arts", "Physical education");

        FIRST_NAMES = new String[]{"Alan", "Abby", "Arthur", "Alisa", "Cab", "Carrol", "Cai", "Calynne", "Hadi",
                "Hallie", "Hendrik", "Holli", "Nick", "Nancy", "Naf", "Narci", "Sam", "Sally", "Salt", "Saima"};

        LAST_NAMES = new String[]{"Albertsen", "Alderik", "Agins", "Alloy", "Caccia", "Caffee", "Caperton", "Cardi",
                "Haberman", "Hammes", "Hanaway", "Harelson", "Nava", "Neel", "Neri", "Ney", "Sullivan", "Saltie",
                "Salva", "Summy"};
    }

    public void deleteTables() {
        executeScript(DELETE_TABLES_FILE_PATH);
    }

    public void createTables() {
        executeScript(CREATE_TABLES_FILE_PATH);
    }

    public void generateData() {
        System.out.println("Generating Data...");

        generateGroups();
        generateCourses();
        generateStudents();
        classifyStudents();

        System.out.println("Data generated");
    }

    private void generateGroups() {
        GroupsDao groupsDao = DaoFactory.getGroupsDao();

        for (int i = 0; i < 10; i++) {
            String groupName = generateGroupName();
            groupsDao.add(new Group(groupName));
        }
    }

    private void generateCourses() {
        CoursesDao coursesDao = DaoFactory.getCoursesDao();
        COURSES.forEach(course -> {
            coursesDao.add(new Course(course, "Course description"));
        });
    }

    private void generateStudents() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();

        for (int i = 0; i < 200; i++) {
            studentsDao.add(generateStudent());
        }
    }

    private void classifyStudents() {
        StudentsDao studentsDao = DaoFactory.getStudentsDao();
        GroupsDao groupsDao = DaoFactory.getGroupsDao();
        CoursesDao coursesDao = DaoFactory.getCoursesDao();

        List<Student> students = studentsDao.getAll();
        List<Group> groups = groupsDao.getAll();
        List<Course> courses = coursesDao.getAll();

        int groupIndex = 0;
        int groupSize = generateGroupSize();

        for (Student student : students) {
            if (groupIndex < groups.size() && groupSize > 0) {
                student.setGroupId(groups.get(groupIndex).getId());
                groupSize--;
                studentsDao.update(student);
            }

            if (groupIndex < groups.size() && groupSize == 0) {
                groupIndex++;
                groupSize = generateGroupSize();
            }

            int coursesCount = generateCourseCount();
            while (coursesCount > 0) {
                try {
                    int courseIndex = random.nextInt(courses.size() - 1);
                    studentsDao.addToCourse(student, courses.get(courseIndex).getId());
                    coursesCount--;
                } catch (DaoException e) {
                    log.log(Level.SEVERE, "Random try add student to already adding course", e);
                }
            }
        }
    }

    private void executeScript(String scriptPath) {
        try (Connection connection = DatabaseUtils.getConnection();
             Statement statement = connection.createStatement()) {

            String scriptText = readScript(scriptPath);
            statement.execute(scriptText);

        } catch (SQLException e) {
            throw new DatabaseException("Something wrong with sql script file", e);
        }
    }

    private String readScript(String scriptPath) {

        File scriptFile = new File(getClass().getClassLoader().getResource(scriptPath).getFile());
        try (BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile))) {
            StringBuilder result = new StringBuilder();

            fileReader.lines().forEach(line -> {
                result.append(line);
                result.append(NEXT_STRING_PATTERN);
            });

            return result.toString();

        } catch (IOException e) {
            throw new DatabaseException("Internal error", e);
        }
    }

    private String generateGroupName() {
        StringBuilder groupName = new StringBuilder();

        for (int i = 0; i < 2; i++) {
            int letterIndex = random.nextInt(LETTERS.length()-1);
            groupName.append(LETTERS.charAt(letterIndex));
        }

        groupName.append("-");
        groupName.append((random.nextInt(MAX_GROUP_NUMBER) + MIN_GROUP_NUMBER));
        return groupName.toString();
    }

    private Student generateStudent() {

        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length - 1)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length - 1)];

        return new Student(firstName, lastName);
    }

    private int generateGroupSize() {
        return random.nextInt(MAX_GROUP_SIZE) + MIN_GROUP_SIZE;
    }

    private int generateCourseCount() {
        return random.nextInt(MAX_COURSE_COUNT) + MIN_COURSE_COUNT;
    }
}
