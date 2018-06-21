package com.tracker.tracker.tareas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class ProfilePicture extends AsyncTask <String, Void, Bitmap>  {

    private ImageView profilePic;

    public ProfilePicture(ImageView img) {
        this.profilePic = img;
    }

    @Nullable
    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        this.profilePic.setImageBitmap(result);
    }
}
