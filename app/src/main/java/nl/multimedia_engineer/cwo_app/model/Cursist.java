package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;

public class Cursist extends CursistPartial implements Parcelable{

    private String fotoFileBase64;

    private Long paspoort;
    private String opmerking;

    private Set<Diploma> diplomaSet;
    private Set<DiplomaEis> diplomaEisSet;


    public Cursist() {

    }

    public boolean isPartialCursist() {
        if((fotoFileBase64 == null || fotoFileBase64.isEmpty())
        && (paspoort == null || paspoort == 0L)
        && (opmerking == null || opmerking.isEmpty())
        && (diplomaSet == null || diplomaSet.isEmpty())
        && (diplomaEisSet == null || diplomaEisSet.isEmpty())) {
            return true;
        }
        return false;
    }

    @Nullable
    public Long getPaspoort() {
        if(paspoort == null || paspoort == 0L) {
            return null;
        }
        return paspoort;
    }

    public void setPaspoort(Long paspoort) {
        this.paspoort = paspoort;
    }

    public Set<Diploma> getDiplomaSet() {
        return diplomaSet;
    }

    public void setDiplomaSet(Set<Diploma> diplomaSet) {
        this.diplomaSet = diplomaSet;
    }

    public Set<DiplomaEis> getDiplomaEisSet() {
        return diplomaEisSet;
    }

    public void setDiplomaEisSet(Set<DiplomaEis> diplomaEisSet) {
        this.diplomaEisSet = diplomaEisSet;
    }

    public String getOpmerking() {
        if(opmerking == null || opmerking.equals("null")) {
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
        this.setVerborgen(cursist.isVerborgen());
    }

    public Cursist(String id, String voornaam, String tussenvoegsel, String achternaam, Long paspoort, String opmerking, boolean verborgen) {
        this.id = id;
        this.voornaam = voornaam;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.paspoort = paspoort;
        this.opmerking = opmerking;
        this.verborgen = verborgen;
    }

    public void addDiploma(Diploma diploma) {
        if(diplomaSet == null) {
            diplomaSet = new HashSet<>();
        }

        diplomaSet.add(diploma);
    }
    
    public void toggleDiploma(Diploma diploma) {
        if(diplomaSet == null) {
            diplomaSet = new HashSet<>();
        }
        if(!diplomaSet.remove(diploma)) {
            diplomaSet.add(diploma);
        }
    }

    public void toggleDiplomaEis(DiplomaEis diplomaEis) {
        if(diplomaEisSet == null) {
            diplomaEisSet = new HashSet<>();
        }
        if(!diplomaEisSet.remove(diplomaEis)) {
            diplomaEisSet.add(diplomaEis);
        }
    }

    public void addDiplomeEis(DiplomaEis diplomaEis) {
        if(diplomaEisSet == null) {
            diplomaEisSet = new HashSet<>();
        }

        diplomaEisSet.add(diplomaEis);
    }

    /**
     * Keeps current paspoortDate date if there is one already.
     */
    public void heeftPaspoort(boolean heeftPaspoort) {
        if(!heeftPaspoort) {
            paspoort = null;
            return;
        }
        if(paspoort != null && paspoort != 0L)
            return;
        paspoort = System.currentTimeMillis();

    }

    public String getFotoFileBase64() {
        return fotoFileBase64;
    }

    public void setFotoFileBase64(String fotoFileBase64) {
        this.fotoFileBase64 = fotoFileBase64;
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
     * Checks if all behaaldeEisen in the list are attained by the cursist.
     */
    public boolean isAlleEisenBehaald(List<DiplomaEis> diplomaEisList) {
        if(diplomaEisList == null || diplomaEisList.isEmpty()) {
            return true;
        }

        for (DiplomaEis diplomaEis : diplomaEisList) {
            // Als 1 eis niet is behaald, is niet alles behaald, dus return false.

            if(diplomaEisSet == null || diplomaEisSet != null && !diplomaEisSet.contains(diplomaEis)) {
                if(diplomaSet == null || diplomaSet != null && !diplomaSet.contains(diplomaEis.getDiploma())) {
                    return false;
                }
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
        super.writeToParcel(parcel, flags);
//        parcel.writeString(id);
//        parcel.writeString(voornaam);
//        parcel.writeString(tussenvoegsel);
//        parcel.writeString(achternaam);
//        parcel.writeString(opmerking);
//        parcel.writeByte((byte) (verborgen ? 1 : 0));
        if (paspoort != null) {
            parcel.writeLong(paspoort);
        } else {
            parcel.writeLong(0L);
        }

        parcel.writeString(fotoFileBase64);

        if(diplomaSet == null || diplomaSet.size() == 0) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(diplomaSet.size());
            Diploma[] diploma = new Diploma[diplomaSet.size()];
            diplomaSet.toArray(diploma);
            parcel.writeTypedArray(diploma, 0);
        }

        if(diplomaEisSet == null || diplomaEisSet.size() == 0) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(diplomaEisSet.size());
            DiplomaEis[] diplomaEisen = new DiplomaEis[diplomaEisSet.size()];
            diplomaEisSet.toArray(diplomaEisen);
            parcel.writeTypedArray(diplomaEisen, 0);

            // save eisen.
        }


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
        super(parcel);
//        id = parcel.readString();
//        voornaam = parcel.readString();
//        tussenvoegsel = parcel.readString();
//        achternaam = parcel.readString();
//        opmerking = parcel.readString();
//        verborgen = parcel.readByte() != 0;

        // Get around paspoortDate sometimes being null.
        Long paspoortTemp = parcel.readLong();

        if (paspoortTemp == 0L) {
            paspoort = null;
        } else {
            paspoort = paspoortTemp;
        }

        fotoFileBase64 = parcel.readString();

        diplomaSet = new HashSet<>();
        int diplomaSetSize = parcel.readInt();
        if(diplomaSetSize != 0 ) {
            Diploma[] diplomas = new Diploma[diplomaSetSize];
            parcel.readTypedArray(diplomas, Diploma.CREATOR);

            for (Diploma diploma : diplomas) {
                diplomaSet.add(diploma);
            }
        }

        diplomaEisSet = new HashSet<>();
        int diplomaEisSetSize = parcel.readInt();
        if(diplomaEisSetSize != 0) {
            DiplomaEis[] diplomaEisen = new DiplomaEis[diplomaEisSetSize];
            parcel.readTypedArray(diplomaEisen, DiplomaEis.CREATOR);
            for (DiplomaEis diplomaEis : diplomaEisen) {
                diplomaEisSet.add(diplomaEis);
            }
        }

    }


}
