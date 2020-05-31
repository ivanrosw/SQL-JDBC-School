package ua.com.foxminded.models;

public class Student {

    private long id;
    private long groupId;
    private String firstName;
    private String lastName;

    public Student() {
    }

    public Student(long groupId, String firstName, String lastName) {
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(long id, long groupId, String firstName, String lastName) {
        this.id = id;
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(long id) {this.id = id;}

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        Student student = (Student) obj;

        if (this.id == student.getId() && this.groupId == student.getGroupId() && this.firstName.equals(student.getFirstName())
                && this.lastName.equals(student.getLastName())) {
            return true;
        } else {
            return false;
        }
    }
}
