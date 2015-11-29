//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class ViewBoughtGameListActivity extends Activity implements DatabaseListener
{
    ListView listView;
    private Parcelable listView_state = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bought_game_list);

        listView = (ListView) findViewById(R.id.boughtGames_listView);

        //Gets the list of bought games from the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute((short) 4);
    }

    private void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new BoughtGameAdapter(this, data));
    }

    public void backToMenu(View view)
    {
        AboutActivity.backToPrevActivity(this);
    }

    @Override
    public void syncGames(List<Game> games, short option)
    {
        populateList(listView, games);
    }
}
