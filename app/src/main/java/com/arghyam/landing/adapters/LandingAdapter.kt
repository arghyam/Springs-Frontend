package com.arghyam.landing.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import com.arghyam.landing.interfaces.HomeFragmentInterface
import com.arghyam.landing.model.AllSpringDataModel
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_spring.view.*


class LandingAdapter(
    val springList: ArrayList<AllSpringDataModel>,
    val context: Context,
    val homeFragmentInterface: HomeFragmentInterface,
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
        holder.setIsRecyclable(false)
        Glide.with(context)
            .load(springs.images[0])
            .into(holder.springImage)
        holder.springBody.setOnClickListener(View.OnClickListener {
            if (springs.ownershipType == "Private" && !springs.privateAccess) {
                ArghyamUtils().makeSnackbar(
                    it,
                    "This is a private spring. Please request access to view the spring",
                    "",
                    context,
                    LandingActivity::class.java
                )
            } else {
                val dataIntent = Intent(context, SpringDetailsActivity::class.java)
                dataIntent.putExtra("SpringCode", springs.springCode)
                dataIntent.putExtra("SpringName", springs.springName)
                context.startActivity(dataIntent)
            }
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
                dataIntent.putExtra("SpringName", springs.springName)
                context.startActivity(dataIntent)
            }
            return@OnClickListener
        })

        if (springs.requested && !springs.privateAccess ) {
            holder.requestAccessTextView.setTextColor(context.resources.getColor(R.color.grey))
            holder.requestAccessTextView.text = "Request Sent"
            holder.requestAccess.isClickable = false
            holder.requestAccessTextView.isClickable = false
        }
        if (springs.ownershipType == "Private" && !springs.privateAccess) {
            holder.dischargeLayout.visibility = View.GONE
            holder.requestAccess.visibility = View.VISIBLE
        } else {
            holder.dischargeLayout.visibility = View.VISIBLE
            holder.requestAccess.visibility = View.GONE
        }

        holder.requestAccess.setOnClickListener(View.OnClickListener {
            if (SharedPreferenceFactory(context).getString(Constants.ACCESS_TOKEN) == "") {
                ArghyamUtils().makeSnackbar(
                    holder.springItemADD,
                    "SignIn To Continue",
                    "SIGN IN",
                    context,
                    LoginActivity::class.java
                )
            } else if (springs.requested) {
                ArghyamUtils().makeSnackbar(
                    holder.springItemADD,
                    "You request has already been sent to the owner",
                    "",
                    context,
                    LoginActivity::class.java
                )
            } else if (!springs.requested) {
                showDialogBox(holder, it)
            }
            return@OnClickListener
        })

        holder.favourite.setOnClickListener {
            SharedPreferenceFactory(context).getString(Constants.USER_ID)?.let { it1 ->
                homeFragmentInterface.onFavouritesItemClickListener(
                    springs.springCode,
                    it1,
                    position
                )
            }
        }
        holder.ownership.text = springs.ownershipType
        holder.springCode.text = springs.springCode
        Log.e("Landing adapter", springs.isFavSelected.toString())
        if (SharedPreferenceFactory(context).getString(Constants.ACCESS_TOKEN).isNullOrEmpty()) {
            holder.favourite.visibility = GONE
        } else {
            if (springs.isFavSelected) {
                holder.favourite.setImageResource(R.drawable.ic_fav_fill)
            } else if (!springs.isFavSelected) {
                holder.favourite.setImageResource(R.drawable.ic_fav)
            }
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
        val requestAccess: LinearLayout = view.request_access_layout
        val requestAccessTextView: TextView = view.tv_request_access
        val dischargeLayout: LinearLayout = view.add_discharge_layout
    }

    private fun showDialogBox(holder: ViewHolder, it: View) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("To view details and add discharge data, spring owner's permission required")
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, which ->
                ArghyamUtils().makeSnackbar(
                    it,
                    "Your request has been sent to the owner",
                    "",
                    context,
                    LandingActivity::class.java
                )
                holder.requestAccessTextView.setTextColor(context.resources.getColor(R.color.grey))
                holder.requestAccessTextView.text = "Request Sent"
                holder.requestAccess.isClickable = false
                holder.requestAccessTextView.isClickable = false
                SharedPreferenceFactory(context).getString(Constants.USER_ID)?.let { it1 ->
                    homeFragmentInterface.onRequestAccess(holder.springCode.text.toString(), it1)
                }
                dialog.cancel()

            }
        val alert = dialogBuilder.create()
        alert.setTitle("Permission Needed")
        alert.show()

    }
}