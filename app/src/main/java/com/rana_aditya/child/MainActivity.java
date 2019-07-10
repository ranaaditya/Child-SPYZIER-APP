package com.rana_aditya.child;

import android.app.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyy_lCnI:APA91bGFWdKKPzfdl-dSvIGppCNObt2yoYSv_GJmuEXc0qMFvBYqLZCUGusCoAOjZrpFNLmCliFT4VTorkFZDez5BkblKbL-lL-43dp7NkJigK5UF008FnA7n9Jqxp3eyXaTWYWyzLbc";
    final private String contentType = "application/json";
    public static final String TOPIC = "/topics/parent";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NotificationChannel channel = new NotificationChannel("mychannel", "mychannel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        FirebaseMessaging.getInstance().subscribeToTopic("childclient")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "sucessfull";
                        if (!task.isSuccessful()) {

                            msg = "failed";
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });



    }

    public void send(View view) {

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", "call me");
            notifcationBody.put("message", "call me immediately ");

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "some error in this transaction", Toast.LENGTH_SHORT).show();
        }
        sendNotification(notification);

    }



    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "successfully send ", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();

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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }



}
