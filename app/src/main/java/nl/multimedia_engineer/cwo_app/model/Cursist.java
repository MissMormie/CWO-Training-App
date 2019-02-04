package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.multimedia_engineer.cwo_app.util.DateUtil;

public class Cursist {
    public Long id;
    public String voornaam;
    public String tussenvoegsel;
    public String achternaam;
    private CursistFoto cursistFoto;
    private boolean verborgen = false;

    // TODO: change this holder variable to the CursistFoto class.
    private String fotoFileBase64;

    public Date paspoort;
    public String opmerking;
    private List<CursistBehaaldEis> cursistBehaaldEis;

    private List<CursistHeeftDiploma> cursistHeeftDiplomas;

    public Cursist() {
    }

    public Cursist(Long id, String voornaam, String tussenvoegsel, String achternaam, Date paspoort, String opmerking, List<CursistBehaaldEis> cursistBehaaldEis, List<CursistHeeftDiploma> cursistHeeftDiplomas, CursistFoto cursistFoto, boolean verborgen) {
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.paspoort = paspoort;
        this.opmerking = opmerking;
        this.cursistBehaaldEis = cursistBehaaldEis;
        this.cursistHeeftDiplomas = cursistHeeftDiplomas;
        this.cursistFoto = cursistFoto;
        this.verborgen = verborgen;
    }

    public Cursist(long id, String voornaam) {
        this.opmerking = "opmerking iets over koud water en wind en zon.";
        this.paspoort = null;
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = "";
        this.achternaam = "Duijvesteijn";
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

    public List<CursistBehaaldEis> getCursistBehaaldEis() {
        return cursistBehaaldEis;
    }

    public void setCursistBehaaldEis(List<CursistBehaaldEis> cursistBehaaldEis) {
        this.cursistBehaaldEis = cursistBehaaldEis;
    }

    /**
     * Keeps current paspoort date if there is one already.
     */
    public void heeftPaspoort(boolean heeftPaspoort) {
        if(!heeftPaspoort) {
            paspoort = null;
            return;
        }
        if(paspoort != null)
            return;
        paspoort = new Date();

    }

    public String getFotoFileBase64() {
        return fotoFileBase64;
    }

    public void setFotoFileBase64(String fotoFileBase64) {
        this.fotoFileBase64 = fotoFileBase64;
    }

    public List<CursistHeeftDiploma> getCursistHeeftDiplomas() {
        return cursistHeeftDiplomas;
    }

    public void setCursistHeeftDiplomas(List<CursistHeeftDiploma> cursistHeeftDiplomas) {
        this.cursistHeeftDiplomas = cursistHeeftDiplomas;
    }


    public CursistFoto getCursistFoto() {
        return cursistFoto;
    }

    public void setCursistFoto(CursistFoto cursistFoto) {
        this.cursistFoto = cursistFoto;
    }

    public String nameToString() {
        String tussenstuk = "";
        if (tussenvoegsel != null && !tussenvoegsel.equals(""))
            tussenstuk = tussenvoegsel + " ";

        return voornaam + " " + tussenstuk + achternaam;
    }

//    public String simpleCursistToJson() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("id", id);
//            jsonObject.put("voornaam", voornaam);
//            jsonObject.put("tussenvoegsel", tussenvoegsel);
//            jsonObject.put("achternaam", achternaam);
//            jsonObject.put("opmerkingen", opmerking);
//            jsonObject.put("verborgen", verborgen);
//            if (paspoort != null) {
//                jsonObject.put("paspoort", DateUtil.dateToYYYYMMDDString(paspoort));
//            } else {
//                jsonObject.put("paspoort", null);
//            }
//            if (fotoFileBase64 != null && !fotoFileBase64.equals(""))
//                jsonObject.put("fotoFileBase64", fotoFileBase64);
//
//
//            return jsonObject.toString();
//        } catch (JSONException e) {
//            return "";
//        }
//    }

    // Make this smarter with a map so i don't have to run through everything every time. Only the first time. (together with isAlleEisenBehaald)
    // Low prio, low numbers make this not very slow.
    public boolean isEisBehaald(DiplomaEis diplomaEis) {
        if (cursistBehaaldEis == null)
            return false;
        for (CursistBehaaldEis cbe : cursistBehaaldEis) {
            if (cbe.getDiplomaEis() != null && cbe.getDiplomaEis().getId().equals(diplomaEis.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all diplomaEisen in the list are attained by the cursist.
     */
    public boolean isAlleEisenBehaald(List<DiplomaEis> diplomaEisList) {
        for (DiplomaEis diplomaEis : diplomaEisList) {
            // Als 1 eis niet is behaald, is niet alles behaald, dus return false.
            if (!hasDiploma(diplomaEis.getDiploma().getId()) && !isEisBehaald(diplomaEis))
                return false;
        }
        return true;
    }

    /**
     * Checks if all diploma's in the list are attained by the cursist.
     */
    public boolean isAlleDiplomasBehaald(List<Diploma> diplomaList) {
        for (Diploma diploma : diplomaList) {
            // Als 1 diploma uit de lijst niet is gehaald, zijn ze niet allemaal behaald dus return false.
            if (!hasDiploma(diploma.getId()))
                return false;
        }
        return true;
    }

    public boolean hasDiploma(Long diplomaId) {
        if (cursistHeeftDiplomas == null)
            return false;
        for (CursistHeeftDiploma cursistHeeftDiploma : cursistHeeftDiplomas) {
            if (cursistHeeftDiploma.getDiploma().getId().equals(diplomaId))
                return true;
        }
        return false;
    }


    // ---------------------------- Support for Parcelable --------------------------------------- //

//    @Override
    public int describeContents() {
        return 0;
    }

//    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(voornaam);
        parcel.writeString(tussenvoegsel);
        parcel.writeString(achternaam);
        parcel.writeString(opmerking);
        parcel.writeValue(verborgen);
        if (paspoort != null) {
            parcel.writeLong(paspoort.getTime());
        } else {
            parcel.writeLong(0L);
        }
        parcel.writeParcelable(cursistFoto, 0);


    }

    public static final Parcelable.Creator<Cursist> CREATOR = new Parcelable.Creator<Cursist>() {
        @Override
        public Cursist createFromParcel(Parcel source) {
            return new Cursist(source);
        }

        @Override
        public Cursist[] newArray(int size) {
            return new Cursist[size];
        }
    };

    // Note, the order IS important, if it's not the same as when parceling it doesn't work.
    private Cursist(Parcel parcel) {
        id = parcel.readLong();
        voornaam = parcel.readString();
        tussenvoegsel = parcel.readString();
        achternaam = parcel.readString();
        opmerking = parcel.readString();
        verborgen = (Boolean) parcel.readValue(null);

        // Get around paspoort sometimes being null.
        Long paspoortTemp = parcel.readLong();

        if (paspoortTemp == 0L) {
            paspoort = null;
        } else {
            paspoort = new Date(paspoortTemp);
        }

        cursistFoto = parcel.readParcelable(CursistFoto.class.getClassLoader());
        //parcel.readTypedList(getCursistBehaaldEis(), CursistBehaaldEis.CREATOR);
    }

    // ---------------------------------------------------------------------------------------------
    public String getHoogsteDiploma() {

        if (cursistHeeftDiplomas == null)
            return "";

        CursistHeeftDiploma chdHolder = null;
        for (CursistHeeftDiploma cursistHeeftDiploma : cursistHeeftDiplomas) {
            if (chdHolder == null) {
                chdHolder = cursistHeeftDiploma;
            } else if (cursistHeeftDiploma.getDiploma().getNivo() > chdHolder.getDiploma().getNivo()) {
                chdHolder = cursistHeeftDiploma;
            }
        }

        if (chdHolder == null)
            return "";
        return chdHolder.getDiploma().toString();
    }

    /**
     * Checks cursistHeeftDiploma isBehaald, if it is it makes sure it's on the list on behaalde diploma's, otherwise it removes it.
     */
    public void addOrRemoveDiploma(CursistHeeftDiploma cursistHeeftDiploma) {
        if(cursistHeeftDiplomas == null)
            cursistHeeftDiplomas = new ArrayList<>();
        if(cursistHeeftDiploma.isBehaald())
            cursistHeeftDiplomas.add(cursistHeeftDiploma);
        else
            for(CursistHeeftDiploma chd: cursistHeeftDiplomas) {
                if(chd.getDiploma().getId().equals(cursistHeeftDiploma.getDiploma().getId()))
                    cursistHeeftDiplomas.remove(chd);
            }
    }

    /**
     * Checks CursistBehaaldEis isBehaald, if it is it makes sure it's on the list, otherwise it removes it.
     */
    public void addOrRemoveDiplomaEis(CursistBehaaldEis newCursistBehaaldEis) {
        if(cursistBehaaldEis == null)
            cursistBehaaldEis = new ArrayList<>();
        if(newCursistBehaaldEis.isBehaald())
            this.cursistBehaaldEis.add(newCursistBehaaldEis);
        else
            for(CursistBehaaldEis cbe: cursistBehaaldEis) {
                if(cbe.getDiplomaEis().getId().equals(newCursistBehaaldEis.getDiplomaEis().getId()))
                    cursistBehaaldEis.remove(cbe);
            }

    }
}
