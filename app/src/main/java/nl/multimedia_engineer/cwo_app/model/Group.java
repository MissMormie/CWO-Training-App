package nl.multimedia_engineer.cwo_app.model;

import java.util.List;

public class Group extends GroupPartial {

    private String discipline;
    private List<Cursist> cursisten;

    public Group(String discipline, String name) {
        super();
        setDiscipline(discipline);
        setName(name);
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


}
