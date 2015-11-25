//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DownloadGameReleasesTask extends AsyncTask<String, Void, List<Game>>{
    private GameDataListener listener = null;
    private Exception exception = null;

    public DownloadGameReleasesTask(GameDataListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected List<Game> doInBackground(String... params)
    {
        List<Game> gameData = new ArrayList<>();
        //Download list of new game releases in background
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            URL url = new URL(params[0]);
            Document document = builder.parse(url.openStream());

            NodeList games = document.getElementsByTagName("item");
            for (int i = 0; i < games.getLength(); i++)
            {
                Node gameNode = games.item(i);
                if(gameNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element gameElement = (Element)gameNode;
                    NodeList titleElements = gameElement.getElementsByTagName("title");
                    String title = titleElements.item(0).getTextContent();
                    NodeList releaseDateElements = gameElement.getElementsByTagName("pubDate");
                    String releaseDate = releaseDateElements.item(0).getTextContent();
                    releaseDate = releaseDate.substring(0, releaseDate.indexOf(":") - 3);
                    NodeList descElements = gameElement.getElementsByTagName("description");
                    String description = descElements.item(0).getTextContent();
                    NodeList linkElements = gameElement.getElementsByTagName("link");
                    String link = linkElements.item(0).getTextContent();
                    gameData.add(new Game(title, releaseDate, description, link));
                }
            }
        }
        catch (Exception e)
        {
            exception = e;
        }
        return gameData;
    }

    public Exception getException()
    {
        return this.exception;
    }

    @Override
    protected void onPostExecute(List<Game> result)
    {
        //Show the news stories in the UI
        if(this.exception != null)
        {
            exception.printStackTrace();
        }

        this.listener.setGames(result);
    }
}
