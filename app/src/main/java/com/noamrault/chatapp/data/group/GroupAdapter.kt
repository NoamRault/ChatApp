package com.noamrault.chatapp.data.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.noamrault.chatapp.R
import com.noamrault.chatapp.ui.main.HomeFragment

class GroupAdapter(private val dataSet: ArrayList<Group>) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        var groupId: String = ""

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.fragment_home_group_name)
            textView.setOnClickListener {
                (view.findFragment<Fragment>() as HomeFragment).openGroup(groupId, textView.text as String)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_home_group_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].name
        viewHolder.groupId = dataSet[position].id
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}