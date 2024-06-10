package com.capstone.signora.ui.frontend.istilah

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.signora.R

class IstilahAdapter(private val context: Context, private val gambarIsyarat: IntArray) :
    RecyclerView.Adapter<IstilahAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_istilah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(gambarIsyarat[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("POSITION", position)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return gambarIsyarat.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageAlphabeth)
    }
}