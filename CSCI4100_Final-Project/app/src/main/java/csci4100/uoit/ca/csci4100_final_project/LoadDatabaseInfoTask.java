//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/*
TODO: Add check that only adds a game if it does not already exist, to avoid
TODO: overwriting the "willBuy" property when adding the same game twice.
*/
public class LoadDatabaseInfoTask extends AsyncTask<Object, Void, List<Game>>
{
    private DatabaseListener listener = null;
    private GameDBHelper dbHelper = null;
    short option = -1;

    //Use getApplicationContext() for context
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
        0 = Add multiple games, 1 = get the list of games only, 2 = Update the list of games
        */
        option = (short) params[0];
        List<Game> games = new ArrayList<>();
        if(option != 1)
        {
            games = (List<Game>) params[1];
        }

        switch (option)
        {
            case 0:
                for (Game game : games)
                {
                    addGame(game);
                }
                break;
            case 1:
                games = getGames();
                break;
            case 2:
                for (Game game : games)
                {
                    updateGame(game);
                }
                break;
        }

        return games;
    }

    private void addGame(Game game)
    {
        dbHelper.addGame(game);
    }

    private void updateGame(Game game)
    {
        dbHelper.updateGame(game);
    }

    private List<Game> getGames()
    {
        return dbHelper.getAllGames();
    }

    @Override
    protected void onPostExecute(List<Game> resultingGames)
    {
        //Sends list of all games in the database to the BrowseGames activity
        listener.syncGames(resultingGames, option);
    }
}
