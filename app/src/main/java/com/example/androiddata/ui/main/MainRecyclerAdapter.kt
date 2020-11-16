package com.example.androiddata.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androiddata.R
import com.example.androiddata.data.Monster

// Receives data and applies to each item in recycler view
// Generic notation is <MainRecyclerAdapter.ViewHolder>()>.
// ViewHolder() is inner class of adapter itself
class MainRecyclerAdapter(val context: Context,
                          val monsters: List<Monster>,
                          // register fragment as a listener
                          val itemListener: MonsterItemListener):
    RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>()
{

    // inner class because it is inside of other class
    //one of jobs of ViewHolder is to contain references to its child views
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val monsterImage: ImageView = itemView.findViewById(R.id.monsterImage)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    // return value can be assigned to the fun
    override fun getItemCount() = monsters.size

    // creates layout view, parent is the ViewGroup at the root of the Layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.monster_grid_item, parent, false)
        return ViewHolder(view)
    }

    // binds data to the ViewHolder, holder reference passed in and will be called for each item in the grid
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get single data out of the collection using the [position] index
        val monster = monsters[position]
        // assign values to each of the objects in the holder
        // with(holder) for referencing holder multiple times
        with(holder) {
            // let for referencing multiple parameters of object
            nameText.let {
                it.text = monster.monsterName
                it.contentDescription = monster.monsterName
            }
            ratingBar.rating = monster.scariness.toFloat()
            // sets the image monster.thumbnailUrl into monsterImage
            Glide.with(context)
                .load(monster.thumbnailUrl)
                .into(monsterImage)
            // handles event of clicking one of the items
            // itemView is the root element of the Layout
            holder.itemView.setOnClickListener {
                // sending selected item monster to the fragment
                itemListener.onMonsterItemClick(monster)
            }
        }
    }

    // click event is handled by RecyclerAdapter, but action is taked by activity or fragment
    // eventual action after clicking on recycler item must be taken by activity or fragment
    interface MonsterItemListener {
        fun onMonsterItemClick(monster: Monster)
    }

}