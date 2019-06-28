package com.arghyam.commons.utils

import java.io.File
import java.io.IOException

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source

class ProgressRequestBody(
    private val mMediaType: MediaType,
    private val mFile: File,
    private val mListener: ProgressListener
) : RequestBody() {

    override fun contentType(): MediaType? {
        return mMediaType
    }

    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {

        var source: Source? = null
        try {
            source = Okio.source(mFile)
            var total: Long = 0
            var read: Long
            val length = contentLength()
            read = source!!.read(sink.buffer(), SEGMENT_SIZE.toLong())
            while (read>-1) {
                total += read
                sink.flush()
                val percent = total.toFloat() / length * 100
                mListener.onProgressUpdate(percent)
            }
        } finally {
            Util.closeQuietly(source)
        }
    }

    interface ProgressListener {
        fun onProgressUpdate(percent: Float)
    }

    companion object {
        private val SEGMENT_SIZE = 2048 // okio.Segment.SIZE
    }
}
