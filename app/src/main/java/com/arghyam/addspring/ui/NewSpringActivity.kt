package com.arghyam.addspring.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.arghyam.R
import com.arghyam.addspring.adapters.ImageUploaderAdapter
import com.arghyam.addspring.entities.ImageEntity
import kotlinx.android.synthetic.main.content_new_spring.*
import java.io.ByteArrayOutputStream

class NewSpringActivity : AppCompatActivity() {
    var count: Int = 0
    var imageList = ArrayList<ImageEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_spring)
        image_upload_layout.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 123)
        }
        init()
    }

    private fun init() {
        initRecyclerView()
    }


    private fun initRecyclerView() {

        imageRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ImageUploaderAdapter(imageList)
        imageRecyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            var bmp = data!!.extras.get("data") as Bitmap
            compressBitmap(bmp, 5)
            imageList.add(ImageEntity(count, bmp, "springName" + String.format("%04d", count) + ".jpg", 0));
            count++
        }
    }

    private fun compressBitmap(bmp: Bitmap, quality: Int): Bitmap {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


}
