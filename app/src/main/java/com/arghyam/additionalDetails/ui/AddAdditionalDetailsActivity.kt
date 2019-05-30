package com.arghyam.additionalDetails.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arghyam.R
import com.arghyam.additionalDetails.adapter.CalenderAdapter
import kotlinx.android.synthetic.main.content_add_additional_details.*

class AddAdditionalDetailsActivity : AppCompatActivity() {

    val calender:ArrayList<String> = ArrayList()
    internal var actionMode: ActionMode? = null
    internal var selectedItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_additional_details)
        init()
    }

    private fun init() {
        initComponenet()
        addCalender()
        initRecyclerview()
        initClick()
    }

    private fun initComponenet() {
        selectedItemCount = 0
    }

    private fun initClick() {
        radioGroup_character?.setOnCheckedChangeListener{group, checkedId ->
            if(R.id.radioButton_seasonal1 == checkedId){
                calenderRecyclerview.visibility=VISIBLE
                radioButton_seasonal1?.setCompoundDrawablesWithIntrinsicBounds(null,null,resources.getDrawable(R.drawable.ic_collapse_up),null)

            } else {
                calenderRecyclerview.visibility=GONE
                radioButton_seasonal1?.setCompoundDrawablesWithIntrinsicBounds(null,null,resources.getDrawable(R.drawable.ic_collapse_down),null)

            }

        }
        initRecyclerview()
    }

    private fun initRecyclerview() {
        calenderRecyclerview.layoutManager = GridLayoutManager(this, 4) as RecyclerView.LayoutManager?
        calenderRecyclerview.adapter = CalenderAdapter(calender, this)

    }

    private fun addCalender() {
        calender.add("Jan")
        calender.add("Feb")
        calender.add("Mar")
        calender.add("Apr")
        calender.add("May")
        calender.add("Jun")
        calender.add("Jul")
        calender.add("Aug")
        calender.add("Sep")
        calender.add("Oct")
        calender.add("Nov")
        calender.add("Dec")
    }
}
