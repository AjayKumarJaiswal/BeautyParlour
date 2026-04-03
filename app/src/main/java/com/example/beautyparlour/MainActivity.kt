package com.example.beautyparlour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.beautyparlour.databinding.ActivityMainBinding
import com.example.beautyparlour.databinding.ItemServiceCardBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var isAtHome = true
    private val sharedPrefs by lazy { getSharedPreferences("UserPrefs", Context.MODE_PRIVATE) }

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            navigateToHome()
        }
    }

    private val trendingAdapter by lazy {
        TrendingAdapter(getTrendingData()) { item ->
            navigateToSalonDetail(item.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        initUI()
        setupListeners()
        setupAutoSlider()
        setupServiceData()
        
        navigateToHome()
        applyWindowInsets()
    }

    override fun onResume() {
        super.onResume()
        refreshProfileInHeader()
        if (isAtHome) resetSliderTimer()
    }

    private fun initUI() {
        with(binding) {
            rvAllTrending.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = trendingAdapter
            }
            trendingViewPager.adapter = trendingAdapter
        }
        refreshProfileInHeader()
    }

    private fun refreshProfileInHeader() {
        val name = sharedPrefs.getString("user_name", "Ajay Kumar") ?: "Ajay Kumar"
        val bio = sharedPrefs.getString("user_bio", "Beauty Enthusiast") ?: "Beauty Enthusiast"
        
        if (isAtHome) {
            binding.tvHeaderTitle.text = name
            binding.tvUserBio.text = bio
            binding.tvUserBio.isVisible = true
        }

        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it[0].uppercaseChar() }
            .joinToString("")
        binding.tvProfileIcon.text = if (initials.isNotEmpty()) initials else "AK"
    }

    private fun setupListeners() {
        with(binding) {
            btnHairCut.setOnClickListener { navigateToCategory(getString(R.string.cat_haircut)) }
            btnMakeup.setOnClickListener { navigateToCategory(getString(R.string.cat_makeup)) }
            btnFacial.setOnClickListener { navigateToCategory(getString(R.string.cat_facial)) }
            btnBridal.setOnClickListener { navigateToCategory(getString(R.string.cat_bridal)) }
            
            llHomeBack.setOnClickListener { if (!isAtHome) navigateToHome() }

            tvViewAll.setOnClickListener {
                val isListViewVisible = rvAllTrending.isVisible
                rvAllTrending.isVisible = !isListViewVisible
                trendingViewPager.isVisible = isListViewVisible
                tvViewAll.text = if (isListViewVisible) getString(R.string.view_all) else getString(R.string.show_slider)
            }

            ivSalonChat.setOnClickListener {
                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
            }

            flProfile.setOnClickListener {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }
        }
    }

    private fun setupServiceData() {
        with(binding) {
            setupCard(cardHairCut1, R.string.service_classic_haircut, R.string.price_classic_haircut)
            setupCard(cardHairCut2, R.string.service_hair_spa, R.string.price_hair_spa)
            setupCard(cardHairCut3, R.string.service_beard_trim, R.string.price_beard_trim)
            setupCard(cardHairCut4, R.string.service_hair_color, R.string.price_hair_color)
            setupCard(cardHairCut5, R.string.service_head_massage, R.string.price_head_massage)

            setupCard(cardMakeup1, R.string.service_party_makeup, R.string.price_party_makeup)
            setupCard(cardMakeup2, R.string.service_bridal_makeup, R.string.price_bridal_makeup)
            setupCard(cardMakeup3, R.string.service_engagement_makeup, R.string.price_engagement_makeup)
            setupCard(cardMakeup4, R.string.service_eye_makeup, R.string.price_eye_makeup)
            setupCard(cardMakeup5, R.string.service_airbrush_makeup, R.string.price_airbrush_makeup)

            setupCard(cardFacial1, R.string.service_fruit_facial, R.string.price_fruit_facial)
            setupCard(cardFacial2, R.string.service_gold_facial, R.string.price_gold_facial)
            setupCard(cardFacial3, R.string.service_diamond_facial, R.string.price_diamond_facial)
            setupCard(cardFacial4, R.string.service_detan_pack, R.string.price_detan_pack)
            setupCard(cardFacial5, R.string.service_hydra_facial, R.string.price_hydra_facial)

            setupCard(cardBridal1, R.string.service_full_bridal_package, R.string.price_full_bridal_package)
            setupCard(cardBridal2, R.string.service_mehendi_art, R.string.price_mehendi_art)
            setupCard(cardBridal3, R.string.service_hair_styling, R.string.price_hair_styling)
            setupCard(cardBridal4, R.string.service_saree_draping, R.string.price_saree_draping)
            setupCard(cardBridal5, R.string.service_pre_bridal_grooming, R.string.price_pre_bridal_grooming)
        }
    }

    private fun setupCard(cardBinding: ItemServiceCardBinding, titleRes: Int, priceRes: Int) {
        cardBinding.tvServiceTitle.text = getString(titleRes)
        cardBinding.tvServicePrice.text = getString(priceRes)
    }

    private fun setupAutoSlider() {
        binding.trendingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                resetSliderTimer()
            }
        })
    }

    private fun navigateToHome() {
        isAtHome = true
        backPressedCallback.isEnabled = false
        setPagesVisibility(homeVisible = true, detailVisible = true)
        updateHeader(isHome = true)
        refreshProfileInHeader()
        binding.scrollView.scrollTo(0, 0)
    }

    private fun navigateToCategory(category: String) {
        isAtHome = false
        backPressedCallback.isEnabled = true
        setPagesVisibility(targetCategoryPage = category)
        updateHeader(title = category, isHome = false)
        binding.scrollView.scrollTo(0, 0)
    }

    private fun navigateToSalonDetail(name: String) {
        isAtHome = false
        backPressedCallback.isEnabled = true
        setPagesVisibility(detailVisible = true)
        updateHeader(title = name, isHome = false, isDetail = true)
        binding.scrollView.scrollTo(0, 0)
    }

    private fun setPagesVisibility(
        homeVisible: Boolean = false,
        detailVisible: Boolean = false,
        targetCategoryPage: String? = null
    ) {
        with(binding) {
            pageHome.isVisible = homeVisible
            sectionSalonDetail.isVisible = detailVisible
            pageHairCut.isVisible = targetCategoryPage == getString(R.string.cat_haircut)
            pageMakeup.isVisible = targetCategoryPage == getString(R.string.cat_makeup)
            pageFacial.isVisible = targetCategoryPage == getString(R.string.cat_facial)
            pageBridal.isVisible = targetCategoryPage == getString(R.string.cat_bridal)
        }
    }

    private fun updateHeader(title: String = "", isHome: Boolean = false, isDetail: Boolean = false) {
        with(binding) {
            if (isHome) {
                ivHeaderIcon.setImageResource(android.R.drawable.ic_menu_mylocation)
                ivHeaderIcon.rotation = 0f
                ivHeaderArrow.isVisible = true
                ivSearchIcon.isVisible = false
                tvUserBio.isVisible = true
            } else {
                ivHeaderIcon.setImageResource(android.R.drawable.ic_menu_revert)
                ivHeaderIcon.rotation = 180f
                ivHeaderArrow.isVisible = false
                tvHeaderTitle.text = title
                tvUserBio.isVisible = false
                etSearch.setText(if (isDetail) "" else title)
                if (isDetail) etSearch.hint = getString(R.string.search_hint_home)
                ivSearchIcon.isVisible = true 
            }
            appBarLayout.setExpanded(true, true)
        }
    }

    private fun resetSliderTimer() {
        sliderHandler.removeCallbacks(sliderRunnable)
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    private val sliderRunnable = Runnable {
        if (trendingAdapter.itemCount > 0) {
            val nextItem = (binding.trendingViewPager.currentItem + 1) % trendingAdapter.itemCount
            binding.trendingViewPager.setCurrentItem(nextItem, true)
        }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getTrendingData() = listOf(
        TrendingItem("Parvan Spa", "4.8 (1.5K Reviews)", "3332"),
        TrendingItem("Glow & Glam", "4.7 (1.2K Reviews)", "2150"),
        TrendingItem("Divine Luxury", "4.9 (2K Reviews)", "4500")
    )

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}