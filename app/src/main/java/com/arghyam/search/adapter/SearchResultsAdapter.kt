package com.arghyam.search.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_spring.view.*

class SearchResultsAdapter(val springList: ArrayList<AllSpringDataModel>, val context: Context) :
	    RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return springList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val springs: AllSpringDataModel = springList[position]
        holder.springNameText.text = springs.springName
        holder.springCode.text = springs.springCode
        holder.ownership.text = springs.ownershipType
        val string = springs.address
        val parts = string.split("|")
        var address = parts[parts.size-1]+", "+parts[0]
        holder.locationText.text = address.trim()
        Glide.with(context)
            .load(springs.images[0])
            .into(holder.springImage)
        holder.springBody.setOnClickListener(View.OnClickListener {
            var dataIntent = Intent(context, SpringDetailsActivity::class.java)
            dataIntent.putExtra("SpringCode", springs.springCode)
            dataIntent.putExtra("SpringName", springs.springName)
            context.startActivity(dataIntent)
            return@OnClickListener
        })

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val springNameText: TextView = view.spring_name
        val locationText: TextView = view.location
        val springImage: ImageView = view.img_spring
        val springBody: LinearLayout = view.spring_body
        val springCode: TextView = view.spring_code
        val ownership: TextView = view.ownership_value

    }
}