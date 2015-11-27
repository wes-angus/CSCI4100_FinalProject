package csci4100.uoit.ca.csci4100_final_project;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        String str = getString(R.string.about_txt);
//        int i_index = str.indexOf("\n");
//        int b_index1_start = str.indexOf(":") + 1;
//        int b_index1_end = str.indexOf("\n", b_index1_start);
//        int b_index2 = str.indexOf(":", b_index1_end) + 1;
//        str = str.replace("*", "");
//        SpannableString text = new SpannableString(str);
//        text.setSpan(new StyleSpan(Typeface.ITALIC), 0, i_index, 0);
//        text.setSpan(new StyleSpan(Typeface.BOLD), b_index1_start, b_index1_end, 0);
//        text.setSpan(new StyleSpan(Typeface.BOLD), b_index2, text.length(), 0);
        AboutBuilder aboutBuilder = new AboutBuilder();
        aboutBuilder.addSoundRefs(R.array.sounds_reference_arrays,R.integer.sound_ref_count);
        CharSequence text = aboutBuilder.build();
        TextView textView = (TextView) findViewById(R.id.about_tView);
        textView.setText(text, TextView.BufferType.SPANNABLE);
    }

    public void backToMenu(View view)
    {
        MainMenuActivity.soundPool.play(MainMenuActivity.sound1_ID, 1, 1, 0, 0, 1);
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
