package com.wildan.storeapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wildan.core.ui.helper.ViewBindingExt.viewBinding
import com.wildan.core.extension.getStringData
import com.wildan.core.utils.Constant
import com.wildan.core.ui.helper.handleData
import com.wildan.core.utils.handleErrorApi
import com.wildan.storeapp.databinding.ActivityMainBinding
import com.wildan.storeapp.ui.adapter.CategoryAdapter
import com.wildan.storeapp.ui.adapter.ProductAdapter
import com.wildan.storeapp.ui.fragment.ProfileBottomSheetFragment
import com.wildan.storeapp.ui.viewmodel.DatabaseViewModel
import com.wildan.storeapp.ui.viewmodel.LocalDataViewModelFactory
import com.wildan.storeapp.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var mAdapterProduct by Delegates.notNull<ProductAdapter>()
    private var mAdapterCategory by Delegates.notNull<CategoryAdapter>()
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var viewModelDatabase: DatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        requestContent()
        getLiveData()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() = with(binding) {
        val factory = LocalDataViewModelFactory.getInstance(this@MainActivity)

        viewModelDatabase =
            ViewModelProvider(this@MainActivity, factory)[DatabaseViewModel::class.java]

        mAdapterProduct = ProductAdapter()
        mAdapterCategory = CategoryAdapter { category ->
            if (category == "All") {
                viewModel.getProductList()
            } else {
                viewModel.getProductByCategory(category)
            }
        }

        rvProduct.apply {
            setHasFixedSize(false)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = mAdapterProduct
        }

        rvCategory.apply {
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapterCategory
        }

        swipeRefresh.setOnRefreshListener {
            requestContent()
        }
        btnCart.setOnClickListener {
            startActivity(Intent(this@MainActivity, CartActivity::class.java))
        }

        lifecycleScope.launch {
            val username = getStringData(Constant.SAVE_USERNAME)
            tvUsername.text = "Hello, $username"
        }

        tvUsername.setOnClickListener {
            val bottomSheet = ProfileBottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "Profile")
        }
    }

    private fun requestContent() {
        viewModel.getProductList()
        viewModel.getCategoryList()
    }

    private fun getLiveData() = with(binding) {
        viewModel.apply {
            getProductList.observe(this@MainActivity) { data ->
                handleData(
                    1, data, mAdapterProduct,
                    rvProduct, textMessageNoData
                )
            }
            getCategoryList.observe(this@MainActivity) { data ->
                mAdapterCategory.submitCategoryList(data)
            }
            error.observe(this@MainActivity) {
                handleErrorApi(it)
            }
            loading.observe(this@MainActivity) {
                swipeRefresh.isRefreshing = it
            }
        }

        viewModelDatabase.getTotalItemCount.observe(this@MainActivity) { count ->
            lifecycleScope.launch {
                val itemCount = (count ?: 0) > 0
                tvCartBadge.visibility = if (itemCount) View.VISIBLE else View.GONE
                tvCartBadge.text = count.toString()
            }
        }
    }
}