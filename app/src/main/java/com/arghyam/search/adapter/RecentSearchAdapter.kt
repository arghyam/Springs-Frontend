package com.arghyam.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.search.interfaces.RecentSearchInterface
import com.arghyam.search.model.RecentSearchModel
import kotlinx.android.synthetic.main.list_recent_search.view.*

class RecentSearchAdapter(val recentSearchList : ArrayList<RecentSearchModel>,val context: Context, val recentSearchInterface: RecentSearchInterface): RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val v = LayoutInflater.from(parent.context).inflate(R.layout.list_recent_search,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return recentSearchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val recentSprings: RecentSearchModel = recentSearchList[position]
        holder.recentSpringName.text = recentSprings.recentSearchName
        holder.recentSearch.setOnClickListener {
            recentSearchInterface.onRecentSearchItemClickListener(position)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val recentSpringName = view.recent_spring_name
        val recentSearch = view.recent_search_layout

    }

}