package com.wildan.storeapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wildan.core.ui.helper.ViewBindingExt.viewBinding
import com.wildan.core.extension.showAlertDialog
import com.wildan.storeapp.databinding.ActivityCartBinding
import com.wildan.storeapp.ui.adapter.CartAdapter
import com.wildan.storeapp.ui.viewmodel.DatabaseViewModel
import com.wildan.storeapp.ui.viewmodel.LocalDataViewModelFactory
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CartActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCartBinding::inflate)
    private lateinit var viewModelDatabase: DatabaseViewModel
    private var mAdapter by Delegates.notNull<CartAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val factory = LocalDataViewModelFactory.getInstance(this)
        viewModelDatabase =
            ViewModelProvider(this, factory)[DatabaseViewModel::class.java]

        mAdapter = CartAdapter(false) { product ->
            showAlertDialog("Are you sure you want to remove?") {
                viewModelDatabase.removeFromCart(product)
            }
        }

        binding.rvProduct.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = mAdapter
        }

        viewModelDatabase.allData.observe(this) { pagingData ->
            lifecycleScope.launch {
                mAdapter.submitList(pagingData)

                val itemCount = pagingData.size
                if (itemCount > 0) {
                    binding.rvProduct.visibility = View.VISIBLE
                    binding.textMessage.visibility = View.GONE
                } else {
                    binding.rvProduct.visibility = View.GONE
                    binding.textMessage.visibility = View.VISIBLE
                }
            }
        }

        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }
}