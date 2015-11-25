//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable
{
    private String title;
    private String releaseDate;
    private String description;
    private String link;
    private String whenWillBuy;

    public Game(String title, String releaseDate, String description, String link) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.description = description;
        this.link = link;
        whenWillBuy = "";
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getLink() {
        return link;
    }

    public void setWhenWillBuy(String whenWillBuy) {
        this.whenWillBuy = whenWillBuy;
    }
    public String getWhenWillBuy() {
        return whenWillBuy;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(title);
        out.writeString(releaseDate);
        out.writeString(description);
        out.writeString(link);
        out.writeString(whenWillBuy);
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>()
    {
        public Game createFromParcel(Parcel in)
        {
            return new Game(in);
        }

        public Game[] newArray(int size)
        {
            return new Game[size];
        }
    };

    private Game(Parcel in)
    {
        title = in.readString();
        releaseDate = in.readString();
        description = in.readString();
        link = in.readString();
        whenWillBuy = (in.readString());
    }
}
