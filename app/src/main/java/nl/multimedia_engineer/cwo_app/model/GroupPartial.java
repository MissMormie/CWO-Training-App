package nl.multimedia_engineer.cwo_app.model;

public class GroupPartial {
    private String id;
    private String name;

    GroupPartial() {}

    public GroupPartial(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
