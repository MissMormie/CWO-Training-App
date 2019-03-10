package nl.multimedia_engineer.watersport_training.model;

import java.util.ArrayList;
import java.util.List;

public class Discipline {
    String id;

    List<Diploma> diplomas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Diploma> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<Diploma> diplomas) {
        this.diplomas = diplomas;
    }

    public void addDiploma(Diploma diploma) {
        if(diplomas == null ) {
            diplomas = new ArrayList<>();
        }
        diplomas.add(diploma);
    }
}
