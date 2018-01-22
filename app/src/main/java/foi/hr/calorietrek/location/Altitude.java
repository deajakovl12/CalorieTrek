package foi.hr.calorietrek.location;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.List;
import static android.content.ContentValues.TAG;

/**
 * Created by marko on 10-Dec-17.
 * Class that implements sensor event listener so it can register to new changes in pressure sensor. After initialization of this class it is possible to find out current altitude from pressure sensor readings.
 */

public class Altitude implements SensorEventListener{
    private SensorManager mySensorManager;
    private boolean pressureSensorAvailable = true;
    private boolean isRegistered = false;
    private  Sensor pressureSensor;
    private Context context;
    private boolean isAltitudeAvailable=false;
    private double altitude;
    public Altitude(Context context) {
        mySensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        listAllSensors();
        if (mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            pressureSensorAvailable = true;
            pressureSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if(!isRegistered)mySensorManager.registerListener(this,pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);
            isRegistered=true;
        }
        else {
            pressureSensorAvailable = false;
        }
    }

    public boolean isAltitudeAvailable() {
        return isAltitudeAvailable;
    }

    public boolean isPressureSensorAvailable() {
        return pressureSensorAvailable;
    }

    public double getAltitude() {
        return altitude;
    }

    public void listAllSensors() {
        List<Sensor> deviceSensors = mySensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.e(TAG, "listAllSensors: "+deviceSensors.toString());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        altitude=SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,event.values[0]);
        isAltitudeAvailable=true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onResume()
    {
        if(isPressureSensorAvailable() && !isRegistered)mySensorManager.registerListener(this,pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);
        isRegistered=true;
    }
    public void onPause()
    {
        if(isPressureSensorAvailable() && isRegistered)mySensorManager.unregisterListener(this);
        isRegistered=false;
    }
}
