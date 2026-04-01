package com.example.beautyparlour

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.beautyparlour.databinding.ActivityMainBinding

/**
 * MainActivity handles the primary navigation and UI logic for the BeautyParlour app.
 * It manages different "pages" through visibility toggling within a scrollable flow.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sliderHandler = Handler(Looper.getMainLooper())
    
    // Lazy initialization of the adapter to optimize resource usage
    private val trendingAdapter by lazy {
        TrendingAdapter(getTrendingData()) { item ->
            navigateToSalonDetail(item.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        initUI()
        setupListeners()
        setupAutoSlider()
    }

    private fun initViewBinding() {
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Handle window insets for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initUI() {
        with(binding) {
            // Setup RecyclerView for Trending list
            rvAllTrending.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = trendingAdapter
            }
            
            // Setup ViewPager for Trending slider
            trendingViewPager.adapter = trendingAdapter
        }
    }

    private fun setupListeners() {
        with(binding) {
            // Category Buttons navigation
            btnHairCut.setOnClickListener { navigateToCategory(getString(R.string.cat_haircut)) }
            btnMakeup.setOnClickListener { navigateToCategory(getString(R.string.cat_makeup)) }
            btnFacial.setOnClickListener { navigateToCategory(getString(R.string.cat_facial)) }
            btnBridal.setOnClickListener { navigateToCategory(getString(R.string.cat_bridal)) }
            
            // Header Back Navigation logic
            llHomeBack.setOnClickListener { navigateToHome() }

            // "View All" / "Show Slider" toggle
            tvViewAll.setOnClickListener { toggleTrendingView() }
        }
    }

    private fun toggleTrendingView() {
        with(binding) {
            val isListViewVisible = rvAllTrending.isVisible
            rvAllTrending.isVisible = !isListViewVisible
            trendingViewPager.isVisible = isListViewVisible
            tvViewAll.text = if (isListViewVisible) {
                getString(R.string.view_all)
            } else {
                getString(R.string.show_slider)
            }
        }
    }

    private fun setupAutoSlider() {
        binding.trendingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                resetSliderTimer()
            }
        })
    }

    private fun resetSliderTimer() {
        sliderHandler.removeCallbacks(sliderRunnable)
        sliderHandler.postDelayed(sliderRunnable, SLIDER_DELAY_MS)
    }

    private fun navigateToCategory(category: String) {
        setPagesVisibility(targetPage = getCategoryPageView(category))
        updateHeader(title = category, showBack = true)
        binding.scrollView.scrollTo(0, 0)
    }

    private fun navigateToSalonDetail(name: String) {
        setPagesVisibility(showSalonDetail = true)
        updateHeader(title = name, showBack = true, isDetail = true)
        binding.scrollView.scrollTo(0, 0)
    }

    private fun navigateToHome() {
        // For Home, we show both home content and the default detail section below it
        setPagesVisibility(showHome = true, showSalonDetail = true)
        updateHeader(isHome = true)
        binding.scrollView.scrollTo(0, 0)
    }

    /**
     * Centralized visibility management for different UI pages.
     */
    private fun setPagesVisibility(
        showHome: Boolean = false,
        showSalonDetail: Boolean = false,
        targetPage: View? = null
    ) {
        with(binding) {
            pageHome.isVisible = showHome
            sectionSalonDetail.isVisible = showSalonDetail
            pageHairCut.isVisible = targetPage == pageHairCut
            pageMakeup.isVisible = targetPage == pageMakeup
            pageFacial.isVisible = targetPage == pageFacial
            pageBridal.isVisible = targetPage == pageBridal
        }
    }

    private fun getCategoryPageView(category: String): View? {
        return when (category) {
            getString(R.string.cat_haircut) -> binding.pageHairCut
            getString(R.string.cat_makeup) -> binding.pageMakeup
            getString(R.string.cat_facial) -> binding.pageFacial
            getString(R.string.cat_bridal) -> binding.pageBridal
            else -> null
        }
    }

    private fun updateHeader(
        title: String = "", 
        isHome: Boolean = false, 
        isDetail: Boolean = false,
        showBack: Boolean = false
    ) {
        with(binding) {
            if (isHome) {
                ivHeaderIcon.setImageResource(android.R.drawable.ic_menu_mylocation)
                ivHeaderIcon.rotation = 0f
                ivHeaderArrow.isVisible = true
                tvHeaderTitle.text = getString(R.string.location_text)
                etSearch.setText("")
                etSearch.hint = getString(R.string.search_hint_home)
            } else {
                ivHeaderIcon.setImageResource(android.R.drawable.ic_menu_revert)
                ivHeaderIcon.rotation = 180f
                ivHeaderArrow.isVisible = false
                tvHeaderTitle.text = title
                etSearch.setText(if (isDetail) "" else title)
                if (isDetail) etSearch.hint = getString(R.string.search_hint_home)
            }
            appBarLayout.setExpanded(true, true)
        }
    }

    private fun getTrendingData() = listOf(
        TrendingItem("Parvan Spa", "4.8 (1.5K Reviews)", "3332"),
        TrendingItem("Glow & Glam", "4.7 (1.2K Reviews)", "2150"),
        TrendingItem("Divine Luxury", "4.9 (2K Reviews)", "4500")
    )

    private val sliderRunnable = Runnable {
        val itemCount = trendingAdapter.itemCount
        if (itemCount > 0) {
            val nextItem = (binding.trendingViewPager.currentItem + 1) % itemCount
            binding.trendingViewPager.setCurrentItem(nextItem, true)
        }
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        resetSliderTimer()
    }

    companion object {
        private const val SLIDER_DELAY_MS = 3000L
    }
}