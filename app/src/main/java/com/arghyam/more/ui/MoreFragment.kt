package com.arghyam.more.ui


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

import com.arghyam.R
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.myactivity.ui.MyActivityFragment
import com.bumptech.glide.Glide.init
import kotlinx.android.synthetic.main.content_more.*
import kotlinx.android.synthetic.main.content_more.view.*

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

        sign_in_button.setOnClickListener {
            startActivity(Intent(activity!!, LoginActivity::class.java))
            activity!!.finish()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_more, container, false)
        if (SharedPreferenceFactory(activity!!).getString(Constants.ACCESS_TOKEN) == "") {
            rootView.user_details.visibility = GONE
            rootView.sign_out.visibility = GONE
            rootView.sign_in_for_guest.visibility = VISIBLE
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }


}
