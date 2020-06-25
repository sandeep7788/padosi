package com.dating.padosi

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterList(private val list: List<F_Model>) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MovieViewHolder(inflater, parent)
        Log.e("@@fc","data")
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie: F_Model = list[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = list.size

}
class MovieViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.activity_friend_list_adapter, parent, false)) {
    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null


    init {
        mTitleView = itemView.findViewById(R.id.textView)
    }

    fun bind(movie: F_Model) {
        mTitleView?.text = movie.name
    }
}
