package com.reisdis.musicrecognizer

import android.content.Context
import android.util.Log
import com.acrcloud.rec.ACRCloudClient
import com.acrcloud.rec.ACRCloudConfig
import com.acrcloud.rec.ACRCloudResult
import com.acrcloud.rec.IACRCloudListener
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class ACRCloudManager(private val context: Context, private val resultCallback: (RecognitionResult) -> Unit) :
    IACRCloudListener {
    private var mClient: ACRCloudClient? = null

    fun initAcrcloud() {
        val config = ACRCloudConfig()

        config.acrcloudListener = this
        config.context = context

        // Set your ACRCloud project credentials here
        config.host = "identify-ap-southeast-1.acrcloud.com"
        config.accessKey = "1eea47f40ab4a15637064e2d0bd71b73"
        config.accessSecret = "HvmRnZkqBx3WbSghMl6kwiqKGKSJIIU3ZXtQJOyj"

        config.recorderConfig.rate = 8000
        config.recorderConfig.channels = 1

        mClient = ACRCloudClient()
        mClient!!.initWithConfig(config)
    }

    fun startRecognition() {
        mClient?.let {
            if (it.startRecognize()) {
                // Recognition started
            } else {
                // Error initializing recognition
            }
        } ?: run {
            // ACRCloud client not initialized
        }
    }

    override fun onResult(acrResult: ACRCloudResult?) {
        acrResult?.let {
            // Call the callback function with the result
            Log.d("Result", acrResult.result)
            val parsed = parseResult(acrResult)
            Log.d("Result_P", parsed.toString())
            resultCallback(parsed)
        }
    }

    override fun onVolumeChanged(vol: Double) {
        // Not used in this example
    }

    private fun parseResult(acrResult: ACRCloudResult): RecognitionResult {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(RecognitionResult::class.java)

        val recognitionResult = adapter.fromJson(acrResult.result)
        return recognitionResult!!
    }
}