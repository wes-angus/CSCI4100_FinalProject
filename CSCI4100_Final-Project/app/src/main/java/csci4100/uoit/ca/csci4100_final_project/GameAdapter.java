//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class GameAdapter extends BaseAdapter
{
    private Context context;
    private List<Game> data;
    private boolean modList;

    public GameAdapter(Context context, List<Game> data, boolean modList)
    {
        this.data = data;
        this.context = context;
        this.modList = modList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Game gameToDisplay = data.get(position);

        Log.d("GameAdapter", "Game:");
        Log.d("GameAdapter", "  Title:         " + gameToDisplay.getTitle());
        Log.d("GameAdapter", "  Release Date:  " + gameToDisplay.getReleaseDate());
        Log.d("GameAdapter", "  Description:   " + gameToDisplay.getDescription());

        if (convertView == null)
        {
            if(modList)
            {
                // create the layout (for the normal list view)
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_mod_game_item, parent, false);
            }
            else
            {
                // create the layout (for the "modify" list view)
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_game_item, parent, false);
            }
        }

        // populate the views with the data from the feed
        TextView lblTitle = (TextView)convertView.findViewById(R.id.lblTitle);
        lblTitle.setText(gameToDisplay.getTitle());

        TextView lblReleaseDate = (TextView)convertView.findViewById(R.id.lblReleaseDate);
        lblReleaseDate.setText(gameToDisplay.getReleaseDate());

        TextView lblDesc = (TextView)convertView.findViewById(R.id.lblDesc);
        lblDesc.setText(gameToDisplay.getDescription());

        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.cBoxWillBuy);
        checkBox.setChecked(gameToDisplay.isWillBuy());

        return convertView;
    }
}

