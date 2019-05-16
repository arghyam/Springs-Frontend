package com.arghyam.search.ui


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arghyam.R
import com.arghyam.geographySearch.ui.activity.GeographySearch
import com.arghyam.more.ui.MoreFragment
import com.bumptech.glide.Glide.init
import kotlinx.android.synthetic.main.content_search.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SearchFragment : Fragment() {

    /**
     * Initialize newInstance for passing paameters
     */
    companion object {
        fun newInstance(): SearchFragment {
            var fragmentSearch = SearchFragment()
            var args = Bundle()
            fragmentSearch.arguments = args
            return fragmentSearch
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun init() {
       initClick()
    }

    private fun initClick() {
        geo_layout.setOnClickListener {
            val intent = Intent(activity, GeographySearch::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_search, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

}
