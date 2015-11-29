//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BoughtGameAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> data;

    public BoughtGameAdapter(Context context, ArrayList<String> data) {
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

    public View getView(int position, View convertView, ViewGroup parent)
    {
        String gameToDisplay = data.get(position);

        Log.d("BoughtGameAdapter", "Game: "+ gameToDisplay);

        if (convertView == null)
        {
            // create the layout
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_bought_game_item, parent, false);
        }

        // populate the views with the data from story
        TextView lblTitle = (TextView)convertView.findViewById(R.id.lblBoughtGame);
        lblTitle.setText(gameToDisplay);

        return convertView;
    }
}