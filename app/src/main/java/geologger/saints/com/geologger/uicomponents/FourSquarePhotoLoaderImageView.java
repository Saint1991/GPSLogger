package geologger.saints.com.geologger.uicomponents;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import org.androidannotations.annotations.EView;

import geologger.saints.com.geologger.foursquare.FourSquareHttpsAsyncImageLoader;
import geologger.saints.com.geologger.models.CheckinEntry;

/**
 * Created by Mizuno on 2015/02/13.
 */
@EView
public class FourSquarePhotoLoaderImageView extends LoaderImageView {

    public static final String ACTION = "InfoWindowPhotoLoaded";

    private String mPlaceId;
    private int mWidth = 80;
    private int mHeight = 80;
    private final static String ANDROID = "android";


    public FourSquarePhotoLoaderImageView(Context context) {
        super(context);
    }

    public FourSquarePhotoLoaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWidth = attrs.getAttributeIntValue(ANDROID, "layout_width", mWidth);
        mHeight = attrs.getAttributeIntValue(ANDROID, "layout_height", mHeight);
    }

    public FourSquarePhotoLoaderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWidth = attrs.getAttributeIntValue(ANDROID, "layout_width", mWidth);
        mHeight = attrs.getAttributeIntValue(ANDROID, "layout_height", mHeight);
    }

    public void setPlaceId(String placeId) {
        this.mPlaceId = placeId;
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        return new FourSquareHttpsAsyncImageLoader(this.getContext(), mPlaceId, mWidth, mHeight);
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap image) {
        setImageBitmap(image);
        invalidate();

        Intent intent = new Intent(ACTION);
        intent.putExtra(CheckinEntry.PLACEID, mPlaceId);
        getContext().sendBroadcast(intent);

        Log.i("FLoaderImageView", mPlaceId);
    }

}
