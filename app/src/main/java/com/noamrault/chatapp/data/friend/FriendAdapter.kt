package com.noamrault.chatapp.data.friend

import android.content.ContentValues
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.noamrault.chatapp.R
import com.noamrault.chatapp.ui.main.HomeFragment
import com.noamrault.chatapp.ui.main.RemoveFriendDialogFragment

class FriendAdapter(private val dataSet: ArrayList<Friend>) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.fragment_home_friend_name)
        val button: ImageButton = view.findViewById(R.id.fragment_home_friend_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_home_friend_item, parent, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].username
        val id = dataSet[position].id
        val username = dataSet[position].username

        viewHolder.button.setOnClickListener {
            RemoveFriendDialogFragment(viewHolder.view.findFragment(), id, username)
                .show(
                    viewHolder.view.findFragment<Fragment>().childFragmentManager,
                    ContentValues.TAG
                )
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}