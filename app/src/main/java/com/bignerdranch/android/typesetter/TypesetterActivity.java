package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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


public class TypesetterActivity extends AppCompatActivity {

    private List<Font> fonts;
    private ActivityTypesetterBinding activityTypesetterBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTypesetterBinding = DataBindingUtil.setContentView(this, R.layout.activity_typesetter);

        if (savedInstanceState == null) {
            // Text view sets the text size using an int, so it looses SP precision and would
            // display as 14.1 because it rounded the value on construction
            activityTypesetterBinding.fillerTextView.setTextSize(24);
        }

        activityTypesetterBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getBitmapFromView(activityTypesetterBinding.constraint);
                File dir = getFilesDir();
                File file = new File(dir, "test.png");
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri uri = FileProvider.getUriForFile(TypesetterActivity.this,
                        "com.bignerdranch.android.typesetter.fileprovider",
                        file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, uri);

                startActivity(intent);

            }
        });

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

        activityTypesetterBinding.renderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextSize();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    updateLetterSpacing();
                }
                updateLineSpacing();
            }
        });

        updateValues();
    }

    public Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void updateValues() {
        float textSize = activityTypesetterBinding.fillerTextView.getTextSize();
        textSize = textSize / getResources().getDisplayMetrics().scaledDensity;
        activityTypesetterBinding.fontSizeEditText.setText(formatFloat(textSize));

        float letterSpacing = activityTypesetterBinding.fillerTextView.getLetterSpacing();
        if (letterSpacing == 0) {
            activityTypesetterBinding.letterSpacingEditText.setText("0.00");
        } else {
            activityTypesetterBinding.letterSpacingEditText.setText(formatFloat(letterSpacing));
        }

        float lineSpacing = activityTypesetterBinding.fillerTextView.getLineSpacingExtra();
        lineSpacing = lineSpacing / getResources().getDisplayMetrics().scaledDensity;
        activityTypesetterBinding.lineSpacingEditText.setText(formatFloat(lineSpacing));
    }

    public static String formatFloat(float floatValue) {
        if(floatValue == (int) floatValue)
            return String.format("%d", (int) floatValue);
        else
            return String.format("%s", floatValue);
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
        float lineSpacingSp = Float.parseFloat(lineSpacing);
        float lineSpacingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lineSpacingSp, getResources().getDisplayMetrics());
        float multiplier = activityTypesetterBinding.fillerTextView.getLineSpacingMultiplier();
        activityTypesetterBinding.fillerTextView.setLineSpacing(lineSpacingPx, multiplier);
    }

    private static class FontAdapter extends ArrayAdapter<Font> {

        public FontAdapter(@NonNull Context context, @NonNull List<Font> fonts) {
            super(context, android.R.layout.simple_list_item_1, fonts);
        }

    }
}
