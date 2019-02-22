package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CursistPartial implements Parcelable {
    protected String id;
    protected String voornaam;
    protected String tussenvoegsel;
    protected String achternaam;

//    protected CursistFoto cursistFoto;
    protected boolean verborgen = false;

    public CursistPartial() {

    }

    public CursistPartial(Cursist cursist) {
        id = cursist.getId();
        voornaam = cursist.getVoornaam();
        tussenvoegsel = cursist.getTussenvoegsel();
        achternaam = cursist.getAchternaam();
    }

    protected CursistPartial(Parcel in) {
        id = in.readString();
        voornaam = in.readString();
        tussenvoegsel = in.readString();
        achternaam = in.readString();
//        cursistFoto = in.readParcelable(CursistFoto.class.getClassLoader());
        verborgen = in.readByte() != 0;
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

//    public CursistFoto getCursistFoto() {
//        return cursistFoto;
//    }
//
//    public void setCursistFoto(CursistFoto cursistFoto) {
//        this.cursistFoto = cursistFoto;
//    }

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
//        dest.writeParcelable(cursistFoto, flags);
        dest.writeByte((byte) (verborgen ? 1 : 0));

    }
}
