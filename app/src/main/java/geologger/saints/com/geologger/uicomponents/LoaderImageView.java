package geologger.saints.com.geologger.uicomponents;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.androidannotations.annotations.EView;

import geologger.saints.com.geologger.utils.HttpAsyncImageLoader;

/**
 * Created by Mizuno on 2015/02/13.
 */
@EView
public class LoaderImageView extends ImageView implements LoaderManager.LoaderCallbacks<Bitmap> {

    protected String mUrl;

    public LoaderImageView(Context context) {
        super(context);
    }

    public LoaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoaderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        return new HttpAsyncImageLoader(getContext(), this.mUrl);
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap image) {
        setImageBitmap(image);
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {}

}
