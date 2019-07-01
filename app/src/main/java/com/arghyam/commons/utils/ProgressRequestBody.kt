package com.arghyam.commons.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ProgressRequestBody(private val mFile: File, private val mListener: UploadCallbacks) : RequestBody() {

    private var mIsStopped = false

    override fun contentType(): MediaType? {
        return MediaType.parse("image/*")
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        var uploaded: Long = 0
        var buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(mFile)

        `in`.use { `in` ->

            var read: Int = `in`.read(buffer)
            val handler = Handler(Looper.getMainLooper())
            while (read != null && uploaded != fileLength) {
                Log.e("Anirudh desc", fileLength.toString() + "     "+ uploaded.toString())
                /*if (fileLength - uploaded < DEFAULT_BUFFER_SIZE) {
                    Log.e("Anirudh arraysize else", (fileLength - uploaded).toString())
                    buffer = ByteArray((fileLength - uploaded).toInt())
                    read = `in`.read(buffer)
                }*/
                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                Log.e("Anirudh", "filelength: $fileLength uploaded $uploaded")
            }
            if (uploaded == fileLength) {
                Log.e("Anirudh", "Removed ")
                handler.removeCallbacks(ProgressUpdater(uploaded, fileLength))
            }
        }
    }

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) : Runnable {

        override fun run() {
            if (mUploaded <= mTotal) {
                mListener.onProgressUpdate((MAX_PERCENTAGE * mUploaded / mTotal).toInt())
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
        private const val MAX_PERCENTAGE = 100
    }
}