package com.example.projectskelton.presentation.ui.adapters

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectskelton.data.repository.Run
import com.example.projectskelton.databinding.ItemRunBinding
import com.example.projectskelton.domain.util.TimeUtility
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    private lateinit var binding: ItemRunBinding

    inner class RunViewHolder(binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root)

    val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(mRunList: List<Run>) = differ.submitList(mRunList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        binding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(binding)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList.get(position)
        holder.itemView.apply {
            Glide
                .with(this)
                .load(run.img)
                .into(binding.ivRunImage)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(calendar.time)

            binding.tvAvgSpeed.text = "${run.avgSpeedInKMH}Km/h"
            binding.tvDistance.text = "${run.distanceInMetres / 1000}km"

            binding.tvTime.text = TimeUtility.getFormattedTime(run.timeInMillis)
            binding.tvCalories.text = "${run.caloriesBurnt}"
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}