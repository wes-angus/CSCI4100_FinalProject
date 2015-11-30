//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.Parcel;
import android.os.Parcelable;

//Class to store info about a game, downloaded from an RSS feed
public class Game implements Parcelable
{
    private String title;
    private String releaseDate;
    private String description;
    private String link; //Link to the game on the website hosting the feed
    private String whenWillBuy; //Value representing when (and if) the user wishes to buy the game

    public Game(String title, String releaseDate, String description, String link)
    {
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

    //Write the members variables to a parcel to enable sending the class via intents
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

    //Read the members variables from a parcel
    private Game(Parcel in)
    {
        title = in.readString();
        releaseDate = in.readString();
        description = in.readString();
        link = in.readString();
        whenWillBuy = (in.readString());
    }
}
