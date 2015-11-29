//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewBoughtGameListActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bought_game_list);

        ListView l_view = (ListView) findViewById(R.id.boughtGames_listView);
        populateList(l_view, MainMenuActivity.getBoughtGameList());
    }

    private void populateList(ListView listView, ArrayList<String> data)
    {
        listView.setAdapter(new BoughtGameAdapter(this, data));
    }

    public void backToMenu(View view)
    {
        AboutActivity.backToPrevActivity(this);
    }
}
