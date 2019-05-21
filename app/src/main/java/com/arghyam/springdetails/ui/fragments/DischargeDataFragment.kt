package com.arghyam.springdetails.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.R
import com.arghyam.springdetails.adapter.DischargeDataAdapter
import com.arghyam.springdetails.models.DischargeDataModal
import kotlinx.android.synthetic.main.discharge_data.*


/**
 * A simple [Fragment] subclass.
 *
 */
class DischargeDataFragment : Fragment() {

    private var dischargeData: ArrayList<DischargeDataModal> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discharge_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initRecyclerView()
        initDischargeData()
    }

    private fun initRecyclerView() {
        discharge_data_recyclerView.layoutManager = LinearLayoutManager(activity)
        discharge_data_recyclerView.adapter = context?.let { DischargeDataAdapter(dischargeData, it) }
    }

    private fun initDischargeData() {
        dischargeData.add(DischargeDataModal("12/11/1995", "30", "karthik", true))
        dischargeData.add(DischargeDataModal("17/08/1996", "63", "Sean Paul", false))
        dischargeData.add(DischargeDataModal("15/07/1996", "30.55", "Enrique iglesius", true))
        dischargeData.add(DischargeDataModal("12/11/1996", "30.44", "Pitbull", false))
        dischargeData.add(DischargeDataModal("12/11/1996", "30.23", "karthik", true))
        discharge_data_recyclerView.adapter?.notifyDataSetChanged()
    }

}
