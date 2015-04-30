package geologger.saints.com.geologger.uicomponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Mizuno on 2015/04/12.
 */
public class ScalableImageView extends ImageView {

    public ScalableImageView(Context context) {
        super(context);
    }

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set Bitmap From file whose size adjusts to the display size
     * @param path
     */
    public void setImageBitmap(String path) {

        Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Bitmap image = loadResizedBitmap(path, size.x, size.y);
        if (image == null) {
            return;
        }

        adjustView(path, size.x, image.getWidth(), image.getHeight());
        setImageBitmap(image);
        invalidate();
    }

    public void setImageBitmap(String path, int viewWidth, int viewHeight) {

        Bitmap photo = loadResizedBitmap(path, viewWidth, viewHeight);
        if (photo == null) {
            return;
        }

        adjustView(path, viewWidth, photo.getWidth(), photo.getHeight());
        setImageBitmap(photo);
        invalidate();
    }

    private Bitmap loadResizedBitmap(String path, int width, int height) {

        Bitmap ret = null;

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeFile(path, options);

            int scaleH = options.outWidth / width + 1;
            int scaleV = options.outHeight / height + 1;
            int scale = Math.max(scaleH, scaleV);

            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;

            ret = BitmapFactory.decodeFile(path, options);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    private void adjustView(String path, int viewWidth, int originalBitmapWidth, int originalBitmapHeight) {

        try {

            ExifInterface exif = new ExifInterface(path);
            int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));

            ViewGroup.LayoutParams params = this.getLayoutParams();
            float factor;
            Matrix matrix = new Matrix();
            matrix.reset();

            switch(orientation) {
                case 1:
                    factor = (float)viewWidth / (float)originalBitmapWidth;
                    matrix.preScale(factor, factor);
                    params.width = (int)(originalBitmapWidth * factor);
                    params.height = (int)(originalBitmapHeight * factor);
                    break;
                case 2://flip vertical
                    factor = (float)viewWidth/(float)originalBitmapWidth;
                    matrix.postScale(factor, -factor);
                    matrix.postTranslate(0, originalBitmapHeight * factor);
                    params.width = (int)(originalBitmapWidth*factor);
                    params.height = (int)(originalBitmapHeight*factor);
                    break;
                case 3://rotate 180
                    matrix.postRotate(180, originalBitmapWidth / 2f, originalBitmapHeight / 2f);
                    factor = (float)viewWidth/(float)originalBitmapWidth;
                    matrix.postScale(factor, factor);
                    params.width = (int)(originalBitmapWidth*factor);
                    params.height = (int)(originalBitmapHeight*factor);
                    break;
                case 4://flip horizontal
                    factor = (float)viewWidth/(float)originalBitmapWidth;
                    matrix.postScale(-factor, factor);
                    matrix.postTranslate(originalBitmapWidth * factor, 0);
                    params.width = (int)(originalBitmapWidth*factor);
                    params.height = (int)(originalBitmapHeight*factor);
                    break;
                case 5://flip vertical rotate270
                    matrix.postRotate(270, 0, 0);
                    factor = (float)viewWidth/(float)originalBitmapHeight;
                    matrix.postScale(factor, -factor);
                    params.width = (int)(originalBitmapHeight*factor);
                    params.height = (int)(originalBitmapWidth*factor);
                    break;
                case 6://rotate 90
                    matrix.postRotate(90, 0, 0);
                    factor = (float)viewWidth/(float)originalBitmapHeight;
                    matrix.postScale(factor, factor);
                    matrix.postTranslate(originalBitmapHeight * factor, 0);
                    params.width = (int)(originalBitmapHeight*factor);
                    params.height = (int)(originalBitmapWidth*factor);
                    break;
                case 7://flip vertical, rotate 90
                    matrix.postRotate(90, 0, 0);
                    factor = (float)viewWidth/(float)originalBitmapHeight;
                    matrix.postScale(factor, -factor);
                    matrix.postTranslate(originalBitmapHeight * factor, originalBitmapWidth * factor);
                    params.width = (int)(originalBitmapHeight*factor);
                    params.height = (int)(originalBitmapWidth*factor);
                    break;
                case 8://rotate 270
                    matrix.postRotate(270, 0, 0);
                    factor = (float)viewWidth/(float)originalBitmapHeight;
                    matrix.postScale(factor, factor);
                    matrix.postTranslate(0, originalBitmapWidth * factor);
                    params.width = (int)(originalBitmapHeight*factor);
                    params.height = (int)(originalBitmapWidth*factor);
                    break;
            }

            this.setScaleType(ScaleType.MATRIX);
            this.setLayoutParams(params);
            this.setImageMatrix(matrix);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
