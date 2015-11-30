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

//Class that serves as an adapter for the ListView that shows the new game releases
public class GameAdapter extends BaseAdapter
{
    private Context context;
    private List<Game> data;

    public GameAdapter(Context context, List<Game> data)
    {
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

        Log.d("GameAdapter", "Game:");
        Log.d("GameAdapter", "  Title:   "+ gameToDisplay.getTitle());
        Log.d("GameAdapter", "  Release Date:  "+ gameToDisplay.getReleaseDate());
        Log.d("GameAdapter", "  Content: "+ gameToDisplay.getDescription());

        if (convertView == null)
        {
            // create the layout
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_game_item, parent, false);
        }

        //Populate the views with the list of new game releases from the database
        TextView lblTitle = (TextView)convertView.findViewById(R.id.lblTitle);
        lblTitle.setText(gameToDisplay.getTitle());

        TextView lblReleaseDate = (TextView)convertView.findViewById(R.id.lblReleaseDate);
        lblReleaseDate.setText(gameToDisplay.getReleaseDate());

        TextView lblWhenWillBuy = (TextView)convertView.findViewById(R.id.lblWhenWillBuy);
        lblWhenWillBuy.setText(R.string.will_i_buy);
        //Displays the "whenWillBuy" value with a bit of text clarifying what the value means
        lblWhenWillBuy.setText(lblWhenWillBuy.getText() + "\n" + gameToDisplay.getWhenWillBuy());

        TextView lblDesc = (TextView)convertView.findViewById(R.id.lblDesc);
        lblDesc.setText(R.string.item_click_instructions);

        return convertView;
    }
}
