//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class LoadDatabaseInfoTask extends AsyncTask<Object, Void, List<Game>>
{
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
                REMOVE_AND_SHOW = 7;
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

    //@SuppressWarnings("unchecked")
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
        if(option == SYNC_ENUM.ADD_GAMES
                || option == SYNC_ENUM.DELETE_GAMES
                || option == SYNC_ENUM.REMOVE_AND_SHOW)
        {
            if(params[1] != null)
            {
                games = (List<Game>) params[1];
            }
        }
        else if(option == SYNC_ENUM.UPDATE_GAME_SINGLE && params[1] instanceof Game)
        {
            singleGame = (Game) params[1];
        }

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
