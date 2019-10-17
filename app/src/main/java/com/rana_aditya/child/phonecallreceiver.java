package com.rana_aditya.child;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class phonecallreceiver extends BroadcastReceiver {
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyy_lCnI:APA91bGFWdKKPzfdl-dSvIGppCNObt2yoYSv_GJmuEXc0qMFvBYqLZCUGusCoAOjZrpFNLmCliFT4VTorkFZDez5BkblKbL-lL-43dp7NkJigK5UF008FnA7n9Jqxp3eyXaTWYWyzLbc";
    final private String contentType = "application/json";
    public static final String TOPIC = "/topics/parentclient";
    Context context;
     @Override
    public void onReceive(Context context, Intent intent) {
 this.context=context;
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
            String incomin_number=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            send("phone_number",incomin_number);


        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                     //   Toast.makeText(context, "successfully send ", Toast.LENGTH_SHORT).show();
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
                        Log.d("SUCCESS","successfully send");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  Toast.makeText(MyServices.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.d("ERROR","no internet connection to send this message");

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    public  void send(String title ,String message){
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message",  message);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNotification(notification);
    }

}
