package nl.multimedia_engineer.cwo_app.dto;

import nl.multimedia_engineer.cwo_app.model.CursistPartial;

public class CursistPartialDTO {
    protected String id;
    protected String voornaam;
    protected String tussenvoegsel;
    protected String achternaam;
    protected boolean verborgen;
    protected String hoogsteDiploma;
    protected String photoPathThumbnail;

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
