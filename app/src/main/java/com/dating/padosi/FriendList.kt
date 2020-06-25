package com.dating.padosi

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FriendList : AppCompatActivity() {

    val f_list = ArrayList<F_Model>()
    var mDatabase: FirebaseDatabase? = null
    var mRef: DatabaseReference? = null
    var currentuser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
        mDatabase= FirebaseDatabase.getInstance()
        mRef= mDatabase!!.getReference("users")
        currentuser = FirebaseAuth.getInstance().currentUser
        list()





        var btn_back=findViewById<ImageView>(R.id.btn_back)
        btn_back.setOnClickListener {
            finish()
        }




    }
    fun list()
    {
        var prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val uid=prefs.getString("uid","error")
        Log.e("@@uidf",uid)
        mRef?.child(uid.toString())?.child("likes")
            ?.addValueEventListener(object:ValueEventListener {
                @SuppressLint("WrongConstant")
                override fun onDataChange(p0: DataSnapshot) {

                    mutableListOf<F_Model>()
                    p0.children.forEach {
                        Log.e("@@key",it.key)
                        Log.e("@@child",it.children.toString())
                        Log.e("@@data",it.getValue().toString())

                        var user=it.getValue(F_Model::class.java)
                        Log.e("@@data", user?.image.toString())
                        Log.e("@@data", user?.name.toString())
                        Log.e("@@data",p0.key.toString())

                        if (user != null) {
                            f_list.add(user)
                        }


                    }
                    val recyclerView = findViewById(R.id.rv) as RecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(this@FriendList,LinearLayout.VERTICAL,false)
                    var adapter = FriendListAdapter(this@FriendList,f_list)
                    recyclerView.adapter = adapter
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }
}
