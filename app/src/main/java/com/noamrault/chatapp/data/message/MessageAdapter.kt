package com.noamrault.chatapp.data.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noamrault.chatapp.R

class MessageAdapter(private val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val msgReceived = 0
    private val msgSent = 1

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.fragment_home_group_name)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        return if (viewType == msgReceived) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.fragment_group_message_received, viewGroup, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.fragment_group_message_sent, viewGroup, false)
            ViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // TODO
        return if (true) {
            msgSent
        } else {
            msgReceived
        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}