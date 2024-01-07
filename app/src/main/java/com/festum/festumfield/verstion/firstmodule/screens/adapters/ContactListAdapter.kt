package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.festum.festumfield.databinding.ContactListBinding
import com.festum.festumfield.verstion.firstmodule.sources.local.model.PhoneContactList
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ContactListAdapter(
    private val context: Context,
    private var phoneContactList: ArrayList<PhoneContactList?>
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ContactListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return phoneContactList.size
    }

    inner class ViewHolder(private val displayBinding: ContactListBinding) : RecyclerView.ViewHolder(displayBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = phoneContactList[position]

            displayBinding.txtUserName.text = item?.name
            displayBinding.txtMessage.text = item?.number

            if (item?.isFFuser == true){

                displayBinding.btnInvite.visibility = View.GONE

            }else{

                displayBinding.btnInvite.visibility = View.VISIBLE

            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterItems: ArrayList<PhoneContactList?>) {
        phoneContactList = filterItems
        notifyDataSetChanged()
    }

}