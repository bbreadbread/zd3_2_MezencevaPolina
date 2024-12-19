package com.example.mytv2
import android.graphics.Color
import com.example.mytv2.Movie
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class QuestsActivity : AppCompatActivity() {

    private val stringArray: Array<String> =
        arrayOf("Smeshariki", "Ever After High", "My little pony", "The Owl House", "We Bare Bears", "Wakfu")

    lateinit var gridLayout: GridLayout
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quests)
        gridLayout = findViewById(R.id.gridLayout)

        val itemWidth = 1800 / 3
        var rows: Int = 0
        var columns: Int = 0

        stringArray.forEach { query ->
            val request = okhttp3.Request.Builder()
                .url("https://api.kinopoisk.dev/v1.4/movie/search?page=1&limit=10&query=$query&language=ru") // Added language parameter
                .get()
                .addHeader("accept", "application/json")
                .addHeader("X-API-KEY", "A8P59HA-YQD44D3-NVRZF41-K8D8GWH")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Обработка ошибки
                    Log.e("QuestsActivity", "Failed to fetch data", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val movie = parseMovieJson(responseData)

                        runOnUiThread {
                            if (movie != null) {
                                val linearLayout = LinearLayout(this@QuestsActivity).apply {
                                    orientation = LinearLayout.VERTICAL
                                    layoutParams = GridLayout.LayoutParams().apply {
                                        rowSpec = GridLayout.spec(rows)
                                        columnSpec = GridLayout.spec(columns)
                                    }
                                    gravity = Gravity.CENTER
                                }

                                val imageView = ImageView(this@QuestsActivity).apply {
                                    layoutParams = LinearLayout.LayoutParams(300, 550)
                                    scaleType = ImageView.ScaleType.FIT_CENTER
                                    movie.poster.image?.let {
                                        Picasso.get().load(it).into(this)
                                    }
                                }
                                linearLayout.addView(imageView)

                                val titleTextView = TextView(this@QuestsActivity).apply {
                                    text = movie.title // Title in Russian
                                    setTextColor(Color.WHITE)
                                    layoutParams = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    gravity = Gravity.CENTER
                                }

                                val yearTextView = TextView(this@QuestsActivity).apply {
                                    text = movie.year.toString()
                                    setTextColor(Color.WHITE)
                                    layoutParams = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    gravity = Gravity.CENTER
                                }

                                linearLayout.addView(titleTextView)
                                linearLayout.addView(yearTextView)

                                gridLayout.addView(linearLayout)

                                columns += 1
                                if (columns == 3) {
                                    rows += 1
                                    columns = 0
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun parseMovieJson(jsonString: String?): Movie? {
        if (jsonString.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        val movieResponse = gson.fromJson(jsonString, MovieResponse::class.java)
        return movieResponse.docs.firstOrNull()
    }

    data class MovieResponse(
        @SerializedName("docs") val docs: List<Movie>
    )
}

