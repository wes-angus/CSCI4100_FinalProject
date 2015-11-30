//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameDBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_FILENAME = "upcoming_games.db";
    public static final String TABLE_NAME = "Games";

    public static final String CREATE_STMT = "CREATE TABLE " + TABLE_NAME + "(" +
            " title text primary key, releaseDate text not null," +
            " description text, link text not null, whenWillBuy text not null)";
    public static final String DROP_STMT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private String[] columns = new String[] { "title", "releaseDate", "description", "link",
            "whenWillBuy" };

    private Context context;

    public GameDBHelper(Context context)
    {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_STMT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        //Should be changed if database will need to be upgraded
        database.execSQL(DROP_STMT);
        database.execSQL(CREATE_STMT);
    }

    public void addGame(Game game)
    {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // insert the data into the database
        ContentValues values = new ContentValues();
        values.put("title", game.getTitle());
        values.put("releaseDate", game.getReleaseDate());
        values.put("description", game.getDescription());
        values.put("link", game.getLink());
        values.put("whenWillBuy", game.getWhenWillBuy());
        database.insert(TABLE_NAME, null, values);

        Log.i("DatabaseAccess", "addGame(" + game.getTitle() + ")");
    }

    public boolean gameExists(String title)
    {
        boolean found = false;

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the game from the database
        String[] columns = new String[] { "title", "releaseDate", "description", "link",
                "whenWillBuy" };
        String whereClause = columns[0] + " = ?";
        String[] whereArgs = new String[]{ title };
        Cursor cursor = database.query(TABLE_NAME, columns, whereClause, whereArgs, "", "", "");
        if (cursor.getCount() >= 1)
        {
            found = true;
        }
        cursor.close();
        Log.i("DatabaseAccess", "getGame(" + title + "):  found: " + found);

        return found;
    }

    //Query all bought games in the database
    public List<Game> getAllBoughtGames()
    {
        List<Game> games = new ArrayList<>();

        //Get a connection to the database
        SQLiteDatabase database = this.getReadableDatabase();

        String whereClause = columns[4] + " = ?";
        String[] whereArgs = new String[]{ context.getString(R.string.bought) };
        String orderBy = columns[0] + " DESC";
        Cursor cursor = database.query(TABLE_NAME, columns, whereClause, whereArgs, "", "", orderBy);
        cursor.moveToFirst();
        //Continue adding games to the list while more are found
        while (!cursor.isAfterLast())
        {
            String title = cursor.getString(0);
            String releaseDate = cursor.getString(1);
            String description = cursor.getString(2);
            String link = cursor.getString(3);
            String willBuy = (cursor.getString(4));
            Game game = new Game(title, releaseDate, description, link);
            game.setWhenWillBuy(willBuy);
            games.add(game);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("DatabaseAccess", "getAllBoughtGames():  num: " + games.size());

        return games;
    }

    //Query all removed games in the database
    public List<Game> getAllRemovedGames()
    {
        List<Game> games = new ArrayList<>();

        //Get a connection to the database
        SQLiteDatabase database = this.getReadableDatabase();

        String whereClause = columns[4] + " = ?";
        String[] whenWillBuy_values = context.getResources().getStringArray(R.array.options);
        String whereArg1 = whenWillBuy_values[whenWillBuy_values.length - 1];
        String[] whereArgs = new String[]{ whereArg1 };
        Cursor cursor = database.query(TABLE_NAME, columns, whereClause, whereArgs, "", "", "");
        cursor.moveToFirst();
        //Continue adding games to the list while more are found
        while (!cursor.isAfterLast())
        {
            String title = cursor.getString(0);
            String releaseDate = cursor.getString(1);
            String description = cursor.getString(2);
            String link = cursor.getString(3);
            String willBuy = (cursor.getString(4));
            Game game = new Game(title, releaseDate, description, link);
            game.setWhenWillBuy(willBuy);
            games.add(game);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("DatabaseAccess", "getAllRemovedGames():  num: " + games.size());

        return games;
    }

    //Query all games that may have expired in the database
    public List<Game> getAllPossiblyExpiredGames()
    {
        List<Game> games = new ArrayList<>();

        //Get a connection to the database
        SQLiteDatabase database = this.getReadableDatabase();

        String whereClause = columns[4] + " = ? OR " + columns[4] + " = ? OR " + columns[4] +
                " = ? OR " + columns[4] + " = ? OR " + columns[4] + " = ?";
        String[] whenWillBuy_values = context.getResources().getStringArray(R.array.options);
        String whereArg1 = whenWillBuy_values[0];
        String whereArg2 = whenWillBuy_values[whenWillBuy_values.length - 2];
        String whereArg3 = whenWillBuy_values[whenWillBuy_values.length - 3];
        String whereArg4 = whenWillBuy_values[whenWillBuy_values.length - 4];
        String[] whereArgs = new String[]{ whereArg1, whereArg2, whereArg3, whereArg4, "" };
        Cursor cursor = database.query(TABLE_NAME, columns, whereClause, whereArgs, "", "", "");
        cursor.moveToFirst();
        //Continue adding games to the list while more are found
        while (!cursor.isAfterLast())
        {
            String title = cursor.getString(0);
            String releaseDate = cursor.getString(1);
            String description = cursor.getString(2);
            String link = cursor.getString(3);
            String willBuy = (cursor.getString(4));
            Game game = new Game(title, releaseDate, description, link);
            game.setWhenWillBuy(willBuy);
            games.add(game);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("DatabaseAccess", "getAllPossiblyRemovedGames():  num: " + games.size());

        return games;
    }

    //Query all games in the database
    public List<Game> getAllGames()
    {
        List<Game> games = new ArrayList<>();

        //Get a connection to the database
        SQLiteDatabase database = this.getReadableDatabase();

        String whereClause = columns[4] + " != ? AND " + columns[4] + " != ?";
        String[] whenWillBuy_values = context.getResources().getStringArray(R.array.options);
        String whereArg1 = whenWillBuy_values[whenWillBuy_values.length - 1];
        String[] whereArgs = new String[]{ whereArg1, context.getString(R.string.bought) };
        String orderBy = columns[0] + " ASC";
        Cursor cursor = database.query(TABLE_NAME, columns, whereClause, whereArgs, "", "",
                orderBy);
        cursor.moveToFirst();
        //Continue adding games to the list while more are found
        while (!cursor.isAfterLast())
        {
            String title = cursor.getString(0);
            String releaseDate = cursor.getString(1);
            String description = cursor.getString(2);
            String link = cursor.getString(3);
            String willBuy = (cursor.getString(4));
            Game game = new Game(title, releaseDate, description, link);
            game.setWhenWillBuy(willBuy);
            games.add(game);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("DatabaseAccess", "getAllGames():  num: " + games.size());

        return games;
    }
    
    public boolean updateGame(Game game)
    {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // update the data in the database
        ContentValues values = new ContentValues();
        values.put(columns[0], game.getTitle());
        values.put(columns[1], game.getReleaseDate());
        values.put(columns[2], game.getDescription());
        values.put(columns[3], game.getLink());
        values.put(columns[4], game.getWhenWillBuy());

        String whereClause = columns[0] + " = ?";
        String[] whereArgs = new String[]{ game.getTitle() };
        int numRowsAffected = database.update(TABLE_NAME, values, whereClause, whereArgs);

        Log.i("DatabaseAccess", "updateGame(" + game.getTitle() + "):  numRowsAffected: " +
                numRowsAffected);

        // verify that the game was updated successfully
        return (numRowsAffected == 1);
    }

    //Delete a game from the database
    public boolean deleteGame(String title)
    {
        //Get a connection to the database
        SQLiteDatabase database = this.getWritableDatabase();

        String whereClause = columns[0] + " = ?";
        String[] whereArgs = new String[]{ title };
        //Delete the game with the given gameId from the database
        int numRowsAffected = database.delete(TABLE_NAME, whereClause, whereArgs);

        Log.i("DatabaseAccess", "deleteGame(" + title + "):  numRowsAffected: " + numRowsAffected);

        //Check if the game was deleted successfully
        return (numRowsAffected == 1);
    }
}
