package nl.multimedia_engineer.cwo_app.dto;

import android.support.annotation.Keep;

import nl.multimedia_engineer.cwo_app.model.CursistPartial;

@Keep
public class CursistPartialDTO {
    public String id;
    public String voornaam;
    public String tussenvoegsel;
    public String achternaam;
    public boolean verborgen;
    public String hoogsteDiploma;
    public String photoPathThumbnail;

    public CursistPartialDTO(CursistPartial cursistPartial) {
        id = cursistPartial.getId();
        voornaam = cursistPartial.getVoornaam();
        tussenvoegsel = cursistPartial.getTussenvoegsel();
        achternaam = cursistPartial.getAchternaam();
        verborgen = cursistPartial.isVerborgen();
        hoogsteDiploma = cursistPartial.getHoogsteDiploma();
        photoPathThumbnail = cursistPartial.getPhotoPathThumbnail();
    }


}
