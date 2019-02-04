package nl.multimedia_engineer.cwo_app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by sonja on 3/14/2017.
 * Diploma information
 */
public class Diploma implements Parcelable {
    private Long id;
    private String titel;
    private List<DiplomaEis> diplomaEis;
    private int nivo;

    public Diploma(Long id, String titel, int nivo, List<DiplomaEis> diplomaEis) {
        this.id = id;
        this.titel = titel;
        this.diplomaEis = diplomaEis;
        this.nivo = nivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public List<DiplomaEis> getDiplomaEis() {
        return diplomaEis;
    }

    public void setDiplomaEis(List<DiplomaEis> diplomaEis) {
        this.diplomaEis = diplomaEis;
    }

    public int getNivo() {
        return nivo;
    }

    public void setNivo(int nivo) {
        this.nivo = nivo;
    }

    @Override
    public String toString() {
        return titel + " " + nivo;
    }

    public boolean equals(Diploma diploma) {
        return (id.equals(diploma.id));
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
        id = parcel.readLong();
        titel = parcel.readString();
        nivo = parcel.readInt();
        //parcel.readTypedList(diplomaEis, DiplomaEis.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(titel);
        parcel.writeInt(nivo);
        //parcel.writeTypedList(diplomaEis);
    }
}