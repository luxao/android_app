package com.example.journey_dp.ui.adapter.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R
import com.example.journey_dp.data.domain.Step
import com.example.journey_dp.databinding.StepItemBinding
import com.example.journey_dp.utils.clearFromHtml

class StepsAdapter : ListAdapter<Step, StepsAdapter.StepItemViewHolder>(DiffCallback) {

    class StepItemViewHolder(var binding: StepItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(step: Step) {
                try {
                    binding.step = step
                    binding.maneuverImageView.setImageResource(
                        when(step.maneuver) {
                            "turn-slight-left" -> R.drawable.turn_slight_left
                            "turn-sharp-left" -> R.drawable.turn_sharp_left
                            "turn-left" -> R.drawable.turn_left
                            "turn-slight-right" -> R.drawable.turn_slight_right
                            "turn-sharp-right" -> R.drawable.turn_sharp_right
                            "keep-right" -> R.drawable.keep_right
                            "keep-left" -> R.drawable.keep_left
                            "uturn-left" -> R.drawable.u_turn_left
                            "uturn-right" -> R.drawable.u_turn_right
                            "turn-right" -> R.drawable.turn_right
                            "straight" -> R.drawable.straight
                            "ramp-left" -> R.drawable.ramp_left
                            "ramp-right" -> R.drawable.ramp_right
                            "merge" -> R.drawable.merge
                            "fork-left" -> R.drawable.fork_left
                            "fork-right" -> R.drawable.fork_right
                            "ferry" -> R.drawable.ferry
                            "ferry-train" -> R.drawable.ic_baseline_directions_transit_filled_24
                            "roundabout-left" -> R.drawable.roundabout_left
                            "roundabout-right" -> R.drawable.roundabout_right
                            else -> R.drawable.straight
                        }
                    )
                    binding.instructionsTextView.text = clearFromHtml(step.instructions!!)

                    //TODO: TRY VEHICLE FOR TRANSIT
                    binding.travelModeImageView.setImageResource(
                        when(step.travelMode) {
                            "DRIVING" -> R.drawable.ic_baseline_directions_car_24
                            "WALKING" -> R.drawable.ic_baseline_directions_walk_24
                            "BICYCLING" -> R.drawable.ic_baseline_directions_bike_24
                            "TRANSIT" -> R.drawable.ic_baseline_directions_transit_filled_24
                            else -> R.drawable.ic_baseline_directions_car_24
                        }
                    )
                    binding.durationAndDistanceTextView.text = step.duration.text.plus("\n ").plus(step.distance.text)
                }
                catch(e: NullPointerException) {
                    Log.e("MYTEST", "NullPointerException : $e")

                    Log.e("MYTEST", "NullPointerException : ${e.stackTrace}")
                    Log.e("MYTEST","NullPointerException : ${e.localizedMessage}")
                }
                catch (e: OutOfMemoryError) {
                    Log.e("MYTEST", "OutOfMemoryError : $e")

                    Log.e("MYTEST", "OutOfMemoryError : ${e.stackTrace}")
                    Log.e("MYTEST","OutOfMemoryError : ${e.localizedMessage}")
                }
                catch (e: IllegalStateException) {
                    Log.e("MYTEST", "IllegalStateException : $e")

                    Log.e("MYTEST", "IllegalStateException : ${e.stackTrace}")
                    Log.e("MYTEST","IllegalStateException : ${e.localizedMessage}")
                }
                catch (e: Exception) {
                    Log.e("MYTEST", "Exception : $e")

                    Log.e("MYTEST", "Exception : ${e.stackTrace}")
                    Log.e("MYTEST","Exception : ${e.localizedMessage}")
                }
            }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Step>() {
        override fun areItemsTheSame(oldItem: Step, newItem: Step): Boolean {
            return oldItem.startLocation == newItem.startLocation
        }

        override fun areContentsTheSame(oldItem: Step, newItem: Step): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepItemViewHolder {
        return StepItemViewHolder(
            StepItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StepItemViewHolder, position: Int) {
        val step: Step = getItem(position)
        holder.bind(step)
    }
}