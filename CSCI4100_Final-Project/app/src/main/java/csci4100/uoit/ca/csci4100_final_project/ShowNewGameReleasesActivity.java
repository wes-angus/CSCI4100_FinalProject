//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Activity for displaying the list of new game releases in a ListView
public class ShowNewGameReleasesActivity extends Activity implements DatabaseListener
{
    public static final int MODIFY_GAME = 10;
    public static final String L_VIEW_STATE = "listView_prevState";
    List<Game> games = new ArrayList<>();
    ListView listView;
    private Parcelable listView_state = null;
    int scrollPos = 0;
    int gamePositionToModify = 0;
    public static final String parseDatePattern = "EEE, d MMM yyyy";
    boolean search_error_showing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new_game_releases);

        listView = (ListView) findViewById(R.id.game_listView);

        //Show the normal background of the search bar
        EditText editText = (EditText) findViewById(R.id.searchField);
        editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);

        //Gets the list of games from the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute(LoadDatabaseInfoTask.SYNC_ENUM.GET_GAMES_LIST);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState)
    {
        //Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        //Save the current list view state
        listView_state = listView.onSaveInstanceState();
        savedInstanceState.putParcelable(L_VIEW_STATE, listView_state);
        //Restores the search bar background colour state (whether it showed an error last or not)
        savedInstanceState.putBoolean("search_error_showing", search_error_showing);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        //Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        //Restores the previous list view state
        listView_state = savedInstanceState.getParcelable(L_VIEW_STATE);
        /*
        Restores the search bar background colour state and sets the search bar's
        background colour to make sure it's maintained when rotating the phone
        */
        search_error_showing = savedInstanceState.getBoolean("search_error_showing", false);
        if(search_error_showing)
        {
            //Show the errored background of the search bar
            EditText editText = (EditText) findViewById(R.id.searchField);
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        else
        {
            //Show the normal background of the search bar
            EditText editText = (EditText) findViewById(R.id.searchField);
            editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume(); //Always call the superclass method first

        //Restores the previous list view state
        if(listView_state != null)
        {
            listView.onRestoreInstanceState(listView_state);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_new_game_releases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void syncGames(final List<Game> games, short option)
    {
        if(option == LoadDatabaseInfoTask.SYNC_ENUM.GET_GAMES_LIST
                || option == LoadDatabaseInfoTask.SYNC_ENUM.UPDATE_GAME_SINGLE
                || option == LoadDatabaseInfoTask.SYNC_ENUM.REMOVE_AND_SHOW)
        {
            if(!games.isEmpty())
            {
                this.games = games;

                if(option == LoadDatabaseInfoTask.SYNC_ENUM.GET_GAMES_LIST)
                {
                    //Gets the list of games that may have expired from the database
                    LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this,
                            getApplicationContext());
                    task.execute(LoadDatabaseInfoTask.SYNC_ENUM.GET_EXPIRED_GAMES);
                }
                else
                {
                    showGameList();
                }
            }

            if (option == LoadDatabaseInfoTask.SYNC_ENUM.UPDATE_GAME_SINGLE)
            {
                //Set the ListView to show the updated item after updating
                listView.setSelection(scrollPos);
                Toast.makeText(this, R.string.mod_success, Toast.LENGTH_SHORT).show();
            }
        }
        else if(option == LoadDatabaseInfoTask.SYNC_ENUM.GET_EXPIRED_GAMES)
        {
            List<Game> expiredGames = getExpiredGames(games);

            if(!expiredGames.isEmpty())
            {
                //Removes the expired games from the database
                LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
                task.execute(LoadDatabaseInfoTask.SYNC_ENUM.REMOVE_AND_SHOW, expiredGames);
            }
            else
            {
                showGameList();
            }
        }
    }

    //Displays the games received from the database in the list view
    private void showGameList()
    {
        /*
        Avoids going out of bounds when the number of items from the query
        decreases after the database is updated (when the "whenWillBuy"
        value is changed to "Will Never Buy It" or "Bought"
        */
        if(scrollPos > (games.size() - 1))
        {
            scrollPos = games.size() - 1;
        }

        listView = (ListView) findViewById(R.id.game_listView);
        listView.setAdapter(new GameAdapter(this, games));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                goToModifyScreen(position);
            }
        });

        //Restores the previous list view state (if the content hasn't changed)
        if(listView_state != null)
        {
            listView.onRestoreInstanceState(listView_state);
        }
    }

    private List<Game> getExpiredGames(List<Game> possiblyExpiredGames)
    {
        List<Game> expiredGames = new ArrayList<>();
        /*
        Checks the date for each game in the list of games given to it (the games that
        the user is unlikely to buy), and if the date is more than a week past, the
        game is added to a list of games that will be removed from the database
        */
        for (Game game : possiblyExpiredGames)
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat(parseDatePattern, Locale.US);
            Date curDate = calendar.getTime();
            try
            {
                Date date = dateFormat.parse(game.getReleaseDate());
                long diffTime = curDate.getTime() - date.getTime();
                long diffDays = (long) ((double) diffTime / (double) (1000 * 60 * 60 * 24));
                if(diffDays > 7)
                {
                    expiredGames.add(game);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return expiredGames;
    }

    /*
    Parcels-up the game and sends it to the GameDetailAndModify activity,
    which starts when you select a game from the list view
    */
    public void goToModifyScreen(int gamePosition)
    {
        Intent intent = new Intent(this, GameDetailAndModifyActivity.class);
        Bundle gamesBundle = new Bundle();
        gamesBundle.putParcelable("game", games.get(gamePosition));
        intent.putExtras(gamesBundle);
        gamePositionToModify = gamePosition;
        MainMenuActivity.playSound(MainMenuActivity.itemClickSound_ID);
        startActivityForResult(intent, MODIFY_GAME);
    }

    public void backToMenu(View view)
    {
        AboutActivity.backToPrevActivity(this);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result)
    {
        //Gets a result back from GameDetailAndModifyActivity
        super.onActivityResult(reqCode, resCode, result);

        if(resCode == Activity.RESULT_OK && reqCode == MODIFY_GAME)
        {
            Game oldGame = games.get(gamePositionToModify);
            /*
            Updates the "willBuy" property of the game that was selected in the database
            and gets the updated list of games to update the ListView.
            */
            String new_whenWillBuy = result.getStringExtra("when_will_buy");
            if(!oldGame.getWhenWillBuy().equals(new_whenWillBuy))
            {
                oldGame.setWhenWillBuy(new_whenWillBuy);
                scrollPos = gamePositionToModify;
                LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this,
                        getApplicationContext());
                task.execute(LoadDatabaseInfoTask.SYNC_ENUM.UPDATE_GAME_SINGLE, oldGame);
            }
        }

    }

    //Searches for games in a ListView
    public static boolean searchGameList(List<Game> games, Activity activity, ListView listView,
                                         int searchFieldID)
    {
        EditText editText = (EditText) activity.findViewById(searchFieldID);
        String searchText = editText.getText().toString().toLowerCase();
        String[] searchTerms = searchText.split(" ");
        int found_pos = 0;
        boolean found = false;

        if(games != null)
            for (int i = 0; i < games.size(); i++)
            {
                //Initialize values for the current game
                found = true;
                String title = games.get(i).getTitle().toLowerCase();

                // if all terms are found in the title
                for( String term : searchTerms ) {
                    found = found && title.contains(term);
                }

                if (found)
                {
                    found_pos = i;
                    break;
                }
            }

        boolean search_error_showing = true;
        if(found)
        {
            //Show the normal background of the search bar and plays a "search"-type sound
            search_error_showing = false;
            editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
            MainMenuActivity.playSound(MainMenuActivity.buttonSound2_ID);
            listView.setSelection(found_pos);
        }
        else
        {
            //Show the "errored" background of the search bar and plays an "error"-type sound
            MainMenuActivity.playSound(MainMenuActivity.cancelSound_ID);
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        return search_error_showing;
    }

    public void search(View view)
    {
        /*
        Runs the method for searching the game list and
        sets the background colour state of the search bar
        */
        search_error_showing = searchGameList(games, this, listView, R.id.searchField);
    }
}
