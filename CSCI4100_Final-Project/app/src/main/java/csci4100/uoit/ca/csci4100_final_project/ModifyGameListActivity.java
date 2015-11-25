//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.List;

import csci4100.uoit.ca.csci4100_final_project.R;

//TODO: Fix checkbox selection issue and pass data back to previous activity to update the database
public class ModifyGameListActivity extends Activity
{
    List<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_game_list);

        Bundle bundle = getIntent().getExtras();
        games = bundle.getParcelableArrayList("newGameList");
        showGameList(games);
    }

    private void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new GameAdapter(this, data, true));
    }

    public void showGameList(List<Game> data)
    {
        ListView listView = (ListView) findViewById(R.id.mod_game_listView);
        populateList(listView, data);
    }

    public void saveWillBuyProperty(View view)
    {
        Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
        ListView listView = (ListView) findViewById(R.id.mod_game_listView);
        //intent.putExtra("will_buy_array", willBuyProperties);
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void cancelModify(View view)
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
