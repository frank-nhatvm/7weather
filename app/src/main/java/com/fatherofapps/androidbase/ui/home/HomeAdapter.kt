package com.fatherofapps.androidbase.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fatherofapps.androidbase.data.models.WeatherInfo
import com.fatherofapps.androidbase.databinding.AdapterHomeBinding

class HomeAdapter : ListAdapter<WeatherInfo, HomeAdapter.HomeViewHolder>(WeatherInfoDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HomeViewHolder private constructor(private val adapterHomeBinding: AdapterHomeBinding) :
        RecyclerView.ViewHolder(adapterHomeBinding.root) {

        companion object {
            fun from(parent: ViewGroup): HomeViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = AdapterHomeBinding.inflate(inflater, parent, false)
                return HomeViewHolder(binding)
            }
        }

        fun bind(weatherInfo: WeatherInfo) {
            adapterHomeBinding.weatherInfo = weatherInfo
            adapterHomeBinding.executePendingBindings()
        }

    }

    class WeatherInfoDiffUtils : DiffUtil.ItemCallback<WeatherInfo>() {
        override fun areItemsTheSame(oldItem: WeatherInfo, newItem: WeatherInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherInfo, newItem: WeatherInfo): Boolean {
            return oldItem.date == newItem.date
        }
    }
}