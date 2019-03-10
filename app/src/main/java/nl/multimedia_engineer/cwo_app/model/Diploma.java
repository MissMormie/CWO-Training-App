package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sonja on 3/14/2017.
 * Diploma information
 */
public class Diploma implements Parcelable {
    private String id;
    private String titel;
    private List<DiplomaEis> diplomaEisList;
    private int nivo;

    public Diploma() {

    }

    public Diploma(String id, String titel, int nivo, List<DiplomaEis> diplomaEisList) {
        this.id = id;
        this.titel = titel;
        this.diplomaEisList = diplomaEisList;
        this.nivo = nivo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public List<DiplomaEis> getDiplomaEisList() {
        return diplomaEisList;
    }

    public void addDiplomaEis(DiplomaEis diplomaEis) {
        if(diplomaEis == null)
            return;
        if(diplomaEisList == null) {
            diplomaEisList = new ArrayList<>();
        }
        diplomaEisList.add(diplomaEis);
    }

    public void setDiplomaEisList(List<DiplomaEis> diplomaEisList) {
        this.diplomaEisList = diplomaEisList;
    }

    public int getNivo() {
        if(nivo == 0 && !id.isEmpty()) {
            try {
                int num = Integer.valueOf(id.substring(id.length()-1));
                nivo = num;
            } catch (Exception e) {
                // not a number, that's fine, we'll keep 0.
            }
        }

        return nivo;
    }

    public void setNivo(int nivo) {
        this.nivo = nivo;
    }

    @Override
    public String toString() {
        if(getNivo() == 0) {
            return "";
        }
        return getTitel() + " " + getNivo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diploma diploma = (Diploma) o;
        return Objects.equals(id, diploma.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

// ---------------------------- Support for Parcelable --------------------------------------- //

    public static final Parcelable.Creator<Diploma> CREATOR = new Parcelable.Creator<Diploma>() {
        @Override
        public Diploma createFromParcel(Parcel source) {
            return new Diploma(source);
        }

        @Override
        public Diploma[] newArray(int size) {
            return new Diploma[size];
        }
    };

    public Diploma(Parcel parcel) {
        id = parcel.readString();
        titel = parcel.readString();
        nivo = parcel.readInt();
        //parcel.readTypedList(diplomaEisList, DiplomaEis.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(titel);
        parcel.writeInt(nivo);
        //parcel.writeTypedList(diplomaEisList);
    }
}