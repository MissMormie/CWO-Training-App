package nl.multimedia_engineer.watersport_training.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import java.io.File;

@Keep
public class CursistPartial implements Parcelable {
    // Protected for use with firebase.
    protected String id;
    protected String voornaam;
    protected String tussenvoegsel;
    protected String achternaam;
    protected boolean verborgen = false;
    protected String hoogsteDiploma;
    protected String photoPathThumbnail;
    protected transient File photoFileThumbnail;

    public CursistPartial() {

    }


    public CursistPartial(Cursist cursist) {
        id = cursist.getId();
        voornaam = cursist.getVoornaam();
        tussenvoegsel = cursist.getTussenvoegsel();
        achternaam = cursist.getAchternaam();
        verborgen = cursist.isVerborgen();
        hoogsteDiploma = cursist.getHoogsteDiploma();
        photoPathThumbnail = cursist.getPhotoPathThumbnail();
        photoFileThumbnail = cursist.getPhotoFileThumbnail();
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
        photoPathThumbnail = in.readString();

        String photoFileString = in.readString();
        if(photoFileString != null && !photoFileString.isEmpty()) {
            photoFileThumbnail = new File(photoFileString);
        }
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

    public String getPhotoPathThumbnail() {
        return photoPathThumbnail;
    }

    public void setPhotoPathThumbnail(String photoPathThumbnail) {
        this.photoPathThumbnail = photoPathThumbnail;
    }

    public File getPhotoFileThumbnail() {
        return photoFileThumbnail;
    }

    public void setPhotoFileThumbnail(File photoFileThumbnail) {
        this.photoFileThumbnail = photoFileThumbnail;
    }

    public String nameToString() {
        String tussenstuk = "";
        if (tussenvoegsel != null && !tussenvoegsel.equals(""))
            tussenstuk = tussenvoegsel + " ";

        return voornaam + " " + tussenstuk + achternaam;
    }

    public String getHoogsteDiploma() {
        return hoogsteDiploma;
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
        dest.writeString(photoPathThumbnail);

        if(getPhotoFileThumbnail() != null) {
            dest.writeString(getPhotoFileThumbnail().getAbsolutePath());
        } else {
            dest.writeString("");
        }

    }
}
