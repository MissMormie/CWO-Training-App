package nl.multimedia_engineer.cwo_app.model;

import java.util.List;

public class Group {

    private String discipline;
    private List<Cursist> cursisten;
    private String name;

    public Group(String discipline, String name) {
        this.discipline = discipline;
        this.name = name;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public List<Cursist> getCursisten() {
        return cursisten;
    }

    public void setCursisten(List<Cursist> cursisten) {
        this.cursisten = cursisten;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
