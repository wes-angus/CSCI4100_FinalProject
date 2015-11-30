// Authors: Montgomery Alban
package csci4100.uoit.ca.csci4100_final_project;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * A simple class for storing the location data of a picker
 */
public class StoreDetails implements Parcelable {

    private LatLng latLng;
    private String name;
    private String address;
    private String attributions;

    public LatLng getLatLng() {
        return latLng;
    }

    public String getStoreTitle() {
        return name + "\n" + address;
    }

    public StoreDetails(final String name, final String address,
                            final String attributions, final LatLng latLng) {
        this.name = name;
        this.address = address;
        this.attributions = attributions;
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(attributions);
        dest.writeDouble(latLng.latitude);
        dest.writeDouble(latLng.longitude);
    }

    public static final Parcelable.Creator<StoreDetails> CREATOR = new Parcelable.Creator<StoreDetails>()
    {
        public StoreDetails createFromParcel(Parcel in)
        {
            return new StoreDetails(in);
        }

        public StoreDetails[] newArray(int size)
        {
            return new StoreDetails[size];
        }
    };

    private StoreDetails(Parcel in)
    {
        name = in.readString();
        address = in.readString();
        attributions = in.readString();
        final double lat = in.readDouble();
        final double lng = in.readDouble();
        latLng = new LatLng(lat,lng);
    }
}
