package ua.com.foxminded.manager;

import ua.com.foxminded.database.dao.DaoFactory;
import ua.com.foxminded.database.dao.interfaces.CoursesDao;
import ua.com.foxminded.database.dao.interfaces.GroupsDao;
import ua.com.foxminded.database.dao.interfaces.StudentsDao;
import ua.com.foxminded.database.exceptions.DaoException;
import ua.com.foxminded.database.exceptions.DatabaseException;
import ua.com.foxminded.manager.exception.ManagerException;
import ua.com.foxminded.models.Course;
import ua.com.foxminded.models.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ApplicationManager {

    private static final String NEXT_STRING_PATTERN ="\n";

    public void getMenu() {

        printMenu();

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("1")) {
                findGroups();
            } else if (userAnswer.equals("2")) {
                findStudents();
            } else if (userAnswer.equals("3")) {
                addStudent();
            } else if (userAnswer.equals("4")) {
                deleteStudent();
            } else if (userAnswer.equals("5")) {
                addToCourse();
            } else if (userAnswer.equals("6")) {
                deleteFromCourse();
            } else if (userAnswer.equals("E")) {
                System.exit(0);
            }
        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void findGroups() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));) {
            System.out.println("Enter count or \"E\" to cancel");
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            GroupsDao groupsDao = DaoFactory.getGroupsDao();
            System.out.println(groupsDao.getByStudentAmount(Integer.parseInt(userAnswer)));

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (NumberFormatException e) {
            System.out.println("Entered wrong count");
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void findStudents() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter course name or \"E\" to cancel");
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            StudentsDao studentsDao = DaoFactory.getStudentsDao();
            List<Student> students = studentsDao.getAllStudentsFromCourse(userAnswer);

            StringBuilder result = new StringBuilder();
            result.append(String.format("%-5s", "Id"));
            result.append(String.format("%-10s", "Group Id"));
            result.append(String.format("%-52s", "First Name"));
            result.append(String.format("%-52s", "Last Name"));
            result.append(NEXT_STRING_PATTERN);

            students.forEach( student -> {
                result.append(String.format("%-5s", student.getId()));
                result.append(String.format("%-10s", student.getGroupId()));
                result.append(String.format("%-52s", student.getFirstName()));
                result.append(String.format("%-52s", student.getLastName()));
                result.append(NEXT_STRING_PATTERN);
            });

            System.out.println(result.toString());

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void addStudent() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("Enter student's First Name to create or \"E\" to cancel");
            String userAnswer = consoleReader.readLine();

            if(userAnswer.equals("E")) {
                return;
            }

            String firstName = userAnswer;

            System.out.println("Enter studen's Last Name to create or \"E\" to cancel");
            userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            String lastName = userAnswer;

            System.out.println("Enter student's Group Id (\"0\" if empty group) or \"E\" to cancel");
            userAnswer = consoleReader.readLine();
            Student student;

            if (userAnswer.equals("E")) {
                return;
            } else if (userAnswer.equals("0")) {
                student = new Student(firstName, lastName);
            } else {
                student = new Student(Long.parseLong(userAnswer),  firstName, lastName);
            }

            StudentsDao studentsDao = DaoFactory.getStudentsDao();
            studentsDao.add(student);
            System.out.println("Adding student complete");

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (NumberFormatException e) {
            System.out.println("Entered wrong Group Id");
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void deleteStudent() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter student's Id to delete from database or \"E\" to cancel");
            String userAnswer = consoleReader.readLine();

            if(userAnswer.equals("E")) {
                return;
            }

            StudentsDao studentsDao = DaoFactory.getStudentsDao();
            studentsDao.delete(Long.parseLong(userAnswer));
            System.out.println("Deleting student complete");

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (NumberFormatException e) {
            System.out.println("Entered wrong Id");
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void addToCourse() {
        try {
            System.out.println("Enter student's Id to add to course or \"E\" to cancel");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            StudentsDao studentsDao = DaoFactory.getStudentsDao();
            Student student = studentsDao.get(Long.parseLong(userAnswer));

            CoursesDao coursesDao = DaoFactory.getCoursesDao();
            List<Course> courses = coursesDao.getAll();
            StringBuilder coursesList  = new StringBuilder();

            coursesList.append(String.format("%-5s", "Id"));
            coursesList.append(String.format("%-52s", "Course Name"));
            coursesList.append(String.format("%-72s", "Course Discription"));
            coursesList.append(NEXT_STRING_PATTERN);

            courses.forEach(course -> {
                coursesList.append(String.format("%-5s", course.getId()));
                coursesList.append(String.format("%-52s", course.getName()));
                coursesList.append(String.format("%-72s", course.getDescription()));
                coursesList.append(NEXT_STRING_PATTERN);
            });

            System.out.println(coursesList.toString());
            System.out.println("Enter Course Id to add student or \"E\" to cancel");
            userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            studentsDao.addToCourse(student, Long.parseLong(userAnswer));
            System.out.println("Adding student to course complete");

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (NumberFormatException e) {
            System.out.println("Entered wrong Student or Course Id");
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void deleteFromCourse() {
        try {
            System.out.println("Enter student's Id to delete from course or \"E\" to cancel");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            StudentsDao studentsDao = DaoFactory.getStudentsDao();
            Student student = studentsDao.get(Long.parseLong(userAnswer));

            System.out.println(studentsDao.getStudentCourses(student));
            System.out.println("Enter Course Id to delete student from the course or \"E\" to cancel");
            userAnswer = consoleReader.readLine();

            if (userAnswer.equals("E")) {
                return;
            }

            studentsDao.deleteFromCourse(student, Long.parseLong(userAnswer));
            System.out.println("Deleting student from  course complete");

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (NumberFormatException e) {
            System.out.println("Entered wrong Student or Course Id");
        } catch (DatabaseException e) {
            throw new ManagerException("Exception in Database methods", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in Query methods", e);
        }
    }

    private void printMenu() {
        System.out.println("Functions:");
        System.out.println("1. Find all groups with less or equals student count");
        System.out.println("2. Find all students related to course with given name");
        System.out.println("3. Add new student");
        System.out.println("4. Delete student by STUDENT_ID");
        System.out.println("5. Add a student to the course (from a list)");
        System.out.println("6. Remove the student from one of his or her courses");
        System.out.println();
        System.out.println("Enter number of function or \"E\" to exit");
    }
}
