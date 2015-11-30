//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

//Async task that performs operations on an SQLite database
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

    //@SuppressWarnings("unchecked")
    @Override
    protected List<Game> doInBackground(Object... params)
    {
        /*
        This variable determines which database method to execute
        0 = Add 1 or more games, 1 = get the list of games only, 2 = Update the given game,
        3 = Delete 1 or more games only, 4 = Get the list of "bought" games,
        5 = Get the list of recently removed games, 6 = Get the list of games that may have expired,
        7 = Delete 1 or more games and get the list of games, 8 = Delete all games in the database
        */
        option = (short) params[0];
        List<Game> games = new ArrayList<>();
        Game singleGame = null;

        //If you are adding or deleting multiple games, 2nd argument is a list of Game objects
        if(option == 0 || option == 3 || option == 7)
        {
            if(params[1] != null)
            {
                /*
                Ignore this warning, if the option is one of the ones given above, the 2nd
                argument should be a list, otherwise the task is being used incorrectly
                */
                games = (List<Game>) params[1];
            }
        }
        else if(option == 2 && params[1] instanceof Game)
        {
            if(params[1] != null)
            {
                singleGame = (Game) params[1];
            }
        }

        //Based on the given option parameter, perform a certain database operation
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
                for (Game game : games)
                {
                    deleteGame(game);
                }
                break;
            case 4:
                games = getBoughtGames();
                break;
            case 5:
                games = getRemovedGames();
                break;
            case 6:
                games = getPossiblyExpiredGames();
                break;
            case 7:
                for (Game game : games)
                {
                    deleteGame(game);
                }
                games = getGames();
                break;
        }

        return games;
    }

    //Adds a game to the database if it is not already in the database
    private void addGame(Game game)
    {
        if(!dbHelper.gameExists(game.getTitle()))
        {
            dbHelper.addGame(game);
        }
    }

    //Updates a game in the database
    private void updateGame(Game game)
    {
        dbHelper.updateGame(game);
    }

    //Gets the list of new game releases from the database
    private List<Game> getGames()
    {
        return dbHelper.getAllGames();
    }

    //Gets the list of bought games from the database
    private List<Game> getBoughtGames()
    {
        return dbHelper.getAllBoughtGames();
    }

    //Gets the list of removed games from the database
    private List<Game> getRemovedGames()
    {
        return dbHelper.getAllRemovedGames();
    }

    //Gets the list of games that may have expired from the database
    private List<Game> getPossiblyExpiredGames()
    {
        return dbHelper.getAllPossiblyExpiredGames();
    }

    //Deletes a game from the database
    private void deleteGame(Game game)
    {
        dbHelper.deleteGame(game.getTitle());
    }

    @Override
    protected void onPostExecute(List<Game> resultingGames)
    {
        //Sends list of all games in the database to the BrowseGames activity
        listener.syncGames(resultingGames, option);
    }
}
