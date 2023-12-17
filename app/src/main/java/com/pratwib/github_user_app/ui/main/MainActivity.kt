package com.pratwib.github_user_app.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratwib.github_user_app.R
import com.pratwib.github_user_app.adapter.OnItemClickCallback
import com.pratwib.github_user_app.adapter.UserAdapter
import com.pratwib.github_user_app.databinding.ActivityMainBinding
import com.pratwib.github_user_app.datasource.UserResponse
import com.pratwib.github_user_app.networking.NetworkConnection
import com.pratwib.github_user_app.repository.UserRepository
import com.pratwib.github_user_app.ui.detail.DetailActivity
import com.pratwib.github_user_app.ui.favorite.FavoriteActivity
import com.pratwib.github_user_app.ui.setting.SettingActivity
import com.pratwib.github_user_app.ui.setting.SettingPreferences

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityMainBinding

    private val adapter: UserAdapter by lazy {
        UserAdapter()
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
        setViewModel()
        observeAnimationAndProgressBar()
        darkModeCheck()
        checkInternetConnection()
        setUpSearchView()
    }

    private fun setViewModel() {
        val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
    }

    private fun darkModeCheck() {
        mainViewModel.getThemeSettings().observe(this@MainActivity) { isDarkModeActive ->
            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this)
    }

    private fun setUpSearchView() {
        with(binding) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    showFailedLoadData(false)
                    showProgressBar(true)
                    UserRepository.getUserBySearch(query)
                    mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
                        if (searchUserResponse != null) {
                            adapter.addDataToList(searchUserResponse)
                            searchView.clearFocus()
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun observeAnimationAndProgressBar() {
        mainViewModel.isLoading.observe(this) {
            showProgressBar(it)
        }
        mainViewModel.isDataFailed.observe(this) {
            showFailedLoadData(it)
        }
    }

    private fun checkInternetConnection() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                showFailedLoadData(false)
                mainViewModel.user.observe(this) { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                }
                mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
                    if (searchUserResponse != null) {
                        adapter.addDataToList(searchUserResponse)
                        binding.rvMain.visibility = View.VISIBLE
                    }
                }
            } else {
                mainViewModel.user.observe(this) { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                }
                Toast.makeText(this@MainActivity, "Tidak ada koneksi internet", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun hideUserList() {
        binding.rvMain.layoutManager = null
        binding.rvMain.adapter = null
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Suppress("SameParameterValue")
    private fun showFailedLoadData(isFailed: Boolean) {
        binding.tvFailed.visibility = if (isFailed) View.VISIBLE else View.GONE
    }

    private fun setUserData() {
        with(binding) {
            val layoutManager =
                LinearLayoutManager(this@MainActivity)
            rvMain.layoutManager = layoutManager
            rvMain.adapter = adapter
            adapter.setOnItemClickCallback(object : OnItemClickCallback {
                override fun onItemClicked(user: UserResponse) {
                    hideUserList()
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.KEY_USER, user)
                    intent.putExtra(DetailActivity.KEY_USERNAME, user.login)
                    intent.putExtra(DetailActivity.KEY_ID, user.id)
                    startActivity(intent)
                }
            })
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.btn_setting -> {
                val setting = Intent(this, SettingActivity::class.java)
                startActivity(setting)
                true
            }

            R.id.btn_favorite -> {
                val favorite = Intent(this, FavoriteActivity::class.java)
                startActivity(favorite)
                true
            }

            else -> false
        }
    }
}
