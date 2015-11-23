package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 100449718 on 10/28/2015.
 */
public class StoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Story> data;

    public StoryAdapter(Context context, ArrayList<Story> data) {
        this.data = data;
        this.context = context;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Story storyToDisplay = data.get(position);

        Log.d("StoryAdapter", "Story:");
        Log.d("StoryAdapter", "  Title:   "+ storyToDisplay.getTitle());
        Log.d("StoryAdapter", "  Author:  "+storyToDisplay.getAuthor());
        Log.d("StoryAdapter", "  Content: "+storyToDisplay.getContent());

        if (convertView == null) {
            // create the layout
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_story_item, parent, false);
        }

        // populate the views with the data from story
        TextView lblTitle = (TextView)convertView.findViewById(R.id.lblTitle);
        lblTitle.setText(storyToDisplay.getTitle());

        TextView lblAuthor = (TextView)convertView.findViewById(R.id.lblAuthor);
        lblAuthor.setText(storyToDisplay.getAuthor());

        TextView lblContent = (TextView)convertView.findViewById(R.id.lblContent);
        lblContent.setText(storyToDisplay.getContent());

        return convertView;
    }
}

