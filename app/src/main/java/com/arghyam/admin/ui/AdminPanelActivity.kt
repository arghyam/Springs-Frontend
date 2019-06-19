package com.arghyam.admin.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import com.arghyam.R
import com.arghyam.admin.adapter.ExpandableListAdapter
import kotlinx.android.synthetic.main.activity_admin_panel.*
import kotlinx.android.synthetic.main.activity_spring_details.*
import kotlinx.android.synthetic.main.fragment_search.*

class AdminPanelActivity : AppCompatActivity() {

    private var user: ArrayList<User> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        init()
    }

    private fun init() {
        initToolBar()
        initData()
        textChangeListenerForSearchEditText()
        clearSearchEditText()
    }

    private fun clearSearchEditText() {
        user_search_input.onRightDrawableClicked {
            user_search_input.text.clear()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText && event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
            hasConsumed
        }
    }




    private fun textChangeListenerForSearchEditText() {
        user_search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.isEmpty()!!) {
                    user_search_input.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_search,0)
                } else {
                    user_search_input.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_close,0)
                }
            }

        })
    }

    private fun initData() {
        loadData()
        var listAdapter = ExpandableListAdapter(this, user as ArrayList<com.arghyam.admin.adapter.User>)
        user_list.setAdapter(listAdapter)
    }

    private fun loadData() {
        user.add(User("username1", "+91 9897969594", mutableListOf("Admin", "Reviewer")))
        user.add(User("username2", "+91 9897969594", mutableListOf("Admin")))
        user.add(User("username3", "+91 9897969594", mutableListOf("Admin")))
        user.add(User("username4", "+91 9897969594", mutableListOf("Admin", "Reviewer")))
        user.add(User("username5", "+91 9897969594", mutableListOf("Admin", "Reviewer")))
        user.add(User("username6", "+91 9897969594", mutableListOf()))
        user.add(User("username7", "+91 9897969594", mutableListOf()))
        user.add(User("username8", "+91 9897969594", mutableListOf()))
        user.add(User("username9", "+91 9897969594", mutableListOf()))
        user.add(User("username10", "+91 9897969594", mutableListOf()))
        user.add(User("username11", "+91 9897969594", mutableListOf()))
        user.add(User("username12", "+91 9897969594", mutableListOf()))
        user.add(User("username13", "+91 9897969594", mutableListOf()))


    }

    private fun initToolBar() {
        setSupportActionBar(admin_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}


data class User(var username: String, var phoneNumber: String, var role: List<String>?)