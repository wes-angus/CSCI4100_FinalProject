package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowFeedActivity extends Activity implements StoryDataListener {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_feed);

        //loads the internet file in an AsyncTask
        url = getIntent().getStringExtra("url");
        DownloadFeedTask task = new DownloadFeedTask(this);
        task.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_feed, menu);
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
    public void showStories(ArrayList<Story> data)
    {
        ListView listView = (ListView)findViewById(R.id.story_listView);
        populateList(listView, data);
    }

    private void populateList(ListView listView, ArrayList<Story> data)
    {
        listView.setAdapter(new StoryAdapter(this, data));
    }
}
