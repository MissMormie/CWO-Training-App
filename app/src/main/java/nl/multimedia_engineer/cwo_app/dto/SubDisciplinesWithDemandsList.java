package nl.multimedia_engineer.cwo_app.dto;

import android.support.annotation.Keep;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;

@Keep
public class SubDisciplinesWithDemandsList {
    List<Diploma> diplomaList;

    public SubDisciplinesWithDemandsList(HashMap<String, HashMap<String, DiplomaEis>> data) {
        diplomaList = new ArrayList<>();
        for(Map.Entry<String, HashMap<String, DiplomaEis>> entry : data.entrySet()) {
            List<DiplomaEis> diplomaEisen = new ArrayList<>();
            for(Map.Entry<String, DiplomaEis> eis : entry.getValue().entrySet()) {
                eis.getValue().setId(eis.getKey());
                diplomaEisen.add(eis.getValue());
            }
            Diploma diploma = new Diploma(entry.getKey(), entry.getKey(), 0, diplomaEisen);
            diplomaList.add(diploma);
        }
    }

    public List<Diploma> getDiplomaList() {
        return diplomaList;
    }
}
