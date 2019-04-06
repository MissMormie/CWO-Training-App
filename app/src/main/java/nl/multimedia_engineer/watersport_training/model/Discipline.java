package nl.multimedia_engineer.watersport_training.model;

import android.support.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

@Keep
public class Discipline {
    private String id;

    private List<Diploma> diplomas;

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
