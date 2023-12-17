package com.pratwib.github_user_app.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratwib.github_user_app.database.FavoriteEntity
import com.pratwib.github_user_app.databinding.ItemUserFollowsBinding
import com.pratwib.github_user_app.ui.detail.DetailActivity

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    private val userFavorites = ArrayList<FavoriteEntity>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            ItemUserFollowsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(userFavorites[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(userFavorites[position]) }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListFavorite(items: List<FavoriteEntity>) {
        userFavorites.clear()
        userFavorites.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = userFavorites.size
    class MyViewHolder(private val binding: ItemUserFollowsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favEntity: FavoriteEntity) {
            with(binding) {
                tvItemName.text = favEntity.login
                Glide.with(root)
                    .load(favEntity.avatar_url)
                    .circleCrop()
                    .into(binding.imgItemPhoto)
                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.KEY_USER, favEntity)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(favEntity: FavoriteEntity)
    }
}