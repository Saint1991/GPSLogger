package geologger.saints.com.geologger.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mizuno on 2015/02/13.
 */
public class HttpAsyncImageLoader extends AsyncTaskLoader<Bitmap> {

    private String mUrl;

    public HttpAsyncImageLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    public Bitmap loadInBackground() {

        Bitmap ret = null;

        try {

            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setDoInput(true);
            connection.connect();

            ret = BitmapFactory.decodeStream(connection.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;

    }
}
