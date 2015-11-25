//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class GameDBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_FILENAME = "upcoming_games.db";
    public static final String TABLE_NAME = "Games";

    public static final String CREATE_STMT = "CREATE TABLE " + TABLE_NAME + "(" +
            " title text primary key, releaseDate text not null," +
            " description text, link text not null, whenWillBuy text not null)";
    public static final String DROP_STMT = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public GameDBHelper(Context context)
    {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
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
        String[] columns = new String[] { "title", "releaseDate", "description", "link", "whenWillBuy" };
        String whereClause = "title = ?";
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

    //Query all games in the database
    public ArrayList<Game> getAllGames()
    {
        ArrayList<Game> games = new ArrayList<>();

        //Get a connection to the database
        SQLiteDatabase database = this.getReadableDatabase();

        String[] columns = new String[] { "title", "releaseDate", "description", "link", "whenWillBuy" };
        Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
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
        values.put("title", game.getTitle());
        values.put("releaseDate", game.getReleaseDate());
        values.put("description", game.getDescription());
        values.put("link", game.getLink());
        values.put("whenWillBuy", game.getWhenWillBuy());

        String whereClause = "title = ?";
        String[] whereArgs = new String[]{ game.getTitle() };
        int numRowsAffected = database.update(TABLE_NAME, values, whereClause, whereArgs);

        Log.i("DatabaseAccess", "updateGame(" + game.getTitle() + "):  numRowsAffected: " + numRowsAffected);

        // verify that the game was updated successfully
        return (numRowsAffected == 1);
    }
}
