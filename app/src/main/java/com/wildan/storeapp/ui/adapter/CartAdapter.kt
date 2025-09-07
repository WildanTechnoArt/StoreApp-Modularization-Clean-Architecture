package com.wildan.storeapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wildan.core.extension.toRupiah
import com.wildan.core.utils.Constant
import com.wildan.storeapp.R
import com.wildan.storeapp.data.database.ProductEntity
import com.wildan.storeapp.databinding.ItemCartBinding
import com.wildan.storeapp.ui.activity.DetailProductActivity

class CartAdapter(private val isCheckout: Boolean, private val deleteItem: (ProductEntity) -> Unit) :
    RecyclerView.Adapter<CartAdapter.Holder>() {

    private val dataList = mutableListOf<ProductEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newData: List<ProductEntity>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = dataList[position]
        val context = holder.itemView.context

        with(holder.binding) {
            val productName = data.title ?: "-"
            Glide.with(context)
                .load(data.image)
                .placeholder(R.drawable.baseline_image_100)
                .into(imgProduct)
            tvProductName.text = productName
            tvProductPrice.text = data.price.toRupiah()
            tvProductQuantity.text = "Quantity: ${data.count} pcs"

            cardProduct.setOnClickListener {
                toDetailFragment(
                    data,
                    DetailProductActivity::class.java,
                    it.context
                )
            }

            if (!isCheckout) {
                btnDelete.visibility = View.VISIBLE
                btnDelete.setOnClickListener {
                    deleteItem(data)
                }
            }
        }
    }

    private fun toDetailFragment(
        data: ProductEntity,
        activity: Class<out AppCompatActivity>,
        context: Context
    ) {
        val bundle = Bundle().apply {
            putString(Constant.PRODUCT_ID, data.id.toString())
        }
        context.startActivity(Intent(context, activity).putExtras(bundle))
    }

    override fun getItemCount(): Int = dataList.size

    class Holder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)
}
