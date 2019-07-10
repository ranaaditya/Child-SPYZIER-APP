package com.rana_aditya.child;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class child extends Activity {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyy_lCnI:APA91bGFWdKKPzfdl-dSvIGppCNObt2yoYSv_GJmuEXc0qMFvBYqLZCUGusCoAOjZrpFNLmCliFT4VTorkFZDez5BkblKbL-lL-43dp7NkJigK5UF008FnA7n9Jqxp3eyXaTWYWyzLbc";
    final private String contentType = "application/json";
    public static final String TOPIC = "/topics/parentclient";

    public static final String STATE_RESULT_CODE = "result_code";
    public static final String STATE_RESULT_DATA = "result_data";
    public static final int REQUEST_MEDIA_PROJECTION = 1;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private ImageReader imageReader;
    public  int width,height;
    private MediaProjectionManager mgr;
    Uri uri;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_child);





        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity=displayMetrics.densityDpi;
        width=displayMetrics.widthPixels;
        height=displayMetrics.heightPixels;

        imageReader=ImageReader.newInstance(width,height, ImageFormat.JPEG,2);


        mMediaProjectionManager=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        KeyguardManager myKM = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);

        boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();

        if(isPhoneLocked){
            Toast.makeText(child.this,"locked",Toast.LENGTH_SHORT).show();

           // send("SSNAVLB");
            Toast.makeText(this,"locked",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(child.this, "not locked", Toast.LENGTH_SHORT).show();
           // send("SSAVLB");
            startScreenCapture();

        }

    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void finish() {
        super.finishAndRemoveTask();
stopScreenCapture();
tearDownMediaProjection();


    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture() {

        if (mMediaProjection != null) {
            setUpVirtualDisplay();

        } else if (mResultCode != 0 && mResultData != null) {

            setUpMediaProjection();
            setUpVirtualDisplay();

        } else {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);




        }

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection(){
        Log.d("setttttttttttvvvvv","setting up media display");
        mMediaProjection=mMediaProjectionManager.getMediaProjection(mResultCode,mResultData);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void tearDownMediaProjection(){
        if (mMediaProjection!=null){
            mMediaProjection.stop();
            mMediaProjection=null;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpVirtualDisplay(){

        Log.d("setttttttttttvvvvv","setting up virtual display");

        mVirtualDisplay=mMediaProjection.createVirtualDisplay("ScreenCapture",width,height,mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,imageReader.getSurface(),null,null);



    }
    private void stopScreenCapture(){

        if (mVirtualDisplay==null){
            return;
        }

        mVirtualDisplay.release();
        mVirtualDisplay=null;

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopScreenCapture();
    }


    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(child.this, "successfully send ", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(child.this, "Request error", Toast.LENGTH_LONG).show();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                return;
            }



            mResultCode = resultCode;
            mResultData = data;
            setUpMediaProjection();
            setUpVirtualDisplay();
            Handler handler=new Handler();

            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {

                    final Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
                    try {



                        PixelCopy.request(imageReader.getSurface(), bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                            @Override
                            public void onPixelCopyFinished(int copyResult) {
                                if (copyResult==PixelCopy.SUCCESS){

                                    String stren=BitMapToString(bitmap);


                                    Log.d("[][][][][][]]]]]]]]]]","inside geturi method ");
                                    uri =  getImageUri(child.this, bitmap);
                                    if (uri!=null){
                                        Toast.makeText(child.this, uri.toString(),Toast.LENGTH_SHORT).show();
Log.d("**********************",stren);
                                         send("https://firebasestorage.googleapis.com/v0/b/test-879af.appspot.com/o/uploads%2F1562060454149.png?alt=media&token=72a1bf0c-a26d-4382-9248-886e59b29bac");
                                        finish();
                                    }
                                    else
                                        Toast.makeText(child.this,"uri is null",Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Handler());

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    if (bitmap!=null)
                        Toast.makeText(child.this,"not null",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(child.this,"null",Toast.LENGTH_SHORT).show();

                }
            },100);



        }



    }


    public void send(String string){
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", "call me");
            notifcationBody.put("message", string);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNotification(notification);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }




    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        //String path= MediaStore.Images.Media.insertImage(inContext.getApplicationContext().getContentResolver(),inImage,"title",null);
        return Uri.parse(path);




    }
}
