package com.arghyam.landing.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arghyam.R
import com.arghyam.landing.interfaces.PermissionInterface
import kotlinx.android.synthetic.main.fragment_error.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ErrorFragment : Fragment() {

    /**
     * Initialize newInstance for passing paameters
     */
    companion object {
        fun newInstance(): ErrorFragment {
            var fragmentError = ErrorFragment()
            var args = Bundle()
            fragmentError.arguments = args
            return fragmentError
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        continueButton.setOnClickListener {
            (activity as PermissionInterface).permissionClick()
        }
    }

}
