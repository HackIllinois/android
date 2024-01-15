package org.hackillinois.android.common

import android.R
import android.content.Context
import android.widget.ImageView
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream

class ImageLoadingUtilities {
    private var httpClient: OkHttpClient? = null

    // on below line we are creating a function to load the svg from the url.
    // in below method we are specifying parameters as context,
    // url for the image and image view.
    fun fetchSVG(context: Context, url: String, target: ImageView) {
        // on below line we are checking
        // if http client is null
        if (httpClient == null) {
            // if it is null on below line
            // we are initializing our http client.
            httpClient =
                OkHttpClient.Builder().cache(Cache(context.cacheDir, 5 * 1024 * 1014) as Cache).build() as OkHttpClient
        }

        // on below line we are creating a variable for our request and initializing it.
        var request: Request = Request.Builder().url(url).build()

        // on below line we are making a call to the http request on below line.
        httpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                // we are adding a default image if we gets any
                // error while loading image from url.
                target.setImageResource(R.drawable.stat_notify_error)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                // sharp is a library which will load stream which we generated
                // from url in our target image view.
                val stream: InputStream = response.body()!!.byteStream()
//                Sharp.loadInputStream(stream).into(target)
//                stream.close()
            }
        })
    }
}
