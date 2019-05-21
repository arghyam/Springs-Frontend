package com.arghyam.springdetails.adapter

import androidx.viewpager.widget.ViewPager
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide


class ImageAdapter(private var mContext: Context?, private var sliderImageId: ArrayList<String>) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(mContext)
        imageView.scaleType = ScaleType.CENTER_CROP
        Glide.with(mContext!!)
            .load(sliderImageId[position])
            .into(imageView)
        (container as ViewPager).addView(imageView, 0)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as ImageView)
    }

    override fun getCount(): Int {
        return sliderImageId.size
    }
}