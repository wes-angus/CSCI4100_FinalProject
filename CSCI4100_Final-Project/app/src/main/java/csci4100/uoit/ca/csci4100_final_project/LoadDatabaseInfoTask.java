//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

//TODO: Add delete for multiple games
public class LoadDatabaseInfoTask extends AsyncTask<Object, Void, ArrayList<Game>>
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
    protected ArrayList<Game> doInBackground(Object... params)
    {
        /*
        This variable determines which database function to use
        0 = Add multiple games, 1 = get the list of games only, 2 = Update the given game,
        3 = Delete the given game, 4 = Delete multiple games
        */
        option = (short) params[0];
        ArrayList<Game> games = new ArrayList<>();
        Game singleGame = null;
        if(option == 0)
        {
            if(params[1] != null)
            {
                games = (ArrayList<Game>) params[1];
            }
        }
        else if(option == 2 || option == 3)
        {
            singleGame = (Game) params[1];
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
                updateGame(singleGame);
                games = getGames();
                break;
            case 3:
                deleteGame(singleGame);
                games = getGames();
                break;
        }

        return games;
    }

    private void addGame(Game game)
    {
        if(!dbHelper.gameExists(game.getTitle()))
        {
            dbHelper.addGame(game);
        }
    }

    private void updateGame(Game game)
    {
        dbHelper.updateGame(game);
    }

    private ArrayList<Game> getGames()
    {
        return dbHelper.getAllGames();
    }

    private void deleteGame(Game game)
    {
        dbHelper.deleteGame(game.getTitle());
    }

    @Override
    protected void onPostExecute(ArrayList<Game> resultingGames)
    {
        //Sends list of all games in the database to the BrowseGames activity
        listener.syncGames(resultingGames, option);
    }
}
