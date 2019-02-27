package nl.multimedia_engineer.cwo_app.model;

import java.util.List;

public class Group extends GroupPartial {

    private List<Cursist> cursisten;

    public Group(String discipline, String name) {
        super();
        setDiscipline(discipline);
        setName(name);
    }


    public List<Cursist> getCursisten() {
        return cursisten;
    }

    public void setCursisten(List<Cursist> cursisten) {
        this.cursisten = cursisten;
    }


}
