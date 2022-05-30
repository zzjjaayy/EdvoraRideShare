package com.jay.edvorarideshare.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jay.edvorarideshare.R
import com.jay.edvorarideshare.data.models.Ride
import com.jay.edvorarideshare.databinding.RideItemBinding
import com.jay.edvorarideshare.utils.Helpers
import com.jay.edvorarideshare.utils.Helpers.formatDate
import com.jay.edvorarideshare.utils.TAG
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class RideAdapter(private val userStationId: Int, private val onRideClicked: (Ride) -> Unit) :
    ListAdapter<Ride, RideAdapter.RideViewHolder>(DiffCallback) {

    class RideViewHolder(private val userStationId: Int,
        private val binding: RideItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(ride: Ride) {
            binding.apply {
                city.text = ride.city
                state.text = ride.state
                this.root.context.let { context ->
                    val mDate =Helpers.stringToDate(ride.date)
                    Glide.with(context).load(ride.map_url).error(R.drawable.ic_error)
                        .transform(CenterCrop(), RoundedCorners(20)).into(mapImage)
                    rideId.text = context.getString(R.string.ride_id_template, ride.id)
                    originStation.text = context.getString(R.string.origin_template, ride.origin_station_code)
                    stationPath.text = context.getString(R.string.path_template, ride.station_path)
                    date.text = context.getString(R.string.date_template, mDate?.formatDate())
                    distance.text = context.getString(R.string.distance_template,
                        Helpers.getDistance(userStationId, ride.station_path))
                }
            }
        }



    }

    companion object DiffCallback: DiffUtil.ItemCallback<Ride>(){
        override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val viewHolder = RideViewHolder(userStationId,
            RideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        viewHolder.itemView.setOnClickListener {
            onRideClicked(getItem(viewHolder.adapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}