package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.bignerdranch.android.typesetter.databinding.ActivityTypesetterBinding;

import java.util.List;

import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class TypesetterActivity extends AppCompatActivity {

    private List<Font> fonts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityTypesetterBinding activityTypesetterBinding = DataBindingUtil.setContentView(this, R.layout.activity_typesetter);

        fonts = Font.listAssetFonts(this);

        activityTypesetterBinding.fontSpinner.setAdapter(new FontAdapter(this, fonts));
        activityTypesetterBinding.fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Typeface typeface = TypefaceUtils.load(getAssets(), "fonts/" + fonts.get(position).getFontName());
                activityTypesetterBinding.fillerTextView.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static class FontAdapter extends ArrayAdapter<Font> {

        public FontAdapter(@NonNull Context context, @NonNull List<Font> fonts) {
            super(context, android.R.layout.simple_list_item_1, fonts);
        }

    }
}
