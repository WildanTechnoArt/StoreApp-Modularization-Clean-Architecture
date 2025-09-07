package com.wildan.storeapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wildan.core.ui.helper.ViewBindingExt.createAlertDialog
import com.wildan.core.ui.helper.ViewBindingExt.viewBinding
import com.wildan.core.extension.showAlertDialog
import com.wildan.core.extension.toRupiah
import com.wildan.storeapp.databinding.ActivityCheckoutBinding
import com.wildan.storeapp.databinding.AlertTransactionSuccessBinding
import com.wildan.storeapp.ui.adapter.CartAdapter
import com.wildan.storeapp.ui.viewmodel.DatabaseViewModel
import com.wildan.storeapp.ui.viewmodel.LocalDataViewModelFactory
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CheckoutActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCheckoutBinding::inflate)
    private lateinit var viewModelDatabase: DatabaseViewModel
    private var mAdapter by Delegates.notNull<CartAdapter>()
    private val mAdminFee = 2000.0
    private val mShipping = 15000.0
    private var mSubtotal = 0.0
    private var mTotalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val factory = LocalDataViewModelFactory.getInstance(this)
        viewModelDatabase =
            ViewModelProvider(this, factory)[DatabaseViewModel::class.java]

        mAdapter = CartAdapter(true) { product ->
            showAlertDialog("Are you sure you want to remove?") {
                viewModelDatabase.removeFromCart(product)
            }
        }

        binding.rvProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = mAdapter
        }

        viewModelDatabase.allData.observe(this) { pagingData ->
            lifecycleScope.launch {
                mAdapter.submitList(pagingData)
                pagingData.forEach {
                    mSubtotal += it.price?.times(it.count ?: 1) ?: 0.0
                }

                mTotalPrice = mSubtotal + mAdminFee + mShipping
                binding.tvTotalPrice.text = mTotalPrice.toRupiah()
                binding.tvSubtotalPrice.text = mSubtotal.toRupiah()
            }
        }

        binding.btnCheckout.setOnClickListener {
            showAlertDialog("Are you sure you want to place an order now?\nYour order will be processed."){
                createAlertDialog(null, AlertTransactionSuccessBinding::inflate){ v, dialog ->
                    v.btnFinish.setOnClickListener {
                        lifecycleScope.launch {
                            viewModelDatabase.clearCart()
                            finish()
                        }
                    }
                    dialog.show()
                }
            }
        }
    }
}