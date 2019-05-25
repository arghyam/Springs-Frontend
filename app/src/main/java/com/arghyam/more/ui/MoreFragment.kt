package com.arghyam.more.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

import com.arghyam.R
import com.arghyam.myactivity.ui.MyActivityFragment
import com.bumptech.glide.Glide.init
import kotlinx.android.synthetic.main.content_more.*

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

    private fun init() {
       initClick()
    }

    private fun initClick() {
        edit_icon.setOnClickListener {
            rl_edit_name.visibility = GONE
            edit_name_layout.visibility = VISIBLE

        }
        save_name.setOnClickListener {
            rl_edit_name.visibility = VISIBLE
            edit_name_layout.visibility = GONE
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_more, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }


}
