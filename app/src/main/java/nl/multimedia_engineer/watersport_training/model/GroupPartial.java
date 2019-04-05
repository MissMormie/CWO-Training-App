package nl.multimedia_engineer.watersport_training.model;

public class GroupPartial {
    private String id;
    private String name;
    private String discipline;


    public GroupPartial() {}

    public GroupPartial(String id, String name, String discipline) {
        this.id = id;
        this.name = name;
        this.discipline = discipline;
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


    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    @Override
    public String toString() {
        return "GroupPartial{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", discipline='" + discipline + '\'' +
                '}';
    }
}
