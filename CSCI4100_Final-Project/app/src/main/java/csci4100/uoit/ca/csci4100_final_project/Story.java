package csci4100.uoit.ca.csci4100_final_project;

/**
 * Created by 100449718 on 10/28/2015.
 */
public class Story {
    private String title;
    private String author;
    private String content;

    public Story(String title, String author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
    }

    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public String getContent() { return this.content; }

    public void setTitle(String newValue) { this.title = newValue; }
    public void setAuthor(String newValue) { this.author = newValue; }
    public void setContent(String newValue) { this.content = newValue; }
}