package com.example.androidsample

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var currencyValue: Float? = null

    fun convertCurrency(view: View) {
        var conversionRate = currencyValue

        if(dollarText.text.isNotEmpty()) {
            val dollarValue = dollarText.text.toString().toFloat()
            val euroValue = dollarValue * conversionRate!!
            convertedCurrencyText.text = "%.2f".format(euroValue)
        } else {
            convertedCurrencyText.text = "No Value"
        }
    }

    private fun getCurrencyRate() {
        val url = "https://api.exchangeratesapi.io/latest?base=USD"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        // Callback is needed to push the network call off of
        // the main thread. Otherwise the app crashes. (Feature)
        client.newCall(request).enqueue(object : Callback {
            var mainHandler = Handler(this@MainActivity.mainLooper)
            override fun onResponse(call: Call, response: Response) {
                mainHandler.post {
                    val responseBody = response.body?.string() ?: return@post
                    val jsonObject = JSONObject(responseBody)
                    val jsonObjectRates = jsonObject.getJSONObject("rates")
                    val amount = jsonObjectRates.get("EUR").toString()
                    currencyValue = amount.toFloat()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("API execute failed")
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        getCurrencyRate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
