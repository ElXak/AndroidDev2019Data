package com.example.androiddata.utilities

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.NumberFormat

// keep all binding adapters in one file

// simple text values can be displayed with data binding expressions in the layout file
// more complex assignments thou might require use of adapter functions
// this are top level functions that are associated with your layout file with property names that you assign

// annotation of BinderAdapter passing the name of attribute that you want to use to assign value for layout file
@BindingAdapter("imageUrl")
// fun requires 2 arguments, first = reference to the view object,
// second = value you can use yo assign something to the view
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view.context)
        .load(imageUrl)
        .into(view)
}

@BindingAdapter("price")
fun itemPrice(view: TextView, value: Double) {
    // formats number as currency
    val formatter = NumberFormat.getCurrencyInstance()
    // format is applied to value
    val text = "${formatter.format(value)} / each"
    view.text = text
}