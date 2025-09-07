package com.wildan.storeapp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.wildan.core.ui.helper.ViewBindingExt.createAlertDialog
import com.wildan.core.ui.helper.ViewBindingExt.viewBinding
import com.wildan.core.extension.showToast
import com.wildan.core.extension.toRupiah
import com.wildan.core.utils.Constant
import com.wildan.core.utils.handleErrorApi
import com.wildan.storeapp.R
import com.wildan.storeapp.data.database.ProductEntity
import com.wildan.storeapp.databinding.ActivityDetailProductBinding
import com.wildan.storeapp.databinding.DialogInsertQuantityBinding
import com.wildan.storeapp.ui.viewmodel.DatabaseViewModel
import com.wildan.storeapp.ui.viewmodel.LocalDataViewModelFactory
import com.wildan.storeapp.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityDetailProductBinding::inflate)
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var viewModelDatabase: DatabaseViewModel
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        getLiveData()
    }

    private fun setupView() = with(binding) {
        productId = intent.getStringExtra(Constant.PRODUCT_ID)
        val factory = LocalDataViewModelFactory.getInstance(this@DetailProductActivity)
        viewModelDatabase =
            ViewModelProvider(this@DetailProductActivity, factory)[DatabaseViewModel::class.java]

        viewModel.getProductDetail(productId.toString())
        swipeRefresh.setOnRefreshListener {
            viewModel.getProductDetail(productId.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLiveData() = with(binding) {
        viewModel.apply {
            getProductDetail.observe(this@DetailProductActivity) { data ->
                val productName = data.title ?: "-"
                Glide.with(applicationContext)
                    .load(data.image)
                    .placeholder(R.drawable.baseline_image_100)
                    .into(imageProduct)
                tvTitle.text = productName
                tvPrice.text = data?.price?.toRupiah()
                tvCategory.text = data.category ?: "None category"
                tvDescription.text = data.description ?: "None description"
                tvRating.text = "${data.rating?.rate} (${data.rating?.count})"

                val product = ProductEntity()
                product.id = data.id
                product.title = productName
                product.price = data?.price ?: 0.0
                product.image = data.image

                btnAddToCart.setOnClickListener {
                    addToCart(product)
                }
            }
            error.observe(this@DetailProductActivity) {
                handleErrorApi(it)
            }
            loading.observe(this@DetailProductActivity) {
                swipeRefresh.isRefreshing = it
            }
        }
    }

    private fun addToCart(cartBody: ProductEntity) {
        var productCount = 1
        val productName = cartBody.title ?: "-"

        createAlertDialog(productName, DialogInsertQuantityBinding::inflate){ v, dialog ->
            v.buttonMin.setOnClickListener {
                if (productCount > 1) {
                    productCount--
                    v.inputCount.setText(productCount.toString())
                }
            }

            v.buttonPlus.setOnClickListener {
                productCount++
                v.inputCount.setText(productCount.toString())
            }

            v.inputCount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if ((s?.length ?: 0) >= 1) {
                        val count = s?.toString()
                        productCount = count?.toIntOrNull() ?: 1
                    }
                }
            })

            v.buttonAdd.setOnClickListener {
                if (v.inputCount.text?.isEmpty() == true) {
                    showToast("The number of products cannot be empty")
                } else {
                    cartBody.count = productCount
                    viewModelDatabase.addToCart(cartBody, productCount)
                    showToast("Add to Cart")
                    dialog.dismiss()
                }
            }

            v.buttonBack.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}