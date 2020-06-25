package com.dating.padosi

import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dating.MyInterface
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*

class MainAdpter(context: Context) : RecyclerView.Adapter<MainAdpter.ViewHolder>() {
    lateinit var context: Context
    constructor(c: Context,userList: ArrayList<User>) : this(c) {
//        super(context,userList)
        this.userList=userList
        this.context=c
    }


    var mainadater:MainAdpter.ViewHolder?=null
    lateinit var mRef:DatabaseReference
    lateinit var mReflike:DatabaseReference
    lateinit var mdatabase:FirebaseDatabase
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=AAAAz1jPDnA:APA91bGygYekJeLSyuMhxaMZ6GzJSi3RsHVGlUot_MZt2zAQUQAz_j_C9IcrOKA4fPGaof1cXAt-NPKxgedbKk4VJ5z-TuGd_8QvGCjIkw1sd5wnxd_1smXrM5ErTpIEBQgSRTU9v39N"
    private val contentType = "application/json"
    val TAG = "@@ NOTIFICATION TAG"
    private val notifManager: NotificationManager? = null
    var prefs =
        context.getSharedPreferences("data", Context.MODE_PRIVATE)
    lateinit var data:User
    lateinit var userList: ArrayList<User>
    private val instance: MainAdpter? = null
    var c=false

    internal interface myInterface


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdpter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_main_adapter, parent, false)




        mdatabase= FirebaseDatabase.getInstance()
        mRef=mdatabase.getReference("users")
        mReflike=mdatabase.getReference("data")





        return ViewHolder(v)
    }



    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MainAdpter.ViewHolder, position: Int) {
//        holder.bindItems(userList[position])
        this.mainadater=holder
        Log.e("@@t", "p" + (holder.adapterPosition).toString())

        data = userList.get(position)
//        holder.t1.setText(data.name+data.password)
        holder.t1.setText(data.name)
        try {
            holder.about.setText(data?.about)
        } catch (e: Exception) {
            holder.about.setText("null")
        }
        holder.city.setText(data?.city)
        holder.state.setText(data?.state)
        holder.country.setText(data?.country)

        Picasso.with(context).load(data.image)
            .error(R.drawable.user)
            .into(holder.image)

        Log.e("@@", "image" + data.image)
        Log.e("@@", "image" + data.password)
        Log.e("@@", "image" + data.email)
        Log.e("@@", "image" + data.name)
        Log.e("@@", "image" + data.number)
        Log.e("@@", "uid" + data.uid)

        holder.image.setOnClickListener {
            showDialog()
        }

        holder.b1.setOnClickListener {
            like_fuction((context as Activity))
            Log.e("@@", "image" + data.image)
            Log.e("@@", "image" + data.password)
            Log.e("@@", "image" + data.email)
            Log.e("@@", "image" + data.name)
            Log.e("@@", "image" + data.number)
            Log.e("@@", "uid" + data.uid)

        }


        var m:MyInterface
        m.set1(data.city.toString())



    }

    fun getInstance(): MainAdpter? {
        return instance

    }

    fun temp1(activity: Activity)
    {
        Log.e("@@t1","called")
        Log.e("@@t1","called"+get().toString())
    }

    fun like_fuction(activity: Activity)
    {

        Log.e("@@t","called1")
        var user_uid: String? =prefs.getString("uid","null")
        var user=F_Model(data.name,data.image,data.uid,data.fcm)

        Log.e("@@t","useruid "+user_uid.toString()+" userdata"+user.toString())

        mRef.child(data.uid.toString()).child("likes").child(user_uid.toString()).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(activity,"Liked", Toast.LENGTH_LONG).show()
                sendnotifaction(user.fcm.toString().trim(),data.name+" Like Your Profile")
                Log.e("@@fl","called2")
            }
    }

    override fun getItemId(position: Int): Long {
        Log.e("@@t","getitemid"+(position).toString())
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        Log.e("@@t","getitemviewtype"+(position).toString())
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        Log.e("@@t","getitemcount"+(userList).toString())
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var b1=itemView.findViewById<FloatingActionButton>(R.id.like_button)
        val t1=itemView.findViewById<TextView>(R.id.t1)
        val image=itemView.findViewById<ImageView>(R.id.image1)
        val about=itemView.findViewById<TextView>(R.id.t_about)
        val city=itemView.findViewById<TextView>(R.id.t_city)
        val state=itemView.findViewById<TextView>(R.id.t_state)
        val country=itemView.findViewById<TextView>(R.id.t_country)
    }

    fun sendnotifaction(fcm:String,msg:String) {
        val FCM_API = "https://fcm.googleapis.com/fcm/send"
        val mainobj = JSONObject()
        try {
            mainobj.put(
                "to",
                fcm
            )
            val notifactionobj = JSONObject()
            notifactionobj.put("title", "Like Your Profile")
            notifactionobj.put("body", msg)
            notifactionobj.put("image","https://www.eharmony.co.uk/dating-advice/wp-content/uploads/2018/04/whatislove.jpg")
            mainobj.put("notification", notifactionobj)
            val request: JsonObjectRequest =
                object : JsonObjectRequest(
                    Method.POST, FCM_API, mainobj,
                    Response.Listener { response -> Log.e("@@fcmn", response.toString()) },
                    Response.ErrorListener { error -> Log.e("@@fcmn", error.toString()) }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> =
                            HashMap()
                        params["Authorization"] = serverKey
                        params["Content-Type"] = contentType
                        return params
                    }
                }
            val requestQueue = Volley.newRequestQueue(context)
            val socketTimeout = 1000 * 60 // 60 seconds
            val policy: RetryPolicy = DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            request.retryPolicy = policy
            requestQueue.add(request)
        } catch (e: Exception) {
            Log.e("@@fcmn", e.message)
        }
    }

        fun showDialog() {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog)
            dialog.getWindow()
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mDialogNo: FrameLayout = dialog.findViewById(R.id.btn_cancel)
            mDialogNo.setOnClickListener(View.OnClickListener {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            })
            val mDialogOk: FrameLayout = dialog.findViewById(R.id.btn_video)
            mDialogOk.setOnClickListener(View.OnClickListener {
                Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            })
            dialog.show()
        }




}

private interface TaskListener {
    fun finished(result: Boolean)
}