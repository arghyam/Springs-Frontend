package com.arghyam.landing.adapters

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
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_spring.view.*


class LandingAdapter(val springList: ArrayList<AllSpringDataModel>, val context: Context) :
    RecyclerView.Adapter<LandingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_spring, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return springList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val springs: AllSpringDataModel = springList[position]
        holder.springNameText.text = springs.orgId
        holder.villageNameText.text = springs.village
        Glide.with(context)
            .load(springs.images[0])
            .into(holder.springImage)
        holder.springBody.setOnClickListener(View.OnClickListener {
            var dataIntent = Intent(context, SpringDetailsActivity::class.java)
            dataIntent.putExtra("SpringCode", springs.springCode)
            context.startActivity(dataIntent)

            return@OnClickListener
        })
        holder.springItemADD.setOnClickListener(View.OnClickListener {
            if (SharedPreferenceFactory(context).getString(Constants.ACCESS_TOKEN) == "") {
                ArghyamUtils().makeSnackbar(
                    holder.springItemADD,
                    "SignIn To Continue",
                    "SIGN IN",
                    context,
                    LoginActivity::class.java
                )
            } else
                context.startActivity(Intent(context, AddDischargeActivity::class.java))
            return@OnClickListener
        })
        holder.favourite.setOnClickListener {
        }
        holder.ownership.text= springs.ownershipType
        holder.springcode.text= springs.springCode
        holder.village.text= springs.village


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val springNameText: TextView = view.spring_name
        val villageNameText: TextView = view.village_name
        val springImage: ImageView = view.img_spring
        val favourite: ImageView = view.fav_icon
        val springItemADD: LinearLayout = view.springItemADD
        val springBody: LinearLayout = view.spring_body
        val ownership: TextView = view.ownership_value
        val springcode:TextView = view.springcode
        val village:TextView = view.village_name


    }
}