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
        0 = Add 1 or more games, 1 = get the list of games only, 2 = Update the given game,
        3 = Delete 1 or more games only, 4 = get the list of "bought" games,
        5 = get the list of recently removed games, 6 = get the list of games that may have expired,
        7 = Delete 1 or more games and get the list of games
        */
        option = (short) params[0];
        List<Game> games = new ArrayList<>();
        Game singleGame = null;
        if(option == 0 || option == 3 || option == 7)
        {
            if(params[1] != null)
            {
                games = (List<Game>) params[1];
            }
        }
        else if(option == 2)
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

    private List<Game> getGames()
    {
        return dbHelper.getAllGames();
    }

    private List<Game> getBoughtGames()
    {
        return dbHelper.getAllBoughtGames();
    }

    private List<Game> getRemovedGames()
    {
        return dbHelper.getAllRemovedGames();
    }

    private List<Game> getPossiblyExpiredGames()
    {
        return dbHelper.getAllPossiblyExpiredGames();
    }

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
