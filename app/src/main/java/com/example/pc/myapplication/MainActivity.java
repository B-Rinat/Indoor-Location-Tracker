package com.example.pc.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    float magnitudeForHandMovement=0;

    // START NEW STORY

    double magAvg=0;
    double counterForMagAvg=0;




    // END NEW STORY

    ImageView my_local_point, compass;
    TextView tv;
    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    float currentAzimuth = 0f;
    SensorManager sensorManager;
    int sampleBase = 50000;
    private LinkedList<float[]> data=new LinkedList<>();

    LineView drawLine;
    boolean stop;
    public static int count_for_SCANNING=0;

    float alongX = 226, alongY = 690;

    int sample_count_move = 0;
    int sample_count_stay = 0;
    double count_compass_button  = 0.0;
    static double magnitude_avg = 0.0;
    boolean can_i_move = true;
    int count_hand_movement=0;
    int error_hand_move=0;
    boolean MODE_SOLDIER = false;
    int count_mode_soldier=0;
    float z,b,q,w;
    Region region;
    double average_for_move=0, average_for_stay=0, generalCountMoveStay=0;
    int constant=0;


    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    public static final int REQUEST_WRITE_STORAGE = 112;
    public static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 3;
    public  static int counter_until_50 = 0;

    public static String name_of_file = "WAY_BOARDERS_DATA.txt";
    int[] vec = {0,0,0,0,0,0,0,0,0,0,0};
    int[] countDown = {0,0,0,0,0,0,0,0,0,0,0};

   /*
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void requestPermissionForReadExternalStorage(Context context) throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else {
            return true;
        }
    }

    public  boolean onTouchEvent(MotionEvent event){

        String x = String.valueOf(event.getX() - 51);
        String y = String.valueOf(event.getY() - 270);
        String msg = "X: " + x + " Y: " + y;
        Log.e("TAG", msg);
        int xx = (int) (event.getX() - 51);
        int yy = (int) (event.getY() - 270);

        return false;
    }
*/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_local_point = findViewById(R.id.point);
        compass = findViewById(R.id.location);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        final Button locateUsingWiFi = (Button)findViewById(R.id.button);
        final Button compassButton = (Button)findViewById(R.id.compass);

        tv=(TextView)findViewById(R.id.motion);
        drawLine = (LineView) findViewById(R.id.boarder);
        region = new Region();


        final double[] error = new double[9];
        final double[] avg = new double[11];

        // Coordinates for x,y in 9 points on the second floor EB3 of UNIST Building mapped into map
        final int[] X_axis = {
                226,	403,	593,	597,	787,	776,	775,	588,	578
        };
        final int[] Y_axis = {
                690,	674,	500,	  669, 	497,    670,	974,	977,	1154
        };

        // Average of signal strength in 9 points on the second floor EB3 of UNIST Building
        final double[][] a = {
                {75.5,	81.1667	,	72.1667	,	71.1667	,	62	,	68	,	72.1667	,	37.5	,	79.1667	,	75.8333	,	81.5},
                {78.1667	,	81	,	71	,	56.5	,	60.8333	,	64	,	71.1667	,	48.5	,	83.8333	,	78.5	,	87.8333},
                {83	,	88.1429	,	80.8571	,	58.1429	,	70.7143	,	76	,	81	,	69.1429	,	83	,	76	,	88.8571},
                {80.8333	,	85.5	,	75.8333	,	61.1667	,	53	,	71.3333	,	76	,	56.1667	,	58.8333	,	80.8333	,	84.5},
                {82	,	87	,	78	,	83.8333	,	48.8571	,	90.2857	,	78	,	77	,	0	,	82	,	68},
                {81.5	,	0	,	84.6667	,	73	,	38	,	80.8333	,	85.5	,	61.5	,	88	,	82	,	65},
                {87	,	80.5	,	72.3333	,	73	,	57.8333	,	78.6667	,	72.3333	,	85.3333	,	0	,	87	,	48},
                {0	,	76.1667	,	69.3333	,	74.4	,	77.5	,	72	,	69.3333	,	80.5	,	88.5	,	0	,	63},
                {0	,	77	,	69.6667	,	77.2	,	84.8333	,	66.1667	,	69.5	,	85.1667	,	85.5	,	0	,	82.3333}
        };

        compassButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                double threshold = 0.01;
                String result = "";
                double magnitude = 0.0;
                int it;
                for (it=0; it < data.size(); it++) {
                    float[] x_y_z = data.get(it);
                    magnitude += Math.sqrt(x_y_z[0] * x_y_z[0] + x_y_z[1] * x_y_z[1] + x_y_z[2] * x_y_z[2]) - 9.8;
                }

                magnitude = magnitude / data.size();
                count_compass_button++;

//                Log.e("TAG", String.valueOf(magnitude));

                if (magnitude > threshold) {
                    sample_count_move++;
                } else {
                    sample_count_stay++;
                }

                // START NEW STORY

                boolean SHAKE_MODE=false;
                boolean STAND_MODE=false;
                boolean WALK_ORDINARY_MODE=false;
                boolean WALK_FASTRER_MODE=false;
                boolean RUNNING_SMALL_MODE=false;
                boolean UNDETERMINED=false;

                magAvg += magnitude;
                counterForMagAvg++;

//                constant = 16;

                if(counterForMagAvg == 50){
                    magAvg /= counterForMagAvg;
                    if(magAvg>(-0.135) && magAvg<0.3){
                        STAND_MODE = true;
                    } else if(magAvg>0.4 && magAvg<0.67){
                        WALK_ORDINARY_MODE = true;
                        constant = 17;
                    } else if(magAvg>1.22 && magAvg<2.56){
                        WALK_FASTRER_MODE = true;
                        constant = 23;
                    } else if(magAvg>4.55 && magAvg<4.9){
                        RUNNING_SMALL_MODE = true;
                        constant = 20;
                    } else if(magAvg>9.1 && magAvg<13){
                        SHAKE_MODE = true;
                    } else {
                        UNDETERMINED = true;
                    }

                    Log.e("TAG", String.valueOf(magAvg));
                    counterForMagAvg=0;
                    magAvg=0;
                }

                boolean moving = (WALK_ORDINARY_MODE || WALK_FASTRER_MODE || RUNNING_SMALL_MODE);
                boolean standing = (STAND_MODE || SHAKE_MODE);




                // END NEW STORY


                count_hand_movement++;
                if(magnitude < 0.9 && magnitude > 0.2){
                    magnitudeForHandMovement+=Math.abs(magnitude);
                    MODE_SOLDIER = true;
                } else {
                    error_hand_move+=Math.abs(magnitude);
                }
                if(count_hand_movement > 4 && (error_hand_move/4 >= 0.9 || error_hand_move/4 <= 0.2) ){
                    error_hand_move=0;
                    count_hand_movement=0;
                    magnitudeForHandMovement=0;
                    MODE_SOLDIER = false;

                } else {
                    float avg;
                    avg = magnitudeForHandMovement / count_hand_movement;
                }

                int sample = 10;
                if(count_compass_button > sample){
                    if(sample_count_stay > 0 && sample_count_move > 0)
                    magnitude_avg = Math.abs(sample_count_stay-sample_count_move);
                    else magnitude_avg = 11;
                }

                float prevX = alongX; float prevY = alongY;


//                generalCountMoveStay++;
//                if(generalCountMoveStay == 10){
//                    average_for_move += average_for_move / 50.0;
//                    average_for_stay += average_for_stay / 50.0;
//                    double avgTemp = Math.abs(average_for_move - average_for_stay);
//                    if( avgTemp < 0.11  && avgTemp > 0.05) {
//                        constant = 15;
//                    }
//                    else if(avgTemp < 0.2 ) {
//                        constant = 18;
//                    }
//                    else if(avgTemp < 0.5) {
//                        constant = 25;
//                    }
//                    else if(avgTemp < 0.9) {
//                        constant = 30;
//                    } else {
//                        constant = 0;
//                    }
//
//                    generalCountMoveStay=0;
//
//
//                }


                if(average_for_stay > 1.1 || average_for_move > 1.1){
                    average_for_stay =0;
                    average_for_move =0;
                }

//                Log.e("TAG", String.valueOf(magnitude_avg));

                if(moving) {

                    double temp = constant * 0.4;

                    // Range for direction changed
                    if (currentAzimuth >= 10 && currentAzimuth <= 22) {
                        alongX -= temp;
                        alongY += temp;
                    } else if (currentAzimuth >= 23 && currentAzimuth <= 110) {
                        alongX -= constant;
                   } else if (currentAzimuth >= 111 && currentAzimuth <= 126) {
                        alongX -= temp;
                        alongY -= temp;
                    } else if (currentAzimuth >= 127 && currentAzimuth <= 188) {
                        alongY -= constant;
                    } else if (currentAzimuth >= 196 && currentAzimuth <= 216) {
                        alongX += temp;
                        alongY -= temp;
                    } else if (currentAzimuth >= 217 && currentAzimuth <= 274) {
                        alongX += constant;
                    } else if (currentAzimuth >= 275 && currentAzimuth <= 305) {
                        alongX += temp;
                        alongY += temp;
                    } else if (currentAzimuth >= 306 && currentAzimuth <= 360 || (currentAzimuth > 0 && currentAzimuth < 10)) {
                        alongY += constant;
                    }

                }  else if(standing){


                } else {
                    // do what you did previously
                }


                if(region.validate((int)alongX, (int)alongY)){

                    z = my_local_point.getX() + my_local_point.getWidth()  / 2;
                    b = my_local_point.getY() + my_local_point.getHeight() / 2;

                    my_local_point.setX(alongX);
                    my_local_point.setY(alongY);

                   q = my_local_point.getX() + my_local_point.getWidth()  / 2;
                   w = my_local_point.getY() + my_local_point.getHeight() / 2;


                        drawLine.drawMyLine(z, b, q, w);

                } else {
                    alongX = prevX;
                    alongY = prevY;
                }


//                String mag = Double.toString(magnitude);
//                Log.e("TAG", mag);
//                tv.setText(String.valueOf(result));



                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        compassButton.performClick();
                    }
                }, 1);
            }
        });

        locateUsingWiFi.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                stop = false;
                final WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert wifiMan != null;
                wifiMan.startScan();

                counter_until_50++;

                registerReceiver(new BroadcastReceiver() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onReceive(Context context, Intent intent) {


                        final int state = wifiMan.getWifiState();

                        List<ScanResult> results;
                        if(state == WifiManager.WIFI_STATE_ENABLED && !stop) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                                results = wifiMan.getScanResults();

                            } else {
                                results = wifiMan.getScanResults();
                            }

                            Context conol = getApplicationContext();

                            for (ScanResult result : results) {
                                try{

                                if (!result.BSSID.equals("38:20:56:7e:da:8d") && !result.BSSID.equals("38:20:56:81:03:8c") && !result.BSSID.equals("38:20:56:80:ff:6e") && !result.BSSID.equals("00:1c:85:0f:12:14") &&
                                        !result.BSSID.equals("38:20:56:7e:de:8f") &&
                                        !result.BSSID.equals("00:1c:85:0c:3d:9b") && !result.BSSID.equals("38:20:56:80:ff:6c") && !result.BSSID.equals("38:20:56:85:ad:2c") &&
                                        !result.BSSID.equals("38:20:56:81:08:2e") &&
                                        !result.BSSID.equals("38:20:56:81:04:6d") && !result.BSSID.equals("38:20:56:7e:da:8f"))
                                {
                                    continue;
                                }

                                int level1 = result.level;
                                switch (result.BSSID) {
                                    case "38:20:56:7e:da:8d":
                                        vec[0] += level1;
                                        countDown[0]++;
                                        break;
                                    case "38:20:56:81:03:8c":
                                        vec[1] += level1;
                                        countDown[1]++;
                                        break;
                                    case "38:20:56:80:ff:6e":
                                        vec[2] += level1;
                                        countDown[2]++;
                                        break;
                                    case "00:1c:85:0f:12:14":
                                        vec[3] += level1;
                                        countDown[3]++;
                                        break;
                                    case "38:20:56:7e:de:8f":
                                        vec[4] += level1;
                                        countDown[4]++;
                                        break;
                                    case "00:1c:85:0c:3d:9b":
                                        vec[5] += level1;
                                        countDown[5]++;
                                        break;
                                    case "38:20:56:80:ff:6c":
                                        vec[6] += level1;
                                        countDown[6]++;
                                        break;
                                    case "38:20:56:85:ad:2c":
                                        vec[7] += level1;
                                        countDown[7]++;
                                        break;
                                    case "38:20:56:81:04:6d":
                                        vec[8] += level1;
                                        countDown[8]++;
                                        break;
                                    case "38:20:56:7e:da:8f":
                                        vec[9] += level1;
                                        countDown[9]++;
                                        break;
                                    case "38:20:56:81:08:2e":
                                        vec[10] += level1;
                                        countDown[10]++;
                                        break;
                                }

                                    if (counter_until_50 == 50) {

                                        counter_until_50 = 0;

                                        for (int i = 0; i < 11; i++) {
                                            int tempo = countDown[i];
                                            if (tempo <= 0) {
                                                tempo = 1;
                                            }
                                            avg[i] = vec[i] / tempo; // Average for Each of the MAC's signals
                                            vec[i] = 0;
                                            countDown[i] = 0;
                                        }

                                        for(int k=0; k<9; k++){
                                            int NUMBER=0;
                                            double sum_square[] = new double[11];
                                            for(int t=0; t<11; t++){
                                               if (a[k][t] != 0 && avg[t] != 0) {
                                                    NUMBER++;
                                                    sum_square[k] += Math.pow((a[k][t] - Math.abs(avg[t])), 2);
                                                }

                                            }
                                            if(NUMBER!=0){
                                                error[k] = Math.sqrt(sum_square[k]) / NUMBER;
                                            } else {
                                                error[k] = 1000000;
                                            }

                                        }
                                        int PLACE=0;
                                        double MIN = error[0];
                                        for(int w=1; w<9; w++){
                                            if(MIN > error[w]){
                                                MIN = error[w];
                                                PLACE = w;
                                            }
                                        }

                                        float X = X_axis[PLACE];
                                        float Y = Y_axis[PLACE];


                                        my_local_point.setX(X);
                                        my_local_point.setY(Y);

                                        alongX = X;
                                        alongY = Y;

                                        z = my_local_point.getX() + my_local_point.getWidth()  / 2;
                                        b = my_local_point.getY() + my_local_point.getHeight() / 2;
                                        drawLine.drawMyLine(z, b, z, b);

                                        Toast.makeText(conol, "X_axis: " + my_local_point.getX() + " Y_axis: " + my_local_point.getY(), Toast.LENGTH_SHORT).show();

                                    }

                            } catch (Exception e){
                                    Toast.makeText(conol, "Exception: " + e, Toast.LENGTH_SHORT).show();
                                }
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    count_for_SCANNING++;
                                    if(count_for_SCANNING < 160){
                                        locateUsingWiFi.performClick();
                                    } else {
                                        count_for_SCANNING = 0;
                                        stop = true;
                                    }
                                }
                            }, 1);
                        }
                    }
                }, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
            }
        });
    }

        protected void onResume(){
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

        @Override
        public void onSensorChanged (SensorEvent s_event){
        final float alpha = 0.8f;

        if (s_event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * s_event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * s_event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * s_event.values[2];
                float[] temp = {s_event.values[0], s_event.values[1], s_event.values[2]};
                data.add(temp);
                if (data.size() > 3 * 1000000 / 50000) {
                    data.remove();
                }
            }
        if (s_event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * s_event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * s_event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * s_event.values[2];
            }

         float R[] = new float[9];
         float I[] = new float[9];
         boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
         if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;

                Animation myCompass = new RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                currentAzimuth = azimuth;
             myCompass.setDuration(500);
             myCompass.setRepeatCount(0);
             myCompass.setFillAfter(true);

             tv.setText(String.valueOf(currentAzimuth));
                compass.startAnimation(myCompass);
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
