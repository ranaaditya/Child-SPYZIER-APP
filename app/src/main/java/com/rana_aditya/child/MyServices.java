package com.rana_aditya.child;


import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MyServices extends FirebaseMessagingService {


    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyy_lCnI:APA91bGFWdKKPzfdl-dSvIGppCNObt2yoYSv_GJmuEXc0qMFvBYqLZCUGusCoAOjZrpFNLmCliFT4VTorkFZDez5BkblKbL-lL-43dp7NkJigK5UF008FnA7n9Jqxp3eyXaTWYWyzLbc";
    final private String contentType = "application/json";
    public static final String TOPIC = "/topics/parentclient";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

if (remoteMessage.getData().get("message").equals("SS")) {
    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    //for devices lower api
    //vibrator.vibrate(1000);
    vibrator.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
    shownotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
    Log.d("OPENING SCREENSHOT CLASS","OPENING");
    Intent intent=new Intent(this,child.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);


}
else if (remoteMessage.getData().get("message").equals("LL")){

    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    vibrator.vibrate(VibrationEffect.createOneShot(5000,VibrationEffect.DEFAULT_AMPLITUDE));
    //send("LL");
    Log.d("SENT THE LOCATION INFORMATION ","LOCATION");
   send_location();
    shownotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));



}


    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String token= FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().subscribeToTopic("childclient");


    }


    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MyServices.this, "successfully send ", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyServices.this, "Request error", Toast.LENGTH_LONG).show();

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


    public  void shownotification(String title,String message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mychannel");
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);
        builder.setContentText(message);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat manager= NotificationManagerCompat.from(this);
        manager.notify(1,builder.build());

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

    private String getfileextension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));

    }

private void send_location(){

    FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MyServices.this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    String lati=String.valueOf(location.getLatitude());
                    String longi=String.valueOf(location.getLongitude());
                    Log.d(lati,longi);
                    send(lati,longi);
                }
            }
        });

}

}
