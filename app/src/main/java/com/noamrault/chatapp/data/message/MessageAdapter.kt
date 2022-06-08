package com.noamrault.chatapp.data.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository

class MessageAdapter(private val dataSet: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private val userId = loginRepo.user!!.uid

    private val msgReceived = 0
    private val msgSent = 1

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView
        val content: TextView
        val date: TextView

        init {
            // Define click listener for the ViewHolder's View.
            author = view.findViewById(R.id.fragment_group_message_author)
            content = view.findViewById(R.id.fragment_group_message)
            date = view.findViewById(R.id.fragment_group_message_date)
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
        return if (dataSet[position].author == userId) {
            msgSent
        } else {
            msgReceived
        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.author.text = dataSet[position].author
        viewHolder.content.text = dataSet[position].content
        viewHolder.date.text = dataSet[position].sentDate.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}