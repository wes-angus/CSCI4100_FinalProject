package csci4100.uoit.ca.csci4100_final_project;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by 100449718 on 10/28/2015.
 */
public class DownloadFeedTask extends AsyncTask<String, Void, String>{
    private StoryDataListener listener = null;
    private Exception exception = null;
    private ArrayList<Story> storyData = new ArrayList<>();

    public DownloadFeedTask(StoryDataListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params)
    {
        //download news story in background
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            URL url = new URL(params[0]);
            Document document = builder.parse(url.openStream());

            NodeList stories = document.getElementsByTagName("entry");
            for (int i = 0; i < stories.getLength(); i++)
            {
                Node storyNode = stories.item(i);
                if(storyNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element storyElement = (Element)storyNode;
                    NodeList authorElements = storyElement.getElementsByTagName("author");
                    Node authorNameNode = authorElements.item(0);
                    String author = "";
                    if(authorNameNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element authorNameElement = (Element)authorNameNode;
                        author = authorNameElement.getTextContent();
                        NodeList authorNameElements = authorNameElement.getElementsByTagName("name");
                        author = authorNameElements.item(0).getTextContent();
                    }
                    NodeList titleElements = storyElement.getElementsByTagName("title");
                    String title = titleElements.item(0).getTextContent();
                    NodeList contentElements = storyElement.getElementsByTagName("summary");
                    String content = contentElements.item(0).getTextContent();
                    storyData.add(new Story(title, author, content));
                }
            }
        }
        catch (Exception e)
        {
            exception = e;
        }
        return "stories downloaded";
    }

    public Exception getException()
    {
        return this.exception;
    }

    @Override
    protected void onPostExecute(String result)
    {
        //Show the news stories in the UI
        if(this.exception != null)
        {
            exception.printStackTrace();
        }

        this.listener.showStories(storyData);
    }
}
