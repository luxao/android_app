package com.example.journey_dp.ui.adapter.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R
import com.example.journey_dp.databinding.DestinationItemBinding
import com.example.journey_dp.domain.InputData
import com.example.journey_dp.ui.adapter.events.InputEventListener
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.textfield.TextInputEditText



//class InputAdapter(
//    private val result: ActivityResultLauncher<Intent>
//) : ListAdapter<InputData, InputAdapter.InputItemViewHolder>(DiffCallback) {
//
//    class InputItemViewHolder(var binding: DestinationItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(result: ActivityResultLauncher<Intent>, inputData: InputData) {
//            binding.inputData = inputData
//            binding.clickListener = InputEventListener(result = result, binding.root)
//        }
//    }
//
//    companion object DiffCallback: DiffUtil.ItemCallback<InputData>() {
//        override fun areItemsTheSame(oldItem: InputData, newItem: InputData): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: InputData, newItem: InputData): Boolean {
//            return oldItem == newItem
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputAdapter.InputItemViewHolder {
//        return InputAdapter.InputItemViewHolder(
//            DestinationItemBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: InputAdapter.InputItemViewHolder, position: Int) {
//        val input: InputData = getItem(position)
//        holder.bind(result, input)
//    }
//}






class InputAdapter(private val inputs: MutableList<LinearLayout>, private val result: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<InputAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.destination_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return inputs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.inputText.id = inputs[position].id
        holder.inputText.setOnClickListener(
            InputEventListener(result)
        )
//        holder.addButton.setOnClickListener {
//            addInput(holder.layout)
//            notifyItemInserted(inputs[position].id)
//        }
    }

//    private fun addInput(layout: LinearLayout) {
//        inputs.add(layout)
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_destination)
//        val addButton: Button = itemView.findViewById(R.id.test_button)
//        val layout: LinearLayout = itemView.findViewById(R.id.layout_for_add_stop)
    }
}

