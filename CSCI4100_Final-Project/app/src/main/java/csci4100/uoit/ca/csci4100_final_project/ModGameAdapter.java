//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

//TODO: Delete ModGameAdapter class, along with the xml and move the setting of willBuy for
//TODO: each individual game listing to a separate activity, started in onItemClick.
//TODO: Also have the link be opened from within this activity.
public class ModGameAdapter extends BaseAdapter
{
    private Context context;
    private List<Game> data;

    public ModGameAdapter(Context context, List<Game> data)
    {
        this.data = data;
        this.context = context;
    }

    static class ViewHolder
    {
        protected TextView titleView;
        protected TextView rDateView;
        protected TextView descView;
        protected CheckBox checkBox;
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
        final Game gameToDisplay = data.get(position);
        ViewHolder viewHolder;

        Log.d("GameAdapter", "Game:");
        Log.d("GameAdapter", "  Title:         " + gameToDisplay.getTitle());
        Log.d("GameAdapter", "  Release Date:  " + gameToDisplay.getReleaseDate());
        Log.d("GameAdapter", "  Description:   " + gameToDisplay.getDescription());

        if (convertView == null)
        {
            // create the layout (for the normal list view)
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_mod_game_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.titleView = (TextView) convertView.findViewById(R.id.lblTitle);
            viewHolder.rDateView = (TextView) convertView.findViewById(R.id.lblReleaseDate);
            viewHolder.descView = (TextView) convertView.findViewById(R.id.lblDesc);

            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.cBoxWillBuy);
            viewHolder.checkBox.setChecked(gameToDisplay.isWillBuy());
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (int) buttonView.getTag();
                    data.get(getPosition).setWillBuy(buttonView.isChecked());
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.lblTitle, viewHolder.titleView);
            convertView.setTag(R.id.cBoxWillBuy, viewHolder.checkBox);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // populate the views with the data from the feed
        viewHolder.titleView.setText(gameToDisplay.getTitle());
        viewHolder.checkBox.setTag(position);
        viewHolder.rDateView.setText(gameToDisplay.getReleaseDate());
        viewHolder.descView.setText(gameToDisplay.getDescription());

        return convertView;
    }
}