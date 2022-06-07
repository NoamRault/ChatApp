package com.noamrault.chatapp.data.newGroup

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noamrault.chatapp.R

class NewGroupAdapter(
    private val modelList: ArrayList<NewGroupModel>,
    private val hashMap: HashMap<String, String>
) :
    RecyclerView.Adapter<NewGroupAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.fragment_new_group_friend_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_new_group_item, parent, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val model = modelList[position]
        viewHolder.textView.text = hashMap[modelList[position].getText()]
        viewHolder.textView.setOnClickListener {
            model.setSelected(!model.isSelected())
            viewHolder.view.setBackgroundColor(if (model.isSelected()) Color.CYAN else Color.WHITE)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = modelList.size
}