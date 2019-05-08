package com.arghyam.more.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arghyam.R
import com.arghyam.myactivity.ui.MyActivityFragment

/**
 * A simple [Fragment] subclass.
 *
 */
class MoreFragment : Fragment() {

    /**
     * Initialize newInstance for passing paameters
     */
    companion object {
        fun newInstance(): MoreFragment {
            var fragmentMore = MoreFragment()
            var args = Bundle()
            fragmentMore.arguments = args
            return fragmentMore
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_more, container, false)
        return rootView
    }


}
