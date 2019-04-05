package nl.multimedia_engineer.watersport_training.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;
import java.util.Set;

public class DiplomaEis implements Parcelable {

    private String id;
    private Diploma diploma;
    private String titel;
    private String tekst;
    private Set<CursistBehaaldEis> cursistBehaaldEis;


    // Used to save whether the checkbox for this eis is checked.
    // determine if this belongs here or should move to a seperate class that holds this info.
    // since it doesn't fit in the model well.
    private boolean checked;

    public DiplomaEis() {}

    public DiplomaEis(String id, String titel, String omschrijving) {
        this.id = id;
        this.titel = titel;
        this.tekst = omschrijving;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Diploma getDiploma() {
        return diploma;
    }

    public void setDiploma(Diploma diploma) {
        this.diploma = diploma;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public Set<CursistBehaaldEis> getCursistBehaaldEis() {
        return cursistBehaaldEis;
    }

    public void setCursistBehaaldEis(Set<CursistBehaaldEis> cursistBehaaldEis) {
        this.cursistBehaaldEis = cursistBehaaldEis;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        checked = !checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiplomaEis that = (DiplomaEis) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ---------------------------- Support for Parcelable --------------------------------------- //
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DiplomaEis> CREATOR = new Parcelable.Creator<DiplomaEis>() {
        @Override
        public DiplomaEis createFromParcel(Parcel source) {
            return new DiplomaEis(source);
        }

        @Override
        public DiplomaEis[] newArray(int size) {
            return new DiplomaEis[size];
        }
    };

    private DiplomaEis(Parcel parcel) {
        id = parcel.readString();
        titel = parcel.readString();
        tekst = parcel.readString();
        diploma = parcel.readParcelable(Diploma.class.getClassLoader());

    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(titel);
        parcel.writeString(tekst);
        // not great solution because now every diplomaEis will have it's own diploma object..
        parcel.writeParcelable(diploma, flags);
    }
}