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
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.springdetails.adapter.DischargeDataAdapter
import com.arghyam.springdetails.models.*
import com.arghyam.springdetails.repository.SpringDetailsRepository
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.arghyam.springdetails.viewmodel.SpringDetailsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    var springCode: String? = null

    lateinit var intent: Intent
    private var springName: String? = null


    private var dischargeData: ArrayList<SpringProfileResponse> = ArrayList()

    private var dischargeModeList: ArrayList<DischargeDataModal> = ArrayList()

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
        initRecyclerView()

        initSpringDetails()

        initSpringDetailsResponse()
//        initDischargeData()
        initDischargeDataButton()
    }

    private fun initRecyclerView() {
        discharge_data_recyclerView.layoutManager = LinearLayoutManager(activity)
        discharge_data_recyclerView.adapter = context?.let { DischargeDataAdapter(dischargeModeList, it) }
    }

    private fun initSpringDetailsResponse() {
        springDetailsViewModel?.getSpringDetailsResponse()?.observe(this, Observer {
            //            initDischargeAdapter(it)
            saveSpringDetailsData(it)
            if (springDetailsViewModel?.getSpringDetailsResponse()?.hasObservers()!!) {
                springDetailsViewModel?.getSpringDetailsResponse()?.removeObservers(this)
            }
        })
        springDetailsViewModel?.getSpringError()?.observe(this, Observer {
            Log.e("stefy error", it)
        })

        springDetailsViewModel?.getSpringFailure()?.observe(this, Observer {
            Log.e("stefy===", it)
        })
    }

    private fun saveSpringDetailsData(responseModel: ResponseModel) {

        var springProfileResponse: SpringProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<SpringProfileResponse>() {}.type
        )

        initDischargeData(springProfileResponse)

        dischargeSample(springProfileResponse)
    }

    private fun initDischargeAdapter(responseModel: ResponseModel) {
        Log.d("responseCheck", responseModel.response.responseObject.toString())
        var springProfileResponse: SpringProfileResponse = Gson().fromJson(
            ArghyamUtils().convertToString(responseModel.response.responseObject),
            object : TypeToken<SpringProfileResponse>() {}.type
        )

        val adapter = activity?.let { DischargeDataAdapter(dischargeModeList, it) }
        initDischargeData(springProfileResponse)


    }

    private fun initComponent() {

        intent = activity?.intent!!

        springCode = intent.getStringExtra("SpringCode")


        Log.d("Anirudh", "" + springCode)
        (activity!!.application as ArghyamApplication).getmAppComponent()?.inject(this)
    }

    private fun dischargeSample(springProfileResponse: SpringProfileResponse): ArrayList<DischargeData> {
        return springProfileResponse.extraInformation.dischargeData
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

            } else {
                val intent = Intent(context, AddDischargeActivity::class.java)
                intent.putExtra("springCode", springName)
                intent.putExtra("SpringCode", springCode)
                Log.e("Code in details", springCode)
                startActivity(intent)
            }
        }
    }

    private fun initSpringDetails() {

        Log.e("SpringCode", "Spring " + springCode)
        var springDetailObject = RequestModel(
            id = Constants.GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = RequestSpringDetailsDataModel(
                springs = SpringDetailsModel(
                    springCode = springCode
                )
            )
        )
        springDetailsViewModel?.springDetailsApi(context!!, springDetailObject)

    }

    private fun initDischargeData(springProfileResponse: SpringProfileResponse) {

        springName = "${springProfileResponse.springName}"

        for (dischargeCount in springProfileResponse.extraInformation.dischargeData) {
            if (springProfileResponse.extraInformation.dischargeData.size > 0)
                dischargeModeList.add(
                    DischargeDataModal(
                        ArghyamUtils().epochToDate(dischargeCount.createdTimeStamp),
                        dischargeCount.litresPerSecond[0].toString(),
                        dischargeCount.submittedby,
                        dischargeCount.status
                    )
                )
            discharge_data_recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun initRepository() {
        springDetailsViewModel = ViewModelProviders.of(this).get(SpringDetailsViewModel::class.java)
        springDetailsViewModel?.setSpringDetailsRepository(springDetailsRepository)
    }

}
