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
import com.arghyam.landing.model.LandingModel
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_spring.view.*

class SearchResultsAdapter(val springList: ArrayList<LandingModel>, val context: Context) :
	    RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return springList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val springs: LandingModel = springList[position]
        holder.springNameText.text = springs.springName
        holder.villageNameText.text = springs.villageName
        Glide.with(context)
            .load(springs.springImage)
            .into(holder.springImage)
        holder.springBody.setOnClickListener(View.OnClickListener {
            context.startActivity(Intent(context, SpringDetailsActivity::class.java))
            return@OnClickListener
        })




    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val springNameText: TextView = view.spring_name
        val villageNameText: TextView = view.village_name
        val springImage: ImageView = view.img_spring
        //        val favourite: ImageView = view.fav_icon
//        val springItemADD: LinearLayout = view.springItemADD
        val springBody: LinearLayout = view.spring_body

    }
}