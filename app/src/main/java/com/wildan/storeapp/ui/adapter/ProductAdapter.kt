package com.wildan.storeapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wildan.core.extension.toRupiah
import com.wildan.core.utils.Constant
import com.wildan.storeapp.R
import com.wildan.storeapp.databinding.ItemProductBinding
import com.wildan.storeapp.model.ProductResponse
import com.wildan.storeapp.ui.activity.DetailProductActivity

class ProductAdapter : ListAdapter<ProductResponse, ProductAdapter.Holder>(MyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position)
        val context = holder.itemView.context

        with(holder.binding) {
            val productName = data.title ?: "-"
            Glide.with(context)
                .load(data.image)
                .placeholder(R.drawable.baseline_image_100)
                .into(imageProduct)
            tvProductName.text = productName
            tvProductPrice.text = data?.price.toRupiah()
            tvRating.text = "${data.rating?.rate} (${data.rating?.count})"

            cardProduct.setOnClickListener {
                toDetailFragment(
                    data,
                    DetailProductActivity::class.java,
                    it.context)
            }
        }
    }

    private fun toDetailFragment(
        data: ProductResponse,
        activity: Class<out AppCompatActivity>,
        context: Context) {
        val bundle = Bundle().apply {
            putString(Constant.PRODUCT_ID, data.id.toString())
        }
        context.startActivity(Intent(context, activity).putExtras(bundle))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class MyDiffCallback : DiffUtil.ItemCallback<ProductResponse>() {
        override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem == newItem
        }
    }

    class Holder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}