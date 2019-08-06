package com.arghyam.landing.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
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
        holder.springNameText.text = springs.springName
        holder.location.text = springs.location
        Glide.with(context)
            .load(springs.images[0])
            .into(holder.springImage)
        holder.springBody.setOnClickListener(View.OnClickListener {
            var dataIntent = Intent(context, SpringDetailsActivity::class.java)
            dataIntent.putExtra("SpringCode", springs.springCode)
            dataIntent.putExtra("springName", springs.springName)
            context.startActivity(dataIntent)

            return@OnClickListener
        })
        holder.springItemADD.setOnClickListener(View.OnClickListener {
            if (SharedPreferenceFactory(context).getString(Constants.ACCESS_TOKEN) == "") {
                ArghyamUtils().makeSnackbar(
                    it,
                    "Sign In to Continue",
                    "SIGN IN",
                    context,
                    LoginActivity::class.java
                )
            } else {
                var dataIntent = Intent(context, AddDischargeActivity::class.java)
                dataIntent.putExtra("SpringCode", springs.springCode)
                dataIntent.putExtra("springName", springs.springName)
                context.startActivity(dataIntent)
            }
            return@OnClickListener
        })
        holder.favourite.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (holder.favourite.drawable.constantState == context.getDrawable(R.drawable.ic_fav).constantState) {

                    holder.favourite.setImageResource(R.drawable.ic_fav_fill)
                } else {
                    holder.favourite.setImageResource(R.drawable.ic_fav)
                }
            } else {
                if (holder.favourite.drawable.constantState == context.resources.getDrawable(R.drawable.ic_fav).constantState) {
                    holder.favourite.setImageResource(R.drawable.ic_fav_fill)
                } else {
                    holder.favourite.setImageResource(R.drawable.ic_fav)

                }
            }
        }
        holder.ownership.text = springs.ownershipType
        holder.springCode.text = springs.springCode
        holder.village.text = springs.location
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val springNameText: TextView = view.spring_name
        val location: TextView = view.location
        val springImage: ImageView = view.img_spring
        val favourite: ImageView = view.fav_icon
        val springItemADD: LinearLayout = view.springItemADD
        val springBody: LinearLayout = view.spring_body
        val ownership: TextView = view.ownership_value
        val springCode: TextView = view.springcode
        val village: TextView = view.location
    }
}