//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//TODO: Get list of unlikely to buy games whose release dates are a week past
public class ShowNewGameReleasesActivity extends Activity implements DatabaseListener
{
    public static final int MODIFY_GAME = 10;
    public static final String L_VIEW_STATE = "listView_prevState";
    List<Game> games = new ArrayList<>();
    ListView listView;
    private Parcelable listView_state = null;
    int scrollPos = 0;
    int gamePositionToModify = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new_game_releases);

        listView = (ListView) findViewById(R.id.game_listView);

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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore activity state from saved instance
        listView_state = savedInstanceState.getParcelable(L_VIEW_STATE);
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
        if(option > 0 && option < 4)
        {
            if(!games.isEmpty())
            {
                //TODO: Remove games that've expired from the database
                this.games = games;
                //getExpiredGames();

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
                populateList(listView, games);

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

            if (option == 2)
            {
                listView.setSelection(scrollPos);
                Toast.makeText(this, R.string.mod_success, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new GameAdapter(this, data));
    }

    private List<Game> getExpiredGames()
    {
        List<Game> expiredGames = new ArrayList<>();
        /*
        Checks the date for each game in the list of games given to it (the games that
        the user is unlikely to buy), and if the date is more than a week past, the
        game is added to a list of games that will be removed from the database
        */
        for (Game game : games)
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
            Date curDate = calendar.getTime();
            try
            {
                Date date = dateFormat.parse(game.getReleaseDate());
                long diffTime = curDate.getTime() - date.getTime();
                long diffDays = (long) ((double) diffTime / (double)(1000 * 60 * 60 * 24));
                if(diffDays > 8)
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
                    LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
                    task.execute((short) 2, oldGame);
                }
            }
        }
    }
}
