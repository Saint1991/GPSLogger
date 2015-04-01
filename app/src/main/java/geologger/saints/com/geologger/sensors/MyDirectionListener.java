package geologger.saints.com.geologger.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import geologger.saints.com.geologger.utils.Direction;


/**
 * Created by Mizuno on 2015/02/13.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MyDirectionListener implements SensorEventListener {

    private final String TAG = getClass().getSimpleName();
    public final String ACTION = "CurrentDirectionUpdated";
    private static final int DIMENSION = 3;
    private static final int MATRIX_SIZE = 16;

    private float[] mAccelerometerValues = null;
    private float[] mGeoMagneticValues = null;

    @RootContext
    Context mContext;

    @SystemService
    SensorManager mSensorManager;

    public MyDirectionListener() {}

    @AfterInject
    public void init() {

        mAccelerometerValues = new float[DIMENSION];
        mGeoMagneticValues = new float[DIMENSION];

        Sensor magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        switch (event.sensor.getType()) {

            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeoMagneticValues = event.values.clone();
                break;

            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerValues = event.values.clone();
                break;

            default:
                return;
        }

        if (mGeoMagneticValues != null && mAccelerometerValues != null) {
            onDirectionChanged();
        }
    }


    private void onDirectionChanged() {

        if (mGeoMagneticValues == null || mAccelerometerValues == null) {
            return;
        }

        float[] r = new float[MATRIX_SIZE];
        float[] i = new float[MATRIX_SIZE];

        SensorManager.getRotationMatrix(r, i, mAccelerometerValues, mGeoMagneticValues);
        if (r == null) {
            return;
        }

        float[] temp = new float[MATRIX_SIZE];
        SensorManager.remapCoordinateSystem(r, SensorManager.AXIS_X, SensorManager.AXIS_Y, temp);

        float[] direction = new float[DIMENSION];
        SensorManager.getOrientation(temp, direction);

        float orientation = ((float)(direction[0] * 180.0f / Math.PI) + 360.0f) % 360;
        Log.i(TAG, "onDirectionChanged: " + orientation);
        Direction.saveDirection(mContext.getApplicationContext(), orientation);

        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(Direction.DIRECTION, orientation);
        mContext.sendBroadcast(broadcastIntent);
    }

    /**
     * Unregister Listener
     */
    public void stopRegisteredListener() {
        mSensorManager.unregisterListener(this);
        Log.i(TAG, "unregisterDirectionListener");
    }

}
