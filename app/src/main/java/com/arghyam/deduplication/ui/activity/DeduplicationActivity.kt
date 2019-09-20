package com.arghyam.deduplication.ui.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.BuildConfig
import com.arghyam.R
import com.arghyam.addspring.ui.NewSpringActivity
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.deduplication.`interface`.DeduplicationInterface
import com.arghyam.deduplication.adapter.DeduplicationAdapter
import com.arghyam.deduplication.model.*
import com.arghyam.deduplication.repository.DeduplicationRepository
import com.arghyam.iam.model.Params
import com.arghyam.iam.model.RequestModel
import com.arghyam.iam.model.ResponseModel
import com.arghyam.landing.model.AllPrivateSpringModel
import com.arghyam.landing.model.PrivateSpringsModel
import com.arghyam.landing.repository.PrivateAccessRepository
import com.arghyam.landing.viewmodel.PrivateAccessViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_deduplication.*
import kotlinx.android.synthetic.main.content_deduplication.*
import javax.inject.Inject


class DeduplicationActivity : AppCompatActivity() {

    @Inject
    lateinit var deduplicationRepository: DeduplicationRepository
    private var deduplicationViewModel: DeduplicationViewModel? = null
    private var deduplicationSpringsList = ArrayList<DeduplicationDataModel>()
    private lateinit var adapter: DeduplicationAdapter
    var TAG = "DeduplicationActivity"
    lateinit var mlocation: Location

    @Inject
    lateinit var privateAccessRepository: PrivateAccessRepository
    private var privateAccessViewModel: PrivateAccessViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deduplication)
        init()
    }

    private fun init() {
        initComponent()
        initToolBar()
        initSnackBar()
        initRepository()
        observeData()
        sendRequest()
        initFAB()
    }

    private fun initFAB() {
        floatingActionButton.setOnClickListener {
            this.startActivity(Intent(this, NewSpringActivity::class.java))
        }
    }

    private fun initSnackBar() {
        val builder = SpannableStringBuilder()
        builder.append("Click ").append(" ")
        builder.setSpan(ImageSpan(this, R.drawable.ic_add_circle), builder.length - 1, builder.length, 0)
        builder.append(" to continue adding the new spring")
        val snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), builder, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun observeData() {
        deduplicationViewModel?.getDeduplicationData?.observe(this, Observer {
            Log.d("Deduplication Activity", "Success")
            saveDeduplicationData(it)
        })

        deduplicationViewModel?.getDeduplicationError?.observe(this, Observer {
            Log.d("Deduplication Activity", "Api Error")
        })

        //Private Springs Observers
        privateAccessViewModel?.privateAccessData?.observe(this, Observer {
            Log.d("Private Spring ","Access Request "+"Success")
        })

        privateAccessViewModel?.privateAccessError?.observe(this, Observer {
            Log.d("Private Spring", "Access Request "+"Api Error")
        })
    }

    private fun privateAccessRequest(springCode: String, userId: String) {
        var privateAccessObject = RequestModel(
            id = Constants.GET_ALL_SPRINGS_ID,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = PrivateSpringsModel(
                privateSpring = AllPrivateSpringModel(
                    springCode = springCode,
                    userId = userId
                )
            )
        )
        privateAccessViewModel?.privateAccessApi(privateAccessObject)
    }

    private fun saveDeduplicationData(responseModel: ResponseModel?) {
        var responseData: DeduplicationModel = Gson().fromJson(
            responseModel?.response?.responseObject?.let { ArghyamUtils().convertToString(it) },
            object : TypeToken<DeduplicationModel>() {}.type
        )
        initRecyclerView(responseData)
    }

    private fun initComponent() {
        (application as ArghyamApplication).getmAppComponent()?.inject(this)
        mlocation = intent.getParcelableExtra("location")
    }

    private fun initToolBar() {
        setSupportActionBar(deduplication_custom_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nearby Springs"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initRecyclerView(responseData: DeduplicationModel) {
        deduplicationRecyclerView.layoutManager = LinearLayoutManager(this)
        deduplicationSpringsList.addAll(responseData.springs)
        adapter = DeduplicationAdapter(deduplicationSpringsList, this, deduplicationInterface)
        deduplicationRecyclerView.adapter = adapter
        progressBar.visibility = GONE
    }

    private fun sendRequest() {
        var userId = SharedPreferenceFactory(this).getString(Constants.USER_ID)!!

        val mRequestData = RequestModel(
            id = Constants.CREATE_STATE,
            ver = BuildConfig.VER,
            ets = BuildConfig.ETS,
            params = Params(
                did = "",
                key = "",
                msgid = ""
            ),
            request = DeduplicationRequestModel(
                location = DeduplicationRequest(
                    latitude = mlocation.latitude,
                    longitude = mlocation.longitude,
                    accuracy = mlocation.accuracy.toDouble()
                )
            )
        )
        makeApiCall(mRequestData,userId)
    }

    private fun makeApiCall(mRequestData: RequestModel,userId: String) {
        deduplicationViewModel?.deduplicationSpringsApi(this,userId, mRequestData)
    }

    private fun initRepository() {
        deduplicationViewModel = ViewModelProviders.of(this).get(DeduplicationViewModel::class.java)
        deduplicationViewModel?.setDeduplicationRepository(deduplicationRepository)

        //Private Access
        privateAccessViewModel = ViewModelProviders.of(this).get(PrivateAccessViewModel::class.java)
        privateAccessViewModel?.setPrivateAccessRepositoryRepository(privateAccessRepository)
    }

    private var deduplicationInterface: DeduplicationInterface = object : DeduplicationInterface {
        override fun onRequestAccess(springCode: String, userId: String) {
            Log.e("HomeFragment", "$springCode           $userId")
            privateAccessRequest(springCode,userId)
        }
    }
}
