package com.arghyam.springdetails.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.springdetails.adapter.DischargeDataAdapter
import com.arghyam.springdetails.models.DischargeDataModal
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import kotlinx.android.synthetic.main.discharge_data.*
import kotlinx.android.synthetic.main.fragment_discharge_data.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 *
 */
class DischargeDataFragment : Fragment() {

    @Inject
    lateinit var springDetailsRepository: SpringDetailsRepository

    private var springDetailsViewModel: SpringDetailsViewModel? = null

    private var dischargeData: ArrayList<DischargeDataModal> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discharge_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initComponent()
        initRepository()
        initSpringDetailsResponse()
        initRecyclerView()
        initDischargeData()
        initDischargeDataButton()
    }

    private fun initRecyclerView() {
        discharge_data_recyclerView.layoutManager = LinearLayoutManager(activity)
        discharge_data_recyclerView.adapter = context?.let { DischargeDataAdapter(dischargeData, it) }
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {

            if (springDetailsViewModel?.getSpringDetailsResponse()?.hasObservers()!!) {
                springDetailsViewModel?.getSpringDetailsResponse()?.removeObservers(this)
            }
        })
        springDetailsViewModel?.getSpringError()?.observe(this, Observer {
            Log.e("stefy error", it)
        })
    }

    private fun initComponent() {
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun initDischargeDataButton() {
        dischargeDataButton.setOnClickListener {
            if (SharedPreferenceFactory(activity as SpringDetailsActivity).getString(Constants.ACCESS_TOKEN) == "") {
                view?.let { it1 ->
                    ArghyamUtils().makeSnackbar(
                        it1,
                        "SignIn to continue",
                        "SIGN In",
                        activity,
                        LoginActivity::class.java
                    )
                }

            } else
                activity?.startActivity(Intent(activity, AddDischargeActivity::class.java))
        }
    }

    private fun initDischargeData() {
        dischargeData.add(DischargeDataModal("12/11/1995", "30", "karthik", true))
        dischargeData.add(DischargeDataModal("17/08/1996", "63", "Sean Paul", false))
        dischargeData.add(DischargeDataModal("15/07/1996", "30.55", "Enrique iglesius", true))
        dischargeData.add(DischargeDataModal("12/11/1996", "30.44", "Pitbull", false))
        dischargeData.add(DischargeDataModal("12/11/1996", "30.23", "karthik", true))
        discharge_data_recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)
    }

}
