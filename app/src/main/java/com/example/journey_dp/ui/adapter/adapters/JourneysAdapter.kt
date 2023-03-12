package com.example.journey_dp.ui.adapter.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.databinding.JourneyCardItemBinding
import com.example.journey_dp.ui.adapter.events.JourneyEventListener
import com.example.journey_dp.ui.viewmodel.ProfileViewModel

class JourneysAdapter(
    private val context: Context,
    private val model: ProfileViewModel,
    private val journeyEventListener: JourneyEventListener
) : ListAdapter<JourneyWithRoutes, JourneysAdapter.JourneyItemViewHolder>(DiffCallback) {

    class JourneyItemViewHolder(var binding: JourneyCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(journey: JourneyWithRoutes,journeyEventListener: JourneyEventListener) {
            binding.journey = journey
            binding.journeyEventListener = journeyEventListener
            binding.journeyNameItem.text = journey.journey.name
            binding.totalDistanceItem.text =  journey.journey.totalDistance
            binding.totalDurationItem.text = journey.journey.totalDuration
            binding.totalDestinationsItem.text = journey.journey.numberOfDestinations.toString()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<JourneyWithRoutes>() {
        override fun areItemsTheSame(oldItem: JourneyWithRoutes, newItem: JourneyWithRoutes): Boolean {
            return oldItem.journey.id == newItem.journey.id
        }

        override fun areContentsTheSame(oldItem: JourneyWithRoutes, newItem: JourneyWithRoutes): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyItemViewHolder {
        return JourneyItemViewHolder(
            JourneyCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JourneyItemViewHolder, position: Int) {
        val journey: JourneyWithRoutes = getItem(position)
        holder.bind(journey,journeyEventListener)
    }

    inner class SwipeToDeleteCallback(private val adapter: JourneysAdapter) :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val journey = getItem(position)

            AlertDialog.Builder(context)
                .setTitle("Delete item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    if (journey != null) {
                        Log.i("MYTEST", "SWIPED ID: ${journey.journey.id}")
                        Log.i("MYTEST", "SWIPED : ${journey.journey}")
                        model.deleteJourneyWithDestinations(journey = journey.journey)
                        adapter.notifyItemRemoved(position)
                    }
                    else {
                        Toast.makeText(context, "Please Try Again in a minute", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    adapter.notifyItemChanged(position)
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            val backgroundColor = Color.parseColor("#f44336")
            val iconMargin = (itemHeight - deleteIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemHeight - deleteIcon.intrinsicHeight) / 2
            val iconBottom = iconTop + deleteIcon.intrinsicHeight

            if (dX < 0) { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)


                val left = itemView.right + dX.toInt()
                val right = itemView.right
                clearCanvas(c, left.toFloat(), itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat())
                c.drawRect(left.toFloat(), itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat(), Paint().apply { color = backgroundColor })
                deleteIcon.draw(c)
            } else { // Swiping to the right
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                val background = Color.parseColor("#4caf50")
                val left = itemView.left
                val right = itemView.left + dX.toInt()
                clearCanvas(c, left.toFloat(), itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat())
                c.drawRect(left.toFloat(), itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat(), Paint().apply { color = backgroundColor })
                deleteIcon.draw(c)
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
            c?.drawRect(left, top, right, bottom, Paint().apply { color = Color.WHITE })
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}








