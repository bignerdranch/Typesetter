package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.bignerdranch.android.typesetter.databinding.ActivityTypesetterBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import uk.co.chrisjenx.calligraphy.TypefaceUtils;

import static com.bignerdranch.android.typesetter.AndroidUtils.IS_LOLLIPOP_AND_ABOVE;
import static com.bignerdranch.android.typesetter.AndroidUtils.formatFloat;


public class TypesetterActivity extends AppCompatActivity {
    private static final String TAG = "TypesetterActivity";

    private List<Font> fonts;
    private ActivityTypesetterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_typesetter);

        if (savedInstanceState == null) {
            // Text view sets the text size using an int, so it looses SP precision and would
            // display as 24.xx because it rounded the value on construction
            binding.fillerTextView.setTextSize(24);
        }

        fonts = Font.listAssetFonts(this);

        binding.fontSpinner.setAdapter(new FontAdapter(this, fonts));
        binding.fontSpinner.setOnItemSelectedListener(onItemSelectedListener);
        binding.renderButton.setOnClickListener(v -> {
            renderValues();
            clearInputFocus();
        });
        binding.floatingActionButton.setOnClickListener(v -> {
            renderValues();
            clearInputFocus();
            shareScreenshot();
        });

        initializeEditTextValues();
        renderValues();
    }

    private void initializeEditTextValues() {
        initializeTextSize();
        initializeLetterSpacing();
        initializeLineSpacing();
    }

    private void initializeTextSize() {
        float textSize = binding.fillerTextView.getTextSize();
        textSize = textSize / getResources().getDisplayMetrics().scaledDensity;
        binding.fontSizeEditText.setText(formatFloat(textSize));
    }

    private void initializeLetterSpacing() {
        if (IS_LOLLIPOP_AND_ABOVE) {
            binding.letterSpacingTextInputLayout.setEnabled(true);
            binding.letterSpacingEditText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            float letterSpacing = binding.fillerTextView.getLetterSpacing();
            if (letterSpacing == 0) {
                binding.letterSpacingEditText.setText("0.00");
            } else {
                binding.letterSpacingEditText.setText(formatFloat(letterSpacing));
            }
        } else {
            binding.letterSpacingTextInputLayout.setEnabled(false);
            binding.letterSpacingEditText.setTextColor(ContextCompat.getColor(this, R.color.light_grey));
        }
    }

    private void initializeLineSpacing() {
        float lineSpacing = binding.fillerTextView.getLineSpacingExtra();
        lineSpacing = lineSpacing / getResources().getDisplayMetrics().scaledDensity;
        binding.lineSpacingEditText.setText(formatFloat(lineSpacing));
    }

    private void renderValues() {
        applyTextSize();
        if (IS_LOLLIPOP_AND_ABOVE) {
            applyLetterSpacing();
        }
        applyLineSpacing();
    }

    private void applyTextSize() {
        String size = binding.fontSizeEditText.getText().toString();
        float sizeSp = Float.parseFloat(size);
        if (sizeSp <= 0) {
            binding.fontSizeTextInputLayout.setErrorEnabled(true);
            binding.fontSizeTextInputLayout.setError("Nah");
        } else {
            binding.fontSizeTextInputLayout.setErrorEnabled(false);
        }
        binding.fillerTextView.setTextSize(sizeSp);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void applyLetterSpacing() {
        String letterSpacing = binding.letterSpacingEditText.getText().toString();
        float letterEms = Float.parseFloat(letterSpacing);
        binding.fillerTextView.setLetterSpacing(letterEms);
    }

    private void applyLineSpacing() {
        String lineSpacing = binding.lineSpacingEditText.getText().toString();
        float lineSpacingSp = Float.parseFloat(lineSpacing);
        float lineSpacingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lineSpacingSp, getResources().getDisplayMetrics());
        float multiplier = binding.fillerTextView.getLineSpacingMultiplier();
        binding.fillerTextView.setLineSpacing(lineSpacingPx, multiplier);
    }

    private void shareScreenshot() {
        Bitmap bitmap = BitmapUtils.getBitmapFromView(binding.constraint);
        File dir = getFilesDir();
        File file = new File(dir, "font-screenshot.png");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save screenshot");
            Snackbar.make(binding.coord, "Failed to save screenshot", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(TypesetterActivity.this,
                "com.bignerdranch.android.typesetter.fileprovider",
                file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(intent);
    }

    private void clearInputFocus() {
        AndroidUtils.hideKeyboard(this);
        binding.coord.requestFocus();
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Typeface typeface = TypefaceUtils.load(getAssets(), "fonts/" + fonts.get(position).getFileName());
            binding.fillerTextView.setTypeface(typeface);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private static class FontAdapter extends ArrayAdapter<Font> {

        public FontAdapter(@NonNull Context context, @NonNull List<Font> fonts) {
            super(context, R.layout.closed_textview, fonts);
            setDropDownViewResource(R.layout.dropdown_textview);
        }
    }
}
