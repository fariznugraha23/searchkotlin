package com.dedykuncoro.searchviewmysql.ui

import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.util.Log
import android.widget.ListView
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.dedykuncoro.searchviewmysql.adapter.Adapter
import com.dedykuncoro.searchviewmysql.api.ApiClient
import com.dedykuncoro.searchviewmysql.app.AppController
import com.dedykuncoro.searchviewmysql.model.DataModel

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap
import android.view.View
import android.widget.AdapterView
import com.dedykuncoro.searchviewmysql.R


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    internal var listData: MutableList<DataModel> = ArrayList()
    internal lateinit var adapter: Adapter
    internal lateinit var swipe: SwipeRefreshLayout
    internal lateinit var list_view: ListView

    internal var tag_json_obj = "json_obj_req"

    companion object {
        val url_data = ApiClient.URL + "data.php"
        val url_cari = ApiClient.URL + "cari_data.php"

        private val TAG = MainActivity::class.java!!.getSimpleName()

        val TAG_ID = "id"
        val TAG_noPerkara = "noPerkara"
        val TAG_RESULTS = "results"
        val TAG_MESSAGE = "message"
        val TAG_VALUE = "value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipe = findViewById<View>(R.id.swipe_refresh) as SwipeRefreshLayout
        list_view = findViewById<View>(R.id.list_view) as ListView

        adapter = Adapter(applicationContext, listData)
        list_view.adapter = adapter

        list_view.setOnItemClickListener(object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // TODO Auto-generated method stub
                Toast.makeText(applicationContext, listData.get(position).noPerkara, Toast.LENGTH_SHORT).show()
            }
        })

        swipe.setOnRefreshListener(this)

        swipe.post {
            swipe.isRefreshing = true
            callData()
        }

    }

    override fun onRefresh() {
        callData()
    }

    private fun callData() {
        listData.clear()
        adapter.notifyDataSetChanged()
        swipe.isRefreshing = true

        // Creating volley request obj
        val jArr = JsonArrayRequest(url_data, Response.Listener<JSONArray> { response ->
            Log.e(TAG, response.toString())

            // Parsing json
            for (i in 0..response.length() - 1) {
                try {
                    val obj = response.getJSONObject(i)

                    val item = DataModel()

                    item.id = obj.getString(TAG_ID)
                    item.noPerkara = obj.getString(TAG_noPerkara)

                    listData.add(item)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            adapter.notifyDataSetChanged()
            swipe.isRefreshing = false
        }, Response.ErrorListener { error ->
            VolleyLog.e(TAG, "Error: " + error.message)
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            swipe.isRefreshing = false
        })

        // Adding request to request queue
        AppController.instance!!.addToRequestQueue(jArr)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        cariData(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.queryHint = getString(R.string.type_name)
        searchView.isIconified = true
        searchView.setOnQueryTextListener(this)
        return true
    }

    private fun cariData(keyword: String) {
        swipe.isRefreshing = true

        val strReq = object : StringRequest(Request.Method.POST, url_cari, Response.Listener<String> { response ->
            Log.e("Response: ", response.toString())

            try {
                val jObj = JSONObject(response)

                val value = jObj.getInt(TAG_VALUE)

                if (value == 1) {
                    listData.clear()
                    adapter.notifyDataSetChanged()

                    val getObject = jObj.getString(TAG_RESULTS)
                    val jsonArray = JSONArray(getObject)

                    for (i in 0..jsonArray.length() - 1) {
                        val obj = jsonArray.getJSONObject(i)

                        val data = DataModel()

                        data.id = obj.getString(TAG_ID)
                        data.noPerkara = obj.getString(TAG_noPerkara)

                        listData.add(data)
                    }

                } else {
                    Toast.makeText(applicationContext, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show()
                }

            } catch (e: JSONException) {
                // JSON error
                e.printStackTrace()
            }

            adapter.notifyDataSetChanged()
            swipe.isRefreshing = false
        }, Response.ErrorListener { error ->
            VolleyLog.e(TAG, "Error: " + error.message)
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            swipe.isRefreshing = false
        }) {

            override fun getParams(): Map<String, String> {
                // Posting parameters to login url
                val params = HashMap<String, String>()
                params.put("keyword", keyword)

                return params
            }

        }

        AppController.instance!!.addToRequestQueue(strReq, tag_json_obj)
    }

}