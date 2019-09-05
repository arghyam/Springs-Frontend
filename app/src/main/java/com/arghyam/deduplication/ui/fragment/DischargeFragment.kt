package com.arghyam.deduplication.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arghyam.ArghyamApplication


/**
 * A simple [Fragment] subclass.
 *
 */
class DischargeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.arghyam.R.layout.content_deduplication, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

}
