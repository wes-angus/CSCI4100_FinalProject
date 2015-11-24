//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class LoadDatabaseInfoTask extends AsyncTask<Object, Void, List<Game>>
{
    private DatabaseListener listener = null;
    private GameDBHelper dbHelper = null;
    boolean addingItems = false;

    public LoadDatabaseInfoTask(DatabaseListener listener, Context context)
    {
        this.listener = listener;
        dbHelper = new GameDBHelper(context);
    }

    @Override
    protected List<Game> doInBackground(Object... params)
    {
        /*
        This variable determines which database function to use
        0 = Add multiple games and get the list of games, 1 = get the list of games only
        */
        addingItems = (boolean) params[0];
        List<Game> games = (List<Game>) params[1];

        if(addingItems)
        {
            for (Game game : games)
            {
                addGame(game);
            }
        }
        games = getGames();

        return games;
    }

    private void addGame(Game game)
    {
        dbHelper.addGame(game.getTitle(), game.getReleaseDate(), game.getDescription(), game.getLink());
    }

    private List<Game> getGames()
    {
        return dbHelper.getAllGames();
    }

    @Override
    protected void onPostExecute(List<Game> resultingGames)
    {
        //Sends list of all games in the database to the BrowseGames activity
        listener.syncGames(resultingGames, addingItems);
    }
}
