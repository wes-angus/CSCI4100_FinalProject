//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

//Activity for displaying the list of bought games in a ListView
public class ViewBoughtGameListActivity extends Activity implements DatabaseListener
{
    ListView listView;
    List<Game> games;
    private Parcelable listView_state = null;
    public static final String B_L_VIEW_STATE = "bought_listView_prevState";
    boolean search_error_showing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bought_game_list);

        listView = (ListView) findViewById(R.id.boughtGames_listView);

        //Show the normal background of the search bar
        EditText editText = (EditText) findViewById(R.id.bought_searchField);
        editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);

        //Gets the list of bought games from the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute(LoadDatabaseInfoTask.SYNC_ENUM.GET_BOUGHT_GAMES);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState)
    {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the current list view state
        listView_state = listView.onSaveInstanceState();
        savedInstanceState.putParcelable(B_L_VIEW_STATE, listView_state);
        savedInstanceState.putBoolean("bought_search_error_showing", search_error_showing);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        //Restores the previous list view state
        listView_state = savedInstanceState.getParcelable(B_L_VIEW_STATE);
        /*
        Restores the search bar background colour state and sets the search bar's
        background colour to make sure it's maintained when rotating the phone
        */
        search_error_showing = savedInstanceState.getBoolean("bought_search_error_showing", false);
        if(search_error_showing)
        {
            //Show the "errored" background of the search bar
            EditText editText = (EditText) findViewById(R.id.bought_searchField);
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        else
        {
            //Show the normal background of the search bar
            EditText editText = (EditText) findViewById(R.id.bought_searchField);
            editText.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume(); // Always call the superclass method first

        //Restores the previous list view state
        if(listView_state != null)
        {
            listView.onRestoreInstanceState(listView_state);
        }
    }

    //Displays the bought games received from the database in the list view
    private void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new BoughtGameAdapter(this, data));
    }

    public void backToMenu(View view)
    {
        AboutActivity.backToPrevActivity(this);
    }

    @Override
    public void syncGames(final List<Game> games, short option)
    {
        if(!games.isEmpty())
        {
            this.games = games;

            listView = (ListView) findViewById(R.id.boughtGames_listView);
            populateList(listView, games);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
                {
                    GameDetailAndModifyActivity.startGameLinkActivity(games.get(position),
                            ViewBoughtGameListActivity.this);
                }
            });

            if(listView_state != null)
            {
                listView.onRestoreInstanceState(listView_state);
            }
        }
    }

    public void search(View view)
    {
        search_error_showing = ShowNewGameReleasesActivity.searchGameList(games, this, listView,
                R.id.bought_searchField);
    }
}
