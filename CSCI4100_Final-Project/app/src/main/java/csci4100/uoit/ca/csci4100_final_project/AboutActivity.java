//Authors: Wesley Angus & Montgomery Alban

package csci4100.uoit.ca.csci4100_final_project;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class AboutActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //setTheme(android.R.style.Theme_DeviceDefault_Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
                AboutBuilder aboutBuilder = new AboutBuilder();
        aboutBuilder.addSoundRefs(R.array.sounds_reference_arrays,R.integer.sound_ref_count);
        CharSequence text = aboutBuilder.build();

        TextView textView = (TextView) findViewById(R.id.about_tView);
        textView.setText(text, TextView.BufferType.SPANNABLE);
    }

    public void backToMenu(View view)
    {
        MainMenuActivity.playSound(MainMenuActivity.buttonSound1_ID);
        finish();
    }

    // class for building the about
    private class AboutBuilder {
        StringBuilder sounds = new StringBuilder();
        public AboutBuilder() {
            String base = getString(R.string.about_txt);

            sounds.append(String.format(base, getString(R.string.reference_feed_site),
                    getString(R.string.reference_feed_url)));
        }


        //
        @SuppressLint("StringFormatMatches")
        public AboutBuilder addSoundRefs(int soundsRefArray, int soundRefCountResource) {
            Resources resources = getResources();
            String[] soundArr = resources.getStringArray(soundsRefArray);
            int countRef = resources.getInteger(soundRefCountResource);

            for (int i = 0; i < soundArr.length; i+=countRef) {
                String soundRefStr = resources.getString(R.string.sound_reference);
                String[] args = Arrays.copyOfRange(soundArr, i, i + countRef);

                // Apply the formatting
                // Ignore this warning, it is not an error
                String compSoundRef = String.format(soundRefStr,args);
                sounds.append(compSoundRef);
            }
            return this;
        }

        public CharSequence build() {
            return Html.fromHtml(sounds.toString());
        }
    }
}
