//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

public class Game {
    private String title;
    private String releaseDate;
    private String description;
    private String link;

    public Game(String title, String releaseDate, String description, String link) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.description = description;
        this.link = link;
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
}