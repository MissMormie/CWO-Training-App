package nl.multimedia_engineer.cwo_app.dto;

import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.CursistBehaaldEis;
import nl.multimedia_engineer.cwo_app.model.CursistHeeftDiploma;

public class CursistDTO extends CursistPartialDTO {
    protected String fotoFileBase64;

    protected final Long paspoort;
    protected final String opmerking;
    protected List<CursistBehaaldEis> cursistBehaaldEis;

    protected List<CursistHeeftDiploma> cursistHeeftDiplomas;


    public CursistDTO(Cursist cursist) {
        super(cursist);
        if(cursist.getPaspoortDate()!= null) {
            paspoort = cursist.getPaspoortDate().getTime();
        } else {
            paspoort = 0L;
        }
        opmerking = cursist.getOpmerking();

        // todo save eisen & diplomas

    }

}
