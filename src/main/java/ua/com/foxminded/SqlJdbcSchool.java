package ua.com.foxminded;

import ua.com.foxminded.database.generator.DatabaseGenerator;
import ua.com.foxminded.manager.ApplicationManager;
import ua.com.foxminded.manager.exception.ManagerException;


public class SqlJdbcSchool {

    public static void main(String[] args) {
        DatabaseGenerator dbGenerator = new DatabaseGenerator();
        dbGenerator.deleteTables();
        dbGenerator.createTables();
        dbGenerator.generateData();

        ApplicationManager applicationManager = new ApplicationManager();
        while(true) {
            try {
                applicationManager.getMenu();
            } catch (ManagerException e) {
                System.out.println("Something wrong: ");
                e.printStackTrace();
            }
        }
    }
}
