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

import java.io.FileInputStream;
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

    public void setImageBitmap(String path) {
        Bitmap image = loadBitmap(path);
        modifyView(path, image.getWidth(), image.getHeight());
        setImageBitmap(image);
        invalidate();
    }

    private Bitmap loadBitmap(String path) {

        Bitmap ret = null;

        try {

            FileInputStream iStream = new FileInputStream(path);
            ret = BitmapFactory.decodeStream(iStream);
            iStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private void modifyView(String path, int originalBitmapWidth, int originalBitmapHeight) {

        try {

            ExifInterface exif = new ExifInterface(path);
            int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));

            ViewGroup.LayoutParams params = this.getLayoutParams();
            float factor;
            Matrix matrix = new Matrix();
            matrix.reset();

            Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int viewWidth = size.x;

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
