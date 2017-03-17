package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
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

    private String fileName;
    private String displayName;

    public Font(String fileName) {
        this.fileName = fileName;
        displayName = cleanUpFileName(fileName);
    }

    private String cleanUpFileName(String fileName) {
        fileName = fileName.replace("-", " ");
        fileName = fileName.replace(".ttf", "");
        fileName = fileName.replace(".otf", "");
        return fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
