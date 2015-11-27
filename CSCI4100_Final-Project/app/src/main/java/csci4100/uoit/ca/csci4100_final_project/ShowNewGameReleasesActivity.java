//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowNewGameReleasesActivity extends Activity implements DatabaseListener
{
    public static final int MODIFY_GAME = 10;
    public static final String L_VIEW_STATE = "listView_prevState";
    ArrayList<Game> games = new ArrayList<>();
    ListView listView;
    private Parcelable listView_state = null;
    int scrollPos = 0;

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
    public void syncGames(final ArrayList<Game> games, short option)
    {
        if(option == 1 || option == 2)
        {
            if(!(games.isEmpty()))
            {
                listView = (ListView) findViewById(R.id.game_listView);
                populateList(listView, games);

                this.games = games;

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

            if (option == 2) {
                listView.setSelection(scrollPos);
                Toast.makeText(this, R.string.mod_success, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new GameAdapter(this, data));
    }

    public void goToModifyScreen(int gamePosition)
    {
        Intent intent = new Intent(this, GameDetailAndModifyActivity.class);
        Bundle gamesBundle = new Bundle();
        gamesBundle.putParcelable("game", games.get(gamePosition));
        intent.putExtras(gamesBundle);
        intent.putExtra("game_position", gamePosition);
        MainMenuActivity.soundPool.play(MainMenuActivity.itemClickSound_ID, 1, 1, 4, 0, 1);
        startActivityForResult(intent, MODIFY_GAME);
    }

    public void backToMenu(View view)
    {
        MainMenuActivity.soundPool.play(MainMenuActivity.buttonSound1_ID, 1, 1, 0, 0, 1);
        finish();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result)
    {
        //Gets a result back from the AskQuestion activity
        super.onActivityResult(reqCode, resCode, result);
        if (resCode == Activity.RESULT_OK)
        {
            if(reqCode == MODIFY_GAME)
            {
                /*
                Updates the "willBuy" property of the game that was selected in the database
                and gets the updated list of games to update the ListView.
                */
                int gamePosition = result.getIntExtra("game_position", 0);
                Game receivedGame = games.get(gamePosition);
                String new_whenWillBuy = result.getStringExtra("when_will_buy");
                if(!receivedGame.getWhenWillBuy().equals(new_whenWillBuy))
                {
                    receivedGame.setWhenWillBuy(new_whenWillBuy);
                    scrollPos = gamePosition;
                    LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
                    task.execute((short) 2, receivedGame);
                }
            }
        }
    }
}
