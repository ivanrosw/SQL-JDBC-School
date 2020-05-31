package ua.com.foxminded.models;

public class Course {

    private long id;
    private String name;
    private String description;

    public Course() {
    }

    public Course(String name, String discription) {
        this.name = name;
        this.description = discription;
    }

    public Course(long id, String name, String discription) {
        this.id = id;
        this.name = name;
        this.description = discription;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        Course course = (Course) obj;
        if (this.id == course.getId() && this.name.equals(course.getName())
                && this.description.equals(course.getDescription())) {
            return true;
        } else {
            return false;
        }
    }
}
