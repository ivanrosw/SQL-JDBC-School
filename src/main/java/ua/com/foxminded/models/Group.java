package ua.com.foxminded.models;

public class Group {

    private long id;
    private String name;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        Group group = (Group) obj;
        if (this.id == group.getId() && this.name.equals(group.getName())) {
            return true;
        } else {
            return false;
        }
    }
}
