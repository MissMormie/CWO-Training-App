package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class CursistPartial implements Parcelable {
    String id;
    String voornaam;
    String tussenvoegsel;
    String achternaam;
    boolean verborgen = false;
    String hoogsteDiploma;
    String thumbnailPhotoPath;

    private transient File thumbnail;


    public CursistPartial() {

    }

    public void setThumbnailPhotoFile(File thumbnail) {
        this.thumbnail = thumbnail;
    }

    public File getThumbnailPhotoFile() {
        return thumbnail;
    }

    public String getThumbnailPhotoPath() {
        return thumbnailPhotoPath;
    }

    public void setThumbnailPhotoPath(String thumbnailPhotoPath) {
        this.thumbnailPhotoPath = thumbnailPhotoPath;
    }

    public CursistPartial(Cursist cursist) {
        id = cursist.getId();
        voornaam = cursist.getVoornaam();
        tussenvoegsel = cursist.getTussenvoegsel();
        achternaam = cursist.getAchternaam();
        verborgen = cursist.isVerborgen();
        hoogsteDiploma = cursist.getHoogsteDiploma();
        thumbnailPhotoPath = cursist.getThumbnailPhotoPath();
    }

    public void setHoogsteDiploma(String hoogsteDiploma) {
        this.hoogsteDiploma = hoogsteDiploma;
    }

    CursistPartial(Parcel in) {
        id = in.readString();
        voornaam = in.readString();
        tussenvoegsel = in.readString();
        achternaam = in.readString();
        verborgen = in.readByte() != 0;
        hoogsteDiploma = in.readString();
    }


    public String getId() {
        if(id == null) {
            return "";
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getVoornaam() {
        if(voornaam == null ) {
            return "";
        }
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getTussenvoegsel() {
        if(tussenvoegsel == null) {
            return "";
        }
        return tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        if(achternaam == null){
            return "";
        }
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public boolean isVerborgen() {
        return verborgen;
    }

    public void setVerborgen(boolean verborgen) {
        this.verborgen = verborgen;
    }

    public void toggleVerborgen() {
        verborgen = !verborgen;
    }

    public String nameToString() {
        String tussenstuk = "";
        if (tussenvoegsel != null && !tussenvoegsel.equals(""))
            tussenstuk = tussenvoegsel + " ";

        return voornaam + " " + tussenstuk + achternaam;
    }

    public String getHoogsteDiploma() {
        // todo either remove this from CursistListActivity or add to Partial Cursist.
//
//        if (cursistHeeftDiplomas == null)
//            return "";
//
//        CursistHeeftDiploma chdHolder = null;
//        for (CursistHeeftDiploma cursistHeeftDiploma : cursistHeeftDiplomas) {
//            if (chdHolder == null) {
//                chdHolder = cursistHeeftDiploma;
//            } else if (cursistHeeftDiploma.getDiploma().getNivo() > chdHolder.getDiploma().getNivo()) {
//                chdHolder = cursistHeeftDiploma;
//            }
//        }
//
//        if (chdHolder == null)
//            return "";
//        return chdHolder.getDiploma().toString();
        return "";
    }



    public static final Creator<CursistPartial> CREATOR = new Creator<CursistPartial>() {
        @Override
        public CursistPartial createFromParcel(Parcel in) {
            return new CursistPartial(in);
        }

        @Override
        public CursistPartial[] newArray(int size) {
            return new CursistPartial[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(voornaam);
        dest.writeString(tussenvoegsel);
        dest.writeString(achternaam);
        dest.writeByte((byte) (verborgen ? 1 : 0));
        dest.writeString(hoogsteDiploma);

    }
}
