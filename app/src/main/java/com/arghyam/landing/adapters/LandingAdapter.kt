package com.arghyam.landing.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.arghyam.favourites.model.FavSpringDataModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.interfaces.FavouritesInterface
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_spring.view.*


class LandingAdapter(
    val springList: ArrayList<AllSpringDataModel>,
    val context: Context,
    val favouritesInterface: FavouritesInterface,
    val favSpringsList: ArrayList<FavSpringDataModel>
) :
    RecyclerView.Adapter<LandingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_spring, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return springList.size
    }


    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val springs: AllSpringDataModel = springList[position]
        holder.springNameText.text = springs.springName
        val item = holder.adapterPosition
        val string = springs.address
        val parts = string.split("|")
        var address = parts[parts.size - 1] + ", " + parts[0]
        holder.location.text = address.trim()
        Glide.with(context)
            .load(springs.images[0])
            .into(holder.springImage)
        holder.springBody.setOnClickListener(View.OnClickListener {
            var dataIntent = Intent(context, SpringDetailsActivity::class.java)
            dataIntent.putExtra("SpringCode", springs.springCode)
            dataIntent.putExtra("springCode", springs.springName)
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
                dataIntent.putExtra("springCode", springs.springName)
                context.startActivity(dataIntent)
            }
            return@OnClickListener
        })


        holder.favourite.setOnClickListener {
            //            if (holder.favourite.drawable.constantState == context.getDrawable(R.drawable.ic_fav).constantState) {
//                holder.favourite.setImageResource(R.drawable.ic_fav_fill)
//            } else {
//                holder.favourite.setImageResource(R.drawable.ic_fav)
//            }
//            if (holder.favourite.drawable.constantState == context.getDrawable(R.drawable.ic_fav_fill).constantState)
//                holder.favourite.setImageResource(R.drawable.ic_fav)
            SharedPreferenceFactory(context).getString(Constants.USER_ID)?.let { it1 ->
                favouritesInterface.onFavouritesItemClickListener(springs.springCode, it1, position)
            }
        }
        holder.ownership.text = springs.ownershipType
        holder.springCode.text = springs.springCode
        Log.e("Landing adapter", springs.isFavSelected.toString())
        if (springs.isFavSelected) {
            holder.favourite.setImageResource(R.drawable.ic_fav_fill)
        } else if (!springs.isFavSelected) {
            holder.favourite.setImageResource(R.drawable.ic_fav)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val springNameText: TextView = view.spring_name
        val location: TextView = view.location
        val springImage: ImageView = view.img_spring
        val favourite: ImageView = view.fav_icon
        val springItemADD: LinearLayout = view.springItemADD
        val springBody: LinearLayout = view.spring_body
        val ownership: TextView = view.ownership_value
        val springCode: TextView = view.spring_code
    }
}