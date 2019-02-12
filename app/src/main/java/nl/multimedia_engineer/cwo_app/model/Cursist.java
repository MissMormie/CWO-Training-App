package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;

public class Cursist extends CursistPartial implements Parcelable{

    // TODO: change this holder variable to the CursistFoto class.
    private String fotoFileBase64;

    public Date paspoortDate;
    public Long paspoort;
    public String opmerking;

    private Set<Diploma> diplomaSet;
    private Set<DiplomaEis> diplomaEisSet;

    // todo remove this.
    private List<CursistHeeftDiploma> cursistHeeftDiplomas;
    private List<CursistBehaaldEis> cursistBehaaldEis;

    public Cursist() {

    }



    @Nullable
    public Date getPaspoortDate() {
        return paspoortDate;
    }

    public void setPaspoortDate(Date paspoortDate) {
        this.paspoortDate = paspoortDate;
    }

    public String getOpmerking() {
        if(opmerking.equals("null")) {
            return "";
        }

        return opmerking;
    }

    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }

    public Cursist(CursistPartial cursist) {
        this.setId(cursist.getId());
        this.setVoornaam(cursist.getVoornaam());
        this.setTussenvoegsel(cursist.getTussenvoegsel());
        this.setAchternaam(cursist.getAchternaam());
        this.setCursistFoto(cursist.getCursistFoto());
        this.setVerborgen(cursist.isVerborgen());
    }

    public Cursist(String id, String voornaam, String tussenvoegsel, String achternaam, Date paspoortDate, String opmerking, List<CursistBehaaldEis> cursistBehaaldEis, List<CursistHeeftDiploma> cursistHeeftDiplomas, CursistFoto cursistFoto, boolean verborgen) {
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.paspoortDate = paspoortDate;
        this.opmerking = opmerking;
        this.cursistBehaaldEis = cursistBehaaldEis;
        this.cursistHeeftDiplomas = cursistHeeftDiplomas;
        this.cursistFoto = cursistFoto;
        this.verborgen = verborgen;
    }

    public Cursist(String id, String voornaam) {
        this.opmerking = "opmerking iets over koud water en wind en zon.";
        this.paspoortDate = null;
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = "";
        this.achternaam = "Duijvesteijn";
    }

    public Set<Diploma> getDiplomaList() {
        return diplomaSet;
    }

    public void setDiplomaList(Set<Diploma> diplomaList) {
        this.diplomaSet = diplomaList;
    }

    public void addDiploma(Diploma diploma) {
        if(diplomaSet == null) {
            diplomaSet = new HashSet<>();
        }

        diplomaSet.add(diploma);
    }

    public void addDiplomeEis(DiplomaEis diplomaEis) {
        if(diplomaEisSet == null) {
            diplomaEisSet = new HashSet<>();
        }

        diplomaEisSet.add(diplomaEis);
    }

    public void removeDiplomaEis(DiplomaEis diplomaEis) {
        if(diplomaEisSet == null) {
            return;
        }
        diplomaEisSet.remove(diplomaEis);
    }

    public void removeDiploma(Diploma diploma) {
        if(diplomaSet == null) {
            return;
        }
        diplomaSet.remove(diploma);
    }

    public List<CursistBehaaldEis> getCursistBehaaldEis() {
        return cursistBehaaldEis;
    }

    public void setCursistBehaaldEis(List<CursistBehaaldEis> cursistBehaaldEis) {
        this.cursistBehaaldEis = cursistBehaaldEis;
    }

    /**
     * Keeps current paspoortDate date if there is one already.
     */
    public void heeftPaspoort(boolean heeftPaspoort) {
        if(!heeftPaspoort) {
            paspoortDate = null;
            return;
        }
        if(paspoortDate != null)
            return;
        paspoortDate = new Date();

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



    // Make this smarter with a map so i don't have to run through everything every time. Only the first time. (together with isAlleEisenBehaald)
    // Low prio, low numbers make this not very slow.
    public boolean isEisBehaald(DiplomaEis diplomaEis) {
        if(diplomaEisSet != null) {
            return diplomaEisSet.contains(diplomaEis);
        }
        return false;
    }

    /**
     * Checks if all diplomaEisen in the list are attained by the cursist.
     */
    public boolean isAlleEisenBehaald(List<DiplomaEis> diplomaEisList) {
        if(diplomaEisSet == null) {
            return false;
        }
        for (DiplomaEis diplomaEis : diplomaEisList) {
            // Als 1 eis niet is behaald, is niet alles behaald, dus return false.
            if(!diplomaEisSet.contains(diplomaEis)) {
                return false;
            }
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

    public boolean hasDiploma(String diplomaId) {
        if(diplomaSet == null || diplomaSet.isEmpty()) {
            return false;
        }

        for(Diploma diploma : diplomaSet) {
            if(diploma.getId().equals(diplomaId)) {
                return true;
            }
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
        parcel.writeString(id);
        parcel.writeString(voornaam);
        parcel.writeString(tussenvoegsel);
        parcel.writeString(achternaam);
        parcel.writeString(opmerking);
        parcel.writeValue(verborgen);
        if (paspoortDate != null) {
            parcel.writeLong(paspoortDate.getTime());
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
        id = parcel.readString();
        voornaam = parcel.readString();
        tussenvoegsel = parcel.readString();
        achternaam = parcel.readString();
        opmerking = parcel.readString();
        verborgen = (Boolean) parcel.readValue(null);

        // Get around paspoortDate sometimes being null.
        Long paspoortTemp = parcel.readLong();

        if (paspoortTemp == 0L) {
            paspoortDate = null;
        } else {
            paspoortDate = new Date(paspoortTemp);
        }

        cursistFoto = parcel.readParcelable(CursistFoto.class.getClassLoader());
        //parcel.readTypedList(getCursistBehaaldEis(), CursistBehaaldEis.CREATOR);
    }

    // ---------------------------------------------------------------------------------------------

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
