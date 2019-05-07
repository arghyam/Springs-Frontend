package com.arghyam.example.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.ArghyamApplication
import com.arghyam.R
import com.arghyam.example.adapters.ExampleViewAdapter
import com.arghyam.example.repository.ExampleRepository
import com.arghyam.example.viewmodel.ExampleViewModel
import kotlinx.android.synthetic.main.activity_example.*
import javax.inject.Inject

class ExampleActivity : AppCompatActivity() {


    @Inject lateinit var exampleRepository: ExampleRepository

    private var exampleViewModel: ExampleViewModel? = null

    private var images: List<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        (application as ArghyamApplication).getmAppComponent()?.inject(this)
        init()
    }

    private fun init() {
        initRepository()
        initRecyclerView()
        initApiCalls()
    }

    private fun initRecyclerView() {
        exampleRecyclerView.layoutManager = LinearLayoutManager(this)
        exampleRecyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initApiCalls() {
        exampleViewModel?.getImagesApi()
        exampleViewModel?.getImages()?.observe( this, Observer {
            images = it.message.subList(0, 20)
            exampleRecyclerView.adapter = ExampleViewAdapter(images, this)
        })
        exampleViewModel?.getImagesError()?.observe(this, Observer {
            Log.e("error", it)
        })
    }

    private fun initRepository() {
        exampleViewModel = ViewModelProviders.of(this).get(ExampleViewModel::class.java!!)
        exampleViewModel?.setExampleRepository(exampleRepository)
    }
}

