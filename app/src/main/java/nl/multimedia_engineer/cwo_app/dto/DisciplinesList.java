package nl.multimedia_engineer.cwo_app.dto;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.Discipline;

public class DisciplinesList {

    List<Discipline> disciplines;

    public DisciplinesList(DataSnapshot dataSnapshot) {

        disciplines = new ArrayList<>();
        for(DataSnapshot disciplineNode :dataSnapshot.getChildren()) {
            Discipline discipline  = new Discipline();
            discipline.setId(disciplineNode.getKey());
            disciplines.add(discipline);
            for(DataSnapshot diplomas : disciplineNode.getChildren()) {
                Diploma diploma = new Diploma((String) diplomas.getKey(), (String) diplomas.getValue(String.class),0, null);
                discipline.addDiploma(diploma);
            }

        }
    }

    public List<Discipline> getDisciplines() {
        return disciplines;
    }



}
