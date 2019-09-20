package com.arghyam.deduplication.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.deduplication.`interface`.DeduplicationInterface
import com.arghyam.deduplication.model.DeduplicationDataModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.ui.activity.LandingActivity
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.list_spring.view.*

class DeduplicationAdapter(
    private val springList: ArrayList<DeduplicationDataModel>,
    val context: Context,
    val deduplicationInterface: DeduplicationInterface
) :
    RecyclerView.Adapter<DeduplicationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_spring, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return springList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val springs: DeduplicationDataModel = springList[position]
        holder.springNameText.text = springs.springName
        val string = springs.address
        val parts = string.split("|")
        var address = parts[parts.size-1]+", "+parts[0]
        holder.addressText.text = address.trim()
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
        holder.springItemADD.setOnClickListener(View.OnClickListener {
            if (SharedPreferenceFactory(context).getString(Constants.ACCESS_TOKEN) == "") {
                ArghyamUtils().makeSnackbar(
                    it,
                    "SignIn To Continue",
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
        holder.favourite.visibility = GONE
        holder.ownership.text = springs.ownershipType
        holder.springcode.text = springs.springCode
        if (springs.ownershipType == "Private" && !springs.privateAccess) {
            holder.dischargeLayout.visibility = GONE
            holder.requestAccess.visibility = VISIBLE
        } else {
            holder.dischargeLayout.visibility = VISIBLE
            holder.requestAccess.visibility = GONE
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
            } else {
                showDialogBox(holder,it)
            }
            return@OnClickListener
        })

    }

    private fun showDialogBox(holder: ViewHolder, it:View){
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("To view details and add discharge data, spring owner's permission required")
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, which ->
                Snackbar.make(it,
                    "Your request has been sent to the owner",
                    5
                )
                holder.requestAccessTextView.setTextColor(context.resources.getColor(R.color.grey))
                holder.requestAccessTextView.text = "Request Sent"
                holder.requestAccess.isClickable = false
                holder.requestAccessTextView.isClickable = false
                SharedPreferenceFactory(context).getString(Constants.USER_ID)?.let { it1 ->
                    deduplicationInterface.onRequestAccess(holder.springcode.text.toString(), it1)
                }
                dialog.cancel()

            }
        val alert = dialogBuilder.create()
        alert.setTitle("Permission Needed")
        alert.show()

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val springNameText: TextView = view.spring_name
        val addressText: TextView = view.location
        val springImage: ImageView = view.img_spring
        val favourite: ImageView = view.fav_icon
        val springItemADD: LinearLayout = view.springItemADD
        val springBody: LinearLayout = view.spring_body
        val ownership: TextView = view.ownership_value
        val springcode: TextView = view.spring_code
        val requestAccess: LinearLayout = view.request_access_layout
        val dischargeLayout: LinearLayout = view.add_discharge_layout
        val requestAccessTextView: TextView = view.tv_request_access
    }
}
