package com.adyen.android.assignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.R
import com.adyen.android.assignment.databinding.PlaceItemBinding
import com.adyen.android.assignment.domain.model.Place
import com.bumptech.glide.Glide

class PlacesAdapter : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private val items: MutableList<Place> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updatePlacesList(items: List<Place>) {
        this.items.clear()
        this.items += items
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Place) = with(item) {
            item.photos?.let {
                Glide.with(itemView.context)
                    .load(item.photos.firstOrNull()?.url ?: R.drawable.ic_baseline_place_24)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_place_24)
                    .error(R.drawable.ic_baseline_place_error_24)
                    .into(binding.placeImage)
            }
            with(binding){
                placeName.text = name
                placeDescription.text = description
            }
        }
    }
}