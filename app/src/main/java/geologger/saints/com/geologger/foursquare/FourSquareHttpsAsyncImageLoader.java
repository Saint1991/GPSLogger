package geologger.saints.com.geologger.foursquare;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Mizuno on 2015/02/13.
 */
public class FourSquareHttpsAsyncImageLoader extends AsyncTaskLoader<Bitmap> {

    private final String TAG = getClass().getSimpleName();
    private FourSquareClient mFourSquareClient;
    private String mPlaceId;
    private int mWidth;
    private int mHeight;

    public FourSquareHttpsAsyncImageLoader(Context context, String placeId, int width, int height) {
        super(context);
        this.mFourSquareClient = new FourSquareClient(context);
        this.mPlaceId = placeId;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public Bitmap loadInBackground() {

        Bitmap ret = null;

        try {

            String photoUrl = mFourSquareClient.searchPhoto(mPlaceId, mWidth, mHeight);

            URL url = new URL(photoUrl);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

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
