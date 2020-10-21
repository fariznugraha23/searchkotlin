package com.dedykuncoro.searchviewmysql.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.dedykuncoro.searchviewmysql.R
import com.dedykuncoro.searchviewmysql.model.DataModel


class Adapter(private val context: Context, private val item: List<DataModel>) : BaseAdapter() {
    private var inflater: LayoutInflater? = null

    override fun getCount(): Int {
        return item.size
    }

    override fun getItem(location: Int): Any {
        return item[location]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (inflater == null)
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (convertView == null)
            convertView = inflater!!.inflate(R.layout.list_item, null)

        val txt_noPerkara = convertView!!.findViewById<View>(R.id.txt_noPerkara) as TextView

        txt_noPerkara.text = item[position].noPerkara

        return convertView
    }
}