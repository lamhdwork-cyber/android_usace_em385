package com.navfac.usace.safety.features.bottomtab.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navfac.usace.safety.R
import com.navfac.usace.safety.features.bottomtab.home.model.MenuModel

class HomeMenuAdapter(private val menulist: List<MenuModel>?) : RecyclerView.Adapter<HomeMenuAdapter.Holder>() {

    internal var actionListener: (position: Int, menu: MenuModel) -> Unit = { _, _ -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.custom_home_menu_item,
                parent,
                false
        )
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val menu = menulist!![position]
        holder.name.text = menu.menuName
        holder.icon.setImageResource(menu.menuIcon)

        holder.itemView.setOnClickListener {
            actionListener.invoke(position, menu)
        }
    }

    override fun getItemCount(): Int {
        return menulist?.size ?: 0
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var icon: ImageView

        init {
            name = itemView.findViewById(R.id.menu_name)
            icon = itemView.findViewById(R.id.menu_icon)
        }
    }

}