//Authors: Wesley Angus, Montgomery Alban

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

//Async task that performs operations on an SQLite database
public class LoadDatabaseInfoTask extends AsyncTask<Object, Void, List<Game>>
{
    // constants to use in place of numbers
    public static class SYNC_ENUM {
        final static short
                NON_OPTION = -1,
                ADD_GAMES = 0,
                GET_GAMES_LIST = 1,
                UPDATE_GAME_SINGLE = 2,
                DELETE_GAMES = 3,
                GET_BOUGHT_GAMES = 4,
                GET_REMOVED_GAMES = 5,
                GET_EXPIRED_GAMES = 6,
                REMOVE_AND_SHOW = 7,
                DELETE_ALL_GAMES = 8;
    }

    private DatabaseListener listener = null;
    private GameDBHelper dbHelper = null;
    short option = SYNC_ENUM.NON_OPTION;

    //Use getApplicationContext() for context
    public LoadDatabaseInfoTask(DatabaseListener listener, Context context)
    {
        this.listener = listener;
        dbHelper = new GameDBHelper(context);
    }

    //@SuppressWarnings("unchecked") // a attribute for suppressing the unchecked warning
    @Override
    protected List<Game> doInBackground(Object... params)
    {
        if(params == null || params.length == 0)
            return null;
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

        if(params.length > 1 && params[1] != null) {
            //If you are adding or deleting multiple games, 2nd argument is a list of Game objects
            if (option == SYNC_ENUM.ADD_GAMES
                    || option == SYNC_ENUM.DELETE_GAMES
                    || option == SYNC_ENUM.REMOVE_AND_SHOW) {
                    /*
                    Ignore this warning, if the option is one of the ones given above, the 2nd
                    argument should be a list, otherwise the task is being used incorrectly
                    */
                games = (List<Game>) params[1];
            } else if (option == SYNC_ENUM.UPDATE_GAME_SINGLE && params[1] instanceof Game) {
                singleGame = (Game) params[1];
            }
        }

        //Based on the given option parameter, perform a certain database operation
        switch (option)
        {
            case SYNC_ENUM.ADD_GAMES:
                for (Game game : games)
                {
                    addGame(game);
                }
                break;
            case SYNC_ENUM.GET_GAMES_LIST:
                games = getGames();
                break;
            case SYNC_ENUM.UPDATE_GAME_SINGLE:
                updateGame(singleGame);
                games = getGames();
                break;
            case SYNC_ENUM.DELETE_GAMES:
                for (Game game : games)
                {
                    deleteGame(game);
                }
                break;
            case SYNC_ENUM.GET_BOUGHT_GAMES:
                games = getBoughtGames();
                break;
            case SYNC_ENUM.GET_REMOVED_GAMES:
                games = getRemovedGames();
                break;
            case SYNC_ENUM.GET_EXPIRED_GAMES:
                games = getPossiblyExpiredGames();
                break;
            case SYNC_ENUM.REMOVE_AND_SHOW:
                for (Game game : games)
                {
                    deleteGame(game);
                }
                games = getGames();
                break;
            case SYNC_ENUM.DELETE_ALL_GAMES:
                deleteAllGames();
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

    private void deleteAllGames()
    {
        dbHelper.deleteAllGames();
    }

    @Override
    protected void onPostExecute(List<Game> resultingGames)
    {
        //Sends list of all games in the database to the BrowseGames activity
        listener.syncGames(resultingGames, option);
    }
}
