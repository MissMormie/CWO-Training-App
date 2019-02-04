package nl.multimedia_engineer.cwo_app.model;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sonja on 3/30/2017.
 * Has Base64 string of cursist foto's
 */

public class CursistFoto implements Parcelable {
    private final Long id;
    private String thumbnail;
    private String image;

    public CursistFoto(Long id, String thumbnail) {
        this.id = id;
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // ---------------------------- Support for Parcelable --------------------------------------- //
    public static final Parcelable.Creator<CursistFoto> CREATOR = new Parcelable.Creator<CursistFoto>() {
        @Override
        public CursistFoto createFromParcel(Parcel source) {
            return new CursistFoto(source);
        }

        @Override
        public CursistFoto[] newArray(int size) {
            return new CursistFoto[size];
        }
    };

    // Note, the order IS important, if it's not the same as when parceling it doesn't work.
    private CursistFoto(Parcel parcel) {
        id = parcel.readLong();
        thumbnail = parcel.readString();

        // Image is too large to send through parcelable. It will crash without saying anything.
        //image = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(thumbnail);
        //dest.writeString(image);
    }
}