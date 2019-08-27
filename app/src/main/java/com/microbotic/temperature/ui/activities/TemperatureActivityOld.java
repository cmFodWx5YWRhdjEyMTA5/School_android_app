package com.microbotic.temperature.ui.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.core.BlueUnit;
import com.jumper.bluetoothdevicelib.device.temperature.BroadcastDeviceTemperature;
import com.jumper.bluetoothdevicelib.device.temperature.DeviceTemperature;
import com.jumper.bluetoothdevicelib.device.temperature.TemperatureResult;
import com.jumper.bluetoothdevicelib.result.Listener;
import com.microbotic.temperature.R;
import com.microbotic.temperature.app.Config;
import com.microbotic.temperature.services.BaseReceiveService;
import com.microbotic.temperature.utils.MaintainRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class TemperatureActivityOld extends AppCompatActivity implements Listener<TemperatureResult> {

    private final String tag = TemperatureActivityOld.class.getSimpleName();
    private Context context;
    private TextView tvMessage, tvResult;
    private ImageView imgCamera;
    private Button btnCamera;
    private boolean showCamera = false;

    private AudioTrack trackPlayer;
    private FileOutputStream fos = null;

    //cjs bot service
    private BaseReceiveService.MessageCallBack messageCallBack;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        tvMessage = findViewById(R.id.tvMessage);
        tvResult = findViewById(R.id.tvResult);

        btnCamera = findViewById(R.id.btnCamera);

        String id = getIntent().getStringExtra("id");


        progressDialog = new ProgressDialog(TemperatureActivityOld.this);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showCamera) {

                    btnCamera.setText("Open Camera");
                    showCamera = false;
                    imgCamera.setImageResource(R.drawable.img_camera);

                    return;
                }

                showCamera = true;

                btnCamera.setText("Close Camera");

            }
        });


        //   initAudio();

        //cjs code
        messageCallBack = new BaseReceiveService.MessageCallBack() {
            @Override
            public void jsonMessageComing(String msg) {

                try {
                    Log.e(tag, "EVENT >> " + msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void videoDataComing(byte[] videoData) {
            }


            @SuppressLint("NewApi")
            @Override
            public void audioDataComing(byte[] audioData) {

            }
        };


        //activity_temperature
        if (!BlueUnit.isHaveBleFeature(this)) {
            String msg = "Device does not support Bluetooth 4.0";
            tvMessage.setText(msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else if (!BlueUnit.isEnabled(this)) {
            String msg = "Bluetooth is not turned on";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            //bluetooth adapter
            ADBlueTooth adBlueTooth = new ADBlueTooth(this, new DeviceTemperature(), new BroadcastDeviceTemperature());
            adBlueTooth.setResultListener(this);
            adBlueTooth.init();
        }
    }

    protected void onActivityResult(int reqCode, int result, Intent data) {
        super.onActivityResult(reqCode, result, data);
        Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        imgCamera.setImageBitmap(bitmap);

        int bufSize = AudioTrack.getMinBufferSize(16000,//??8K?? 
                AudioFormat.CHANNEL_OUT_MONO,//???
                AudioFormat.ENCODING_PCM_16BIT);

        trackPlayer = new AudioTrack(AudioManager.STREAM_MUSIC,
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize,
                AudioTrack.MODE_STREAM);

        trackPlayer.play();
    }

    public void onResult(final TemperatureResult result) {
        Log.e(tag, "Temperature Result");

        if (result != null) {
            Log.e(tag, "Temperature Result >> Not Null");
            final String res = "Body activity_temperature (C):" + result.getTemp4C();


            Log.d(tag, "result ---->" + result.temperature);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessage.setText(res);
                    sendData("1", "1", String.valueOf(result.getTemp4C()));


                }
            });


            return;
        }


        Log.e(tag, "Temperature Result >> NULL");
    }

    @Override
    public void onConnectedState(int state) {
        Log.e(tag, "Temperature Device Connected");

        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvMessage.setText("Temperature Device Connected!");

            }
        });
    }

    private void sendData(final String id, final String studentId, final String temp) {
        String url = Config.BASE_URL + "healthrecord";
        url = url.replace(" ", "%20");
        url = url.replace("\n", "%0A");
        progressDialog.show();
        progressDialog.setMessage("Please wait...");

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("studentId", "1");
            jsonBody.put("activity_temperature", temp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest req = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error " + error.getMessage());
                progressDialog.dismiss();
            }

        }) {
            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }

        };
        MaintainRequestQueue.getInstance(this).addToRequestQueue(req, "tag");
    }

}


