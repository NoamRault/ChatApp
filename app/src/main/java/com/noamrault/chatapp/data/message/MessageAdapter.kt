package com.noamrault.chatapp.data.message

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class MessageAdapter(private val dataSet: ArrayList<Message>, private val activity: MainActivity) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private val userId = loginRepo.user!!.uid

    private val msgReceived = 0
    private val msgSent = 1

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var author = dataSet[position].author

        for (member in activity.database.groupDao().findById(dataSet[position].groupId).members) {
            if (member.id == author) {
                author = member.username
                break
            }
        }

        viewHolder.author.text = author
        viewHolder.content.text = dataSet[position].content
        viewHolder.date.text =
            SimpleDateFormat("dd/MM/yy, HH:mm").format(dataSet[position].sentDate)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}