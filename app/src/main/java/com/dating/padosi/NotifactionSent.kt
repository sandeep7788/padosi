package com.dating.padosi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject

class NotifactionSent : AppCompatActivity() {
    var TAG="@@notifaction"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifaction_sent)
        var b1:Button=findViewById(R.id.b1)

        Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()






    }

}

