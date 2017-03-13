package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Font {

    public static List<Font> listAssetFonts(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] fontNames;
        try {
            fontNames = assetManager.list("fonts");
        } catch (IOException e) {
            Log.e("Error", "Unable to list fonts", e);
            return new ArrayList<>();
        }

        ArrayList<Font> fonts = new ArrayList<>(fontNames.length);
        for (int i = 0; i < fontNames.length; i++) {
            fonts.add(new Font(fontNames[i]));
        }

        return fonts;
    }

    private String fontName;

    public Font(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }

    public String getDisplayName() {
        return fontName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
