package com.arghyam.notification.adapter

import android.content.Context
import android.graphics.Color.rgb
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.arghyam.R
import com.arghyam.notification.`interface`.NotificationInterface
import com.arghyam.notification.model.NotificationDataModel
import kotlinx.android.synthetic.main.notification_listview.view.*
import kotlinx.android.synthetic.main.notification_listview.view.breaker
import kotlinx.android.synthetic.main.notification_listview.view.date
import kotlinx.android.synthetic.main.notification_listview.view.notification_data
import kotlinx.android.synthetic.main.notification_listview.view.right_arrow
import kotlinx.android.synthetic.main.notification_listview.view.time




class NotificationAdapter : ArrayAdapter<NotificationDataModel>, View.OnClickListener {

    private var notificationArray: ArrayList<NotificationDataModel>? = null
    var mContext: Context? = null
    var notificationInterface: NotificationInterface? = null

    private class ViewHolder {
        internal lateinit var notification: TextView
        internal lateinit var date: TextView
        internal lateinit var time: TextView
        internal lateinit var button: ImageView
        internal lateinit var breaker: ImageView
        internal lateinit var private: LinearLayout
        internal lateinit var accept: Button
        internal lateinit var reject: Button


    }

    constructor(
        context: Context,
        data: ArrayList<NotificationDataModel>,
        notificationInterface: NotificationInterface
    ) : super(
        context,
        R.layout.notification_listview,
        data
    ) {
        this.notificationArray = data
        this.mContext = context
        this.notificationInterface = notificationInterface
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val dataModel = getItem(position)
        val viewHolder: ViewHolder

        val result: View

        if (convertView == null) {

            viewHolder = ViewHolder()

            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.notification_listview, parent, false)

            viewHolder.notification = convertView!!.notification_data
            viewHolder.date = convertView.date
            viewHolder.time = convertView.time
            viewHolder.button = convertView.right_arrow
            viewHolder.breaker = convertView.breaker
            viewHolder.private = convertView.private_notification_layout
            viewHolder.reject = convertView.reject_private
            viewHolder.accept = convertView.accept_private

            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }


        viewHolder.notification.text = dataModel!!.notification
        setHighLightedText(viewHolder.notification, "Spring")
        viewHolder.time.text = dataModel.time
        viewHolder.date.text = dataModel.date
        viewHolder.button.setImageResource(R.drawable.ic_notif_icon)
        viewHolder.breaker.setImageResource(R.drawable.ic_break)
        if (viewHolder.notification.text.contains(" is requesting permission to view details and add discharge data for "))
            viewHolder.private.visibility = VISIBLE
        viewHolder.accept.setOnClickListener {
            notificationInterface?.onPrivateReview("Accepted",dataModel.springCode,dataModel.osid.substring(2),dataModel.userId,dataModel.requesterId)
        }
        viewHolder.reject.setOnClickListener {
            notificationInterface?.onPrivateReview("Rejected",dataModel.springCode,dataModel.osid.substring(2),dataModel.userId,dataModel.requesterId)
        }

        // Return the completed view to render on screen
        return convertView
    }

    private fun setHighLightedText(tv: TextView, textToHighlight: String) {
        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0)
        val wordToSpan = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1)
                break
            else {
                // set color here
                // set color here
                wordToSpan.setSpan(
                    ForegroundColorSpan(rgb(35, 114, 217)),
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                wordToSpan.setSpan(
                    android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }

    override fun getViewTypeCount(): Int {

        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onClick(v: View?) {
        val position = v?.tag as Int
        val spring = getItem(position)
        val dataModel = spring as NotificationDataModel
    }

}
