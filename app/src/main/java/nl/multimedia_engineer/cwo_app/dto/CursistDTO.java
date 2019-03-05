package nl.multimedia_engineer.cwo_app.dto;

import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;

public class CursistDTO extends CursistPartialDTO {

    protected final Long paspoort;
    protected final String opmerking;
    protected final Map<String, String> behaaldeEisen;
    protected final Map<String, String> diplomas;
    protected String photoPathLarge;
    protected String photoPathNormal;

    public CursistDTO(Cursist cursist) {
        super(cursist);
        if(cursist.getPaspoort()!= null) {
            paspoort = cursist.getPaspoort();
        } else {
            paspoort = 0L;
        }
        opmerking = cursist.getOpmerking();

        behaaldeEisen = new HashMap<>();
        if(cursist.getDiplomaEisSet() != null && !cursist.getDiplomaEisSet().isEmpty()) {
            for(DiplomaEis diplomaEis : cursist.getDiplomaEisSet())
            behaaldeEisen.put(diplomaEis.getId(), diplomaEis.getId());
        }

        diplomas = new HashMap<>();
        if(cursist.getDiplomaSet() != null && !cursist.getDiplomaSet().isEmpty()) {
            for(Diploma diploma : cursist.getDiplomaSet()) {
                diplomas.put(diploma.getId(), diploma.getId());
            }
        }

        photoPathLarge = cursist.getPhotoPathLarge();
        photoPathNormal = cursist.getPhotoPathNormal();
    }

}
