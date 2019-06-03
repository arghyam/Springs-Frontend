package com.arghyam.springdetails.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.arghyam.R
import com.arghyam.additionalDetails.ui.AddAdditionalDetailsActivity
import com.arghyam.geographySearch.ui.activity.GeographySearchActivity
import com.arghyam.springdetails.adapter.ImageAdapter
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.spring_details.*

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initClick()
        initImageAdapter()
        initAddDischargeData()
    }

    private fun initClick() {
        additional_details_layout.setOnClickListener {
            val intent = Intent(activity, AddAdditionalDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initAddDischargeData() {
        add_discharge_data.setOnClickListener {
            activity?.startActivity(Intent(activity, AddDischargeActivity::class.java))
        }
    }


    private fun initImageAdapter() {
        val adapter = activity?.let { ImageAdapter(it, imageSample()) }
        images_view_pager.addOnPageChangeListener(imageChangeListener())
        images_view_pager.adapter = adapter
    }

    private fun imageSample(): ArrayList<String> {
        var images: ArrayList<String> = ArrayList()
        images.add("https://picsum.photos/200/300")
        images.add("https://picsum.photos/300/300")
        images.add("https://picsum.photos/400/300")
        return images
    }

    private fun imageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

        }
    }

}
