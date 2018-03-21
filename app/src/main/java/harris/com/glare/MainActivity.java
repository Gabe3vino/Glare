package harris.com.glare;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mPressure;
    float previousLuminosity = 0.0f;
    int screenBrightness = 0;
    float brightnessValue = 0.0f;
    TextView luxLabel;
    View activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = (View) findViewById(R.id.activity_main);


        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        luxLabel = (TextView) findViewById(R.id.lux_label);

    }

    @Override
    protected void onResume()
    {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float currLuminosity = 0;

        if(event != null && event.values[0] >= 0)
            currLuminosity = event.values[0];
        else
            return;

        if(abs(previousLuminosity-currLuminosity) >= 5)
        {
            Log.d("Glare App", "-------------------------------------");
            Log.d("Glare App", "The accuracy: "+ event.accuracy);
            Log.d("Glare App", "The timestamp: "+ event.timestamp);
            Log.d("Glare App", "The measure of light: "+ event.values[0] + " lux.");
            Log.d("Glare App", "-------------------------------------");

            previousLuminosity = currLuminosity;

            if(luxLabel != null)
                luxLabel.setText("The current measure of light is "+currLuminosity+ " luxes!");



            screenBrightness = (int)Math.ceil(19.5f*Math.log(80f*currLuminosity));

            if(screenBrightness > 255)
                screenBrightness = 255;

            brightnessValue = screenBrightness/255f;

            Log.d("Glare App","The screenbrightness value is: " + screenBrightness);
            Log.d("Glare App","The brightness value is: " + brightnessValue);

            //int alphaConversion = (int)Math.ceil(15f*Math.log(75f*currLuminosity));

            if(activity !=null)
            {
                RadioGroup rgbR = (RadioGroup) findViewById(R.id.rgbRadios);

                //activity.setBackgroundColor(Color.rgb(0, 0, screenBrightness));
                setColor(rgbR.getCheckedRadioButtonId());
                setGrayscale();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d("Glare App", "The accuracy of the light sensor changed?");
    }

    public void colorChange(View v)
    {
        RadioGroup rgbR = (RadioGroup) findViewById(R.id.rgbRadios);
        int checkedID = rgbR.getCheckedRadioButtonId();

        setColor(checkedID);

    }

    public void setColor(int checkedID)
    {
        switch(checkedID)
        {
            case R.id.redRadio:
                //activity.setBackgroundColor(Color.rgb(screenBrightness,0,0));
                activity.setBackgroundColor(Color.HSVToColor(new float[] {0f,1f,brightnessValue}));
                break;
            case R.id.greenRadio:
                activity.setBackgroundColor(Color.rgb(0, screenBrightness,0));
                break;
            case R.id.blueRadio:
            activity.setBackgroundColor(Color.rgb(0,0, screenBrightness));
            break;
            default:
                break;
        }
    }

    public void setGrayscale()
    {
        ImageView sun = (ImageView) findViewById(R.id.sunIcon);

        // Apply grayscale filter
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        sun.getBackground().setColorFilter(filter);
    }
}
