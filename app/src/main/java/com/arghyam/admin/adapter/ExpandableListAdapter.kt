package com.arghyam.admin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.arghyam.R
import com.arghyam.admin.interfaces.AdminPanelInterface



class ExpandableListAdapter : BaseExpandableListAdapter {

    private var context: Context
    private var user: ArrayList<User>
    private var adminPanelInterface: AdminPanelInterface

    constructor(
        context: Context,
        user: ArrayList<User>,
        adminPanelInterface: AdminPanelInterface
    ) : super() {
        this.context = context
        this.user = user
        this.adminPanelInterface = adminPanelInterface
    }

    override fun getGroup(groupPosition: Int): Any {
        return user[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var username: String? = (getGroup(groupPosition) as com.arghyam.admin.ui.User).username
        var phoneNumber: String? = (getGroup(groupPosition) as com.arghyam.admin.ui.User).phoneNumber
        var admin: Boolean = (getGroup(groupPosition) as com.arghyam.admin.ui.User).admin
        var reviewer: Boolean = (getGroup(groupPosition) as com.arghyam.admin.ui.User).reviewer
        var id: String? = (getGroup(groupPosition) as com.arghyam.admin.ui.User).id
        var convertView = convertView
        if (convertView == null) {
            val inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_header, null)
        }
        var userName = convertView!!.findViewById<TextView>(R.id.user_name)
        var phonenumber = convertView!!.findViewById<TextView>(R.id.phone_number)
        var adminRole = convertView!!.findViewById<TextView>(R.id.admin_role)
        var reviewerRole = convertView!!.findViewById<TextView>(R.id.reviewer_role)
        var role_layout = convertView!!.findViewById<LinearLayout>(R.id.role)
        var text_breaker = convertView!!.findViewById<TextView>(R.id.breaker)


        userName.text = username
        phonenumber.text = phoneNumber

        if (admin && reviewer) {
            text_breaker.visibility = View.VISIBLE
            role_layout.visibility = View.VISIBLE
            Log.e("role", " 2 visible")
            adminRole.visibility = View.VISIBLE
            reviewerRole.visibility = View.VISIBLE

        } else if (admin || reviewer) {
            text_breaker.visibility = View.GONE
            role_layout.visibility = View.VISIBLE
            if (admin) {
                Log.e("role admin", "visible")
                adminRole.visibility = View.VISIBLE
                reviewerRole.visibility = View.GONE

            } else {
                Log.e("role review", "visible")
                reviewerRole.visibility = View.VISIBLE
                adminRole.visibility = View.GONE
            }
        } else {
            text_breaker.visibility = View.GONE

            role_layout.visibility = View.GONE

            Log.e("role", "invisible")
            adminRole.visibility = View.GONE
            reviewerRole.visibility = View.GONE
        }

        if (isExpanded) {
            val indicator = convertView.findViewById<ImageView>(R.id.viewIndicator)
            indicator.setImageResource(R.drawable.up)
        } else {
            val indicator = convertView.findViewById<ImageView>(R.id.viewIndicator)
            indicator.setImageResource(R.drawable.down)
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return user.get(groupPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView: View?
        Log.e("Adapter", (getGroup(groupPosition) as com.arghyam.admin.ui.User).username)
            val inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.list_item, null)
            val adminCheckBox = convertView.findViewById<CheckBox>(R.id.admin_checkbox)
            if((getGroup(groupPosition) as com.arghyam.admin.ui.User).admin){
                adminCheckBox.isChecked = true
            }
            adminCheckBox.setOnClickListener {
                (getGroup(groupPosition) as com.arghyam.admin.ui.User).id?.let { it1 ->
                    adminPanelInterface.onCheckBoxListener(
                        "admin",
                        it1
                    )
                }
            }
            val reviewerCheckBox = convertView.findViewById<CheckBox>(R.id.reviewer_checkbox)
            if ((getGroup(groupPosition) as com.arghyam.admin.ui.User).reviewer){
                reviewerCheckBox.isChecked = true
            }
            reviewerCheckBox.setOnClickListener {
                (getGroup(groupPosition) as com.arghyam.admin.ui.User).id?.let { it1 ->
                    adminPanelInterface.onCheckBoxListener(
                        "reviewer",
                        it1
                    )
                }
            }
        return convertView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return user.size
    }

    fun filterList(filteredNames: ArrayList<User>) {
        this.user = filteredNames
        notifyDataSetChanged()
    }

}

data class User(
    var userName: String,
    var phoneNumber: String,
    var role: List<String>?,
    var id: String
)