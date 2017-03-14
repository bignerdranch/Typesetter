package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.bignerdranch.android.typesetter.databinding.ActivityTypesetterBinding;

import java.util.List;

import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class TypesetterActivity extends AppCompatActivity {

    private List<Font> fonts;
    private ActivityTypesetterBinding activityTypesetterBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTypesetterBinding = DataBindingUtil.setContentView(this, R.layout.activity_typesetter);

        fonts = Font.listAssetFonts(this);

        activityTypesetterBinding.fontSpinner.setAdapter(new FontAdapter(this, fonts));
        activityTypesetterBinding.fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Typeface typeface = TypefaceUtils.load(getAssets(), "fonts/" + fonts.get(position).getFileName());
                activityTypesetterBinding.fillerTextView.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityTypesetterBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextSize();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    updateLetterSpacing();
                }
                updateLineSpacing();
            }
        });
    }

    private void updateTextSize() {
        String size = activityTypesetterBinding.fontSizeEditText.getText().toString();
        float sizeSp = Float.parseFloat(size);
        if (sizeSp <= 0) {
            activityTypesetterBinding.fontSizeTextInputLayout.setErrorEnabled(true);
            activityTypesetterBinding.fontSizeTextInputLayout.setError("Nah");
        } else {
            activityTypesetterBinding.fontSizeTextInputLayout.setErrorEnabled(false);
        }
        activityTypesetterBinding.fillerTextView.setTextSize(sizeSp);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateLetterSpacing() {
        String letterSpacing = activityTypesetterBinding.letterSpacingEditText.getText().toString();
        float letterEms = Float.parseFloat(letterSpacing);
        activityTypesetterBinding.fillerTextView.setLetterSpacing(letterEms);
    }

    private void updateLineSpacing() {
        String lineSpacing = activityTypesetterBinding.lineSpacingEditText.getText().toString();
        float lineSpacingsp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(lineSpacing), getResources().getDisplayMetrics());
        activityTypesetterBinding.fillerTextView.setLineSpacing(lineSpacingsp ,activityTypesetterBinding.fillerTextView.getLineSpacingMultiplier());
    }

    private static class FontAdapter extends ArrayAdapter<Font> {

        public FontAdapter(@NonNull Context context, @NonNull List<Font> fonts) {
            super(context, android.R.layout.simple_list_item_1, fonts);
        }

    }
}
