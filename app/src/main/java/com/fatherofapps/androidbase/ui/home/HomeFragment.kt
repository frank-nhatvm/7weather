package com.fatherofapps.androidbase.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatherofapps.androidbase.R
import com.fatherofapps.androidbase.base.fragment.BaseFragment
import com.fatherofapps.androidbase.common.Constant
import com.fatherofapps.androidbase.common.Utils
import com.fatherofapps.androidbase.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.OffsetDateTime
import java.io.File

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentHomeBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycleViewForecastWeather()

        dataBinding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = dataBinding.edtSearch.text.toString()
                Utils.hideSoftKeyboard(dataBinding.edtSearch, requireContext())
                searchWeatherInfo(query = query)
                return@setOnEditorActionListener true
            }

            false
        }

        dataBinding.imgSearch.setOnClickListener {
            val query = dataBinding.edtSearch.text.toString()
            Utils.hideSoftKeyboard(dataBinding.edtSearch, requireContext())
            searchWeatherInfo(query = query)
        }

        homeViewModel.listWeatherInfo.observe(viewLifecycleOwner) { listWeatherInfo ->
            homeAdapter.submitList(listWeatherInfo)
        }

        registerAllExceptionEvent(homeViewModel, viewLifecycleOwner)
        registerObserverLoadingEvent(homeViewModel, viewLifecycleOwner)
    }

    private fun setupRecycleViewForecastWeather() {
        dataBinding.rcvWeatherForecast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        homeAdapter = HomeAdapter()
        dataBinding.rcvWeatherForecast.adapter = homeAdapter

        AppCompatResources.getDrawable(requireContext(), R.drawable.home_divider)
            ?.let { drawable ->
                val divider =
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                divider.setDrawable(drawable)
                dataBinding.rcvWeatherForecast.addItemDecoration(divider)
            }

    }

    private fun searchWeatherInfo(query: String) {
        if (query.length > 3) {
            val today = OffsetDateTime.now()
            homeViewModel.searchAction(
                query = query,
                cacheFolder = getCacheFolder(),
                queryDate = today
            )
        }
    }

    private fun getCacheFolder(): File {
        val cacheFolder = File(requireContext().filesDir, Constant.CACHE_FOLDER_PATH)
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs()
        }

        return cacheFolder
    }


}