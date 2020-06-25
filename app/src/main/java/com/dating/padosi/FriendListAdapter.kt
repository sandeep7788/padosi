package com.dating.padosi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dating.padosi.FriendListAdapter.viewHolder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendListAdapter(var context: Context, var f_list:ArrayList<F_Model>) : RecyclerView.Adapter<viewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        Log.e("@@fclass","data")
        val v =LayoutInflater.from(parent.context).inflate(R.layout.activity_friend_list_adapter,parent,false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return f_list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
//        var user=f_list.get(position).name
        val prefs: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.getString("uid","defoult")

        Log.e("@@fa","data"+f_list.get(position).name+"image"+f_list.get(position).image+"uid"+f_list.get(position).uid)
        holder.t1.setText(f_list.get(position).name)
        Picasso.with(context).load(f_list.get(position).image).error(R.drawable.myaccount)
            .into(holder.image)
        holder.l1.setOnClickListener {
            var intent =Intent(context,Chat::class.java)
            intent.putExtra("uidc",f_list.get(position).uid)
            Log.e("@@fauid",f_list.get(position).uid)
            context.startActivity(intent)
        }
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val t1=itemView.findViewById<TextView>(R.id.textView)
        val image=itemView.findViewById<CircleImageView>(R.id.image)
        val l1=itemView.findViewById<LinearLayout>(R.id.l1)
    }
}
