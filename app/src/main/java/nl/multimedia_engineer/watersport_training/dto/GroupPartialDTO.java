package nl.multimedia_engineer.watersport_training.dto;

import android.support.annotation.Keep;

@Keep
public class GroupPartialDTO {
    private String id;
    private String name;
    private String discipline;

    public GroupPartialDTO() {}

    public GroupPartialDTO(String id, String name, String discipline) {
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
}
