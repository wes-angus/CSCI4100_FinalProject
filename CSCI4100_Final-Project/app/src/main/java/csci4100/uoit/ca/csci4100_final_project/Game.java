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
    private boolean willBuy;

    public Game(String title, String releaseDate, String description, String link) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.description = description;
        this.link = link;
        willBuy = false;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public boolean isWillBuy() {
        return willBuy;
    }
    public void setWillBuy(boolean willBuy) {
        this.willBuy = willBuy;
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
        out.writeInt(willBuy ? 1 : 0);
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
        willBuy = (in.readInt() != 0);
    }
}