package com.dating.padosi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import static com.google.developers.mobile.targeting.proto.Conditions.Condition.ConditionCase.TOPIC;

public class hashkey extends AppCompatActivity {

    Button btnSent;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=AAAAz1jPDnA:APA91bGygYekJeLSyuMhxaMZ6GzJSi3RsHVGlUot_MZt2zAQUQAz_j_C9IcrOKA4fPGaof1cXAt-NPKxgedbKk4VJ5z-TuGd_8QvGCjIkw1sd5wnxd_1smXrM5ErTpIEBQgSRTU9v39N";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private NotificationManager notifManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashkey);
        btnSent = findViewById(R.id.btn_done);




        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to
                String NOTIFICATION_TITLE = "title";
                String NOTIFICATION_MESSAGE = "messege";

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", "eYVIL-5KBpU:APA91bFxfMoHg7J0haQJDorZInw3wbsEasyl6zD3_Jyp8YUM6IpAU6JkX2rWL46XqSI3hnmTQAxO_8j9_-B6K5un97vs44xrP4zg-Yb_tSG4wZy5W2wtQYn8uR_8CkYn1LZs-vk9ApmT");
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e("@@n", "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);
            }*/
                sendnotifaction();
            }
        });


        createNotification("notifaction",this);

    }


    private void sendNotification1() {

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {

            obj = new JSONObject();
            objData = new JSONObject();

            dataobjData = new JSONObject();
            dataobjData.put("message", "android");
            dataobjData.put("image","https://assets.pcmag.com/media/images/638669-apple-imac-27-inch-with-5k-retina-display-2019.jpg?thumb=y&width=90&height=50");
            dataobjData.put("isSuccess", true);
            dataobjData.put("catTitle","catTitle");

            objData.put("content_available","true");
            objData.put("priority", "high");

            obj.put("to","eYVIL-5KBpU:APA91bFxfMoHg7J0haQJDorZInw3wbsEasyl6zD3_Jyp8YUM6IpAU6JkX2rWL46XqSI3hnmTQAxO_8j9_-B6K5un97vs44xrP4zg-Yb_tSG4wZy5W2wtQYn8uR_8CkYn1LZs-vk9ApmT");
            obj.put("notification", objData);
            obj.put("data", dataobjData);

            Log.e("MYOBJs", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("@@SUCCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("@@ERRORS", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization","key=AAAAz1jPDnA:APA91bGygYekJeLSyuMhxaMZ6GzJSi3RsHVGlUot_MZt2zAQUQAz_j_C9IcrOKA4fPGaof1cXAt-NPKxgedbKk4VJ5z-TuGd_8QvGCjIkw1sd5wnxd_1smXrM5ErTpIEBQgSRTU9v39N");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
    public void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, hashkey.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }



    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("@@n", "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(hashkey.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.e("@@n", "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };

    }
    public void createNotification(String aMessage, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = context.getString(R.string.app_name); // default_channel_id
        String title = context.getString(R.string.project_id); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }


    public void sendnotifaction()
    {
        JSONObject mainobj=new JSONObject();

        try {
            mainobj.put("to","eYVIL-5KBpU:APA91bFxfMoHg7J0haQJDorZInw3wbsEasyl6zD3_Jyp8YUM6IpAU6JkX2rWL46XqSI3hnmTQAxO_8j9_-B6K5un97vs44xrP4zg-Yb_tSG4wZy5W2wtQYn8uR_8CkYn1LZs-vk9ApmT");
            JSONObject notifactionobj=new JSONObject();
            notifactionobj.put("title","title");
            notifactionobj.put("body","body");
            mainobj.put("notification",notifactionobj);

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,FCM_API,mainobj,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("@@fcmn",response.toString());
                }
            },
            new Response.ErrorListener()
            {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("@@fcmn",error.toString());
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", serverKey);
                    params.put("Content-Type", contentType);
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            int socketTimeout = 1000 * 60;// 60 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            requestQueue.add(request);

        }catch (Exception e)
        {
            Log.e("@@fcmn",e.getMessage());
        }
    }
}
