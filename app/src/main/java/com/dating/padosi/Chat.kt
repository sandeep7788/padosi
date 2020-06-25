package com.dating.padosi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.firebase.client.ChildEventListener
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import java.util.*

class Chat : AppCompatActivity() {
    var layout: LinearLayout? = null
    var layout_2: RelativeLayout? = null
    lateinit var sendButton: ImageView
    lateinit var messageArea: EditText
    var scrollView: ScrollView? = null
    var reference1: Firebase? = null
    var reference2:Firebase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        layout = findViewById(R.id.layout1)
        layout_2 = findViewById(R.id.layout2)
        sendButton = findViewById(R.id.sendButton)
        messageArea = findViewById(R.id.messageArea)
        scrollView = findViewById(R.id.scrollView)

        var prefs: SharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        var uid=prefs.getString("uid","defoult")
        var intent = getIntent()
        var uidc = intent.getStringExtra("uidc")

        Log.e("@@chat",uid+"and"+uidc)



        Firebase.setAndroidContext(this)

        reference1 =
            Firebase("https://padosi-cef2f.firebaseio.com//messages/" + uid + "_" + uidc)
        reference2 =
            Firebase("https://padosi-cef2f.firebaseio.com//messages/" + uidc + "_" + uid)


        sendButton.setOnClickListener {

            val messageText = messageArea.text.toString()

            if (messageText != "") {
                val map: MutableMap<String, String> =
                    HashMap()
                map["message"] = messageText
                map["user"] = uid.toString()
                reference1!!.push().setValue(map)
                reference2!!.push().setValue(map)
                messageArea.setText("")
            } else {
                Toast.makeText(this@Chat, "Enter text", Toast.LENGTH_SHORT).show()
            }
        }

        reference1!!.addChildEventListener(object: ChildEventListener
        {
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val map: Map<*, *> =
                    p0?.getValue(Map::class.java)!!

                val message = map["message"].toString()
                val userName = map["user"].toString()

                if (userName == uid.toString()) {
                    addMessageBox(message, 1)
                } else {
                    addMessageBox(message, 2)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(p0: FirebaseError?) {

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addMessageBox(message: String?, type: Int) {
        val textView = TextView(this@Chat)
        val imageView = ImageView(this@Chat)
        imageView.setImageResource(R.drawable.ic_launcher_background)
        imageView.maxHeight = 25
        imageView.maxWidth = 25
        textView.text = message
        textView.textSize = 25f
        val lp2 = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp2.weight = 7.0f
        if (type == 1) {
            lp2.gravity = Gravity.RIGHT
            textView.setBackgroundResource(R.drawable.rounded_corner1)
            textView.setPadding(25, 25, 25, 25)
        } else {
            lp2.gravity = Gravity.LEFT
            textView.setBackgroundResource(R.drawable.rounded_corner2)
            textView.setPadding(35, 35, 35, 35)
        }
        textView.layoutParams = lp2
        imageView.layoutParams = lp2
        layout!!.addView(textView)
        layout!!.addView(imageView)
        scrollView!!.fullScroll(View.FOCUS_DOWN)
    }
}
