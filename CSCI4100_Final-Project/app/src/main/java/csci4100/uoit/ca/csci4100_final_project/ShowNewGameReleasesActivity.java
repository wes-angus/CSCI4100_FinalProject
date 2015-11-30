//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
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

        EditText editText = (EditText) findViewById(R.id.searchField);
        editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);

        //Gets the list of games from the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute((short) 1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the current list view position
        listView_state = listView.onSaveInstanceState();
        savedInstanceState.putParcelable(L_VIEW_STATE, listView_state);
        savedInstanceState.putBoolean("search_error_showing", search_error_showing);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore activity state from saved instance
        listView_state = savedInstanceState.getParcelable(L_VIEW_STATE);
        search_error_showing = savedInstanceState.getBoolean("search_error_showing", false);
        if(search_error_showing)
        {
            EditText editText = (EditText) findViewById(R.id.searchField);
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        else
        {
            EditText editText = (EditText) findViewById(R.id.searchField);
            editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume(); // Always call the superclass method first

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
        if(option == 1 || option == 2 || option == 7)
        {
            if(!games.isEmpty())
            {
                this.games = games;

                if(option == 1)
                {
                    //Gets the list of games that may have expired from the database
                    LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this,
                            getApplicationContext());
                    task.execute((short) 6);
                }
                else
                {
                    showGameList();
                }
            }

            if (option == 2)
            {
                listView.setSelection(scrollPos);
                Toast.makeText(this, R.string.mod_success, Toast.LENGTH_SHORT).show();
            }
        }
        else if(option == 6)
        {
            List<Game> expiredGames = getExpiredGames(games);

            if(!expiredGames.isEmpty())
            {
                //Removes the expired games from the database
                LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
                task.execute((short) 7, expiredGames);
            }
            else
            {
                showGameList();
            }
        }
    }

    private void showGameList()
    {
        /*
        Avoid going out of bounds when the number of items from the query
        decreases after the database is updated (when the "whenWillBuy"
        value is changed to "Will Never Buy It" or "Bought"
        */
        if(scrollPos > (games.size() - 1))
        {
            scrollPos = games.size() - 1;
        }

        listView = (ListView) findViewById(R.id.game_listView);
        listView.setAdapter(new GameAdapter(this, games));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                goToModifyScreen(position);
            }
        });

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
        if (resCode == Activity.RESULT_OK)
        {
            if(reqCode == MODIFY_GAME)
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
                    task.execute((short) 2, oldGame);
                }
            }
        }
    }

    public static boolean searchGameList(List<Game> games, Activity activity, ListView listView,
                                         int textFieldID)
    {
        EditText editText = (EditText) activity.findViewById(textFieldID);
        String searchText = editText.getText().toString().toLowerCase();
        String[] searchTerms = searchText.split(" ");
        boolean[] foundTerms = new boolean[searchTerms.length];
        int found_pos = 0;
        boolean found = true;
        for (int i = 0; i < foundTerms.length; i++)
        {
            foundTerms[i] = false;
        }

        gameLoop:
        for (int i = 0; i < games.size(); i++)
        {
            //Initialize values for the current game
            found = true;
            for (int index = 0; index < foundTerms.length; index++)
            {
                foundTerms[index] = false;
            }
            String title = games.get(i).getTitle().toLowerCase();
            for (int j = 0; j < searchTerms.length; j++)
            {
                //Check if the individual search term is contained in the ti
                if(title.contains(searchTerms[j]))
                {
                    foundTerms[j] = true;
                    if(j == searchTerms.length - 1)
                    {
                        for (boolean foundTerm : foundTerms)
                        {
                            if(!foundTerm)
                            {
                                //If one of the search terms doesn't
                                //match, then the item wasn't found
                                found = false;
                                break;
                            }
                        }
                        //If all search terms match...
                        if(found)
                        {
                            //...exit the game loop and set the position to move the ListView to
                             found_pos = i;
                            break gameLoop;
                        }
                    }
                }
                if(i == games.size() - 1 && j == searchTerms.length - 1)
                {
                    found = false;
                }
            }
        }

        boolean search_error_showing;
        if(found)
        {
            search_error_showing = false;
            editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
            MainMenuActivity.playSound(MainMenuActivity.buttonSound2_ID);
            listView.setSelection(found_pos);
        }
        else
        {
            search_error_showing = true;
            MainMenuActivity.playSound(MainMenuActivity.cancelSound_ID);
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        return search_error_showing;
    }

    public void search(View view)
    {
        search_error_showing = searchGameList(games, this, listView, R.id.searchField);
    }
}
