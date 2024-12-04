package com.example.c2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ContactsAdapter(
    private val contacts: List<Contacts>,
    private val context: Context
) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.contact_list_image)
        val title: TextView = view.findViewById(R.id.contact_list_title)
        val numb: TextView = view.findViewById(R.id.contact_list_numb)
        val desc: TextView = view.findViewById(R.id.contact_list_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contacts[position]

        holder.title.text = contact.title
        holder.numb.text = contact.numb
        holder.desc.text = contact.desc

        Glide.with(context)
            .load(contact.image)
            .error(R.drawable.bonichka)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ContactActivity::class.java)
            intent.putExtra("contactId", contact.id)
            intent.putExtra("contactImage", contact.image)
            intent.putExtra("contactTitle", contact.title)
            intent.putExtra("contactNumb", contact.numb)
            intent.putExtra("contactDesc", contact.desc)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = contacts.size
}
