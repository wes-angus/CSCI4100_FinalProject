//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BoughtGameAdapter extends BaseAdapter {
    private Context context;
    private List<Game> data;

    public BoughtGameAdapter(Context context, List<Game> data) {
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
        Game gameToDisplay = data.get(position);

        Log.d("BoughtGameAdapter", "Game: "+ gameToDisplay);

        if (convertView == null)
        {
            // create the layout
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_bought_game_item, parent, false);
        }

        //Populate the views with the data from the "new game" feed
        TextView lblTitle = (TextView)convertView.findViewById(R.id.lblBoughtTitle);
        lblTitle.setText(gameToDisplay.getTitle());

        TextView lblDesc = (TextView)convertView.findViewById(R.id.lblBoughtDesc);
        lblDesc.setText(gameToDisplay.getDescription());

        TextView lblMoreDesc = (TextView)convertView.findViewById(R.id.lblBoughtMoreDesc);
        lblMoreDesc.setText(R.string.bought_item_click_instructions);

        return convertView;
    }
}