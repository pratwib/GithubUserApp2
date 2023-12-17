package com.pratwib.github_user_app.ui.detail

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.pratwib.github_user_app.R
import com.pratwib.github_user_app.adapter.SectionPagerAdapter
import com.pratwib.github_user_app.database.FavoriteEntity
import com.pratwib.github_user_app.databinding.ActivityDetailBinding
import com.pratwib.github_user_app.datasource.UserResponse
import com.pratwib.github_user_app.networking.NetworkConnection

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailDataLayout.visibility = View.GONE
        val username = intent.getStringExtra(KEY_USERNAME)
        username?.let {
            checkInternetConnection(it)
        }
    }

    private fun checkInternetConnection(username: String) {
        val user = intent.getParcelableExtra<UserResponse>(KEY_USER)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                val favorite = FavoriteEntity()
                favorite.login = username
                favorite.id = intent.getIntExtra(KEY_ID, 0)
                favorite.avatar_url = user?.avatarUrl
                val detailViewModel: DetailViewModel by viewModels {
                    DetailViewModelFactory(username, application)
                }
                detailViewModel.isLoading.observe(this@DetailActivity) {
                    showProgressBar(it)
                }
                detailViewModel.isDataFailed.observe(this@DetailActivity) {
                    showFailedLoadData(it)
                }
                detailViewModel.detailUser.observe(this@DetailActivity) { userResponse ->
                    if (userResponse != null) {
                        setData(userResponse)
                        setTabLayoutAdapter(userResponse)
                    }
                }
                detailViewModel.getFavoriteById(favorite.id!!)
                    .observe(this@DetailActivity) { listFav ->
                        isFavorite = listFav.isNotEmpty()

                        binding.detailFabFavorite.imageTintList = if (listFav.isEmpty()) {
                            ColorStateList.valueOf(Color.rgb(255, 255, 255))
                        } else {
                            ColorStateList.valueOf(Color.rgb(247, 106, 123))
                        }

                    }

                binding.detailFabFavorite.apply {
                    setOnClickListener {
                        if (isFavorite) {
                            detailViewModel.delete(favorite)
                            Toast.makeText(
                                this@DetailActivity,
                                "${favorite.login} telah dihapus dari data User Favorite ",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            detailViewModel.insert(favorite)
                            Toast.makeText(
                                this@DetailActivity,
                                "${favorite.login} telah ditambahkan ke data User Favorite",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            } else {
                binding.detailDataLayout.visibility = View.GONE
            }
        }
    }

    private fun setTabLayoutAdapter(user: UserResponse) {
        val sectionPagerAdapter = SectionPagerAdapter(this@DetailActivity)
        sectionPagerAdapter.model = user
        binding.detailViewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(binding.detailTabs, binding.detailViewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

    }

    private fun setData(userResponse: UserResponse?) {
        if (userResponse != null) {
            with(binding) {
                detailDataLayout.visibility = View.VISIBLE
                detailImage.visibility = View.VISIBLE
                Glide.with(root)
                    .load(userResponse.avatarUrl)
                    .circleCrop()
                    .into(binding.detailImage)
                detailName.visibility = View.VISIBLE
                detailUsername.visibility = View.VISIBLE
                detailName.text = userResponse.name
                detailUsername.text = userResponse.login
                if (userResponse.company != null) {
                    detailCompany.visibility = View.VISIBLE
                    detailCompany.text = userResponse.company
                } else {
                    detailCompany.visibility = View.GONE
                }
                if (userResponse.location != null) {
                    detailLocation.visibility = View.VISIBLE
                    detailLocation.text = userResponse.location
                } else {
                    detailLocation.visibility = View.GONE
                }
                if (userResponse.followers != null) {
                    detailFollowersValue.visibility = View.VISIBLE
                    detailFollowersValue.text = userResponse.followers.toString()
                } else {
                    detailFollowersValue.visibility = View.GONE
                }
                if (userResponse.followers != null) {
                    detailFollowers.visibility = View.VISIBLE
                } else {
                    detailFollowers.visibility = View.GONE
                }
                if (userResponse.following != null) {
                    detailFollowingValue.visibility = View.VISIBLE
                    detailFollowingValue.text = userResponse.following.toString()
                } else {
                    detailFollowingValue.visibility = View.GONE
                }
                if (userResponse.following != null) {
                    detailFollowing.visibility = View.VISIBLE
                } else {
                    detailFollowing.visibility = View.GONE
                }
                if (userResponse.publicRepos != null) {
                    detailRepoValue.visibility = View.VISIBLE
                    detailRepoValue.text = userResponse.publicRepos.toString()
                } else {
                    detailRepoValue.visibility = View.GONE
                }
                if (userResponse.publicRepos != null) {
                    detailRepo.visibility = View.VISIBLE
                } else {
                    detailRepo.visibility = View.GONE
                }
            }
        } else {
            Log.i(TAG, "setData function is error")
        }
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showFailedLoadData(isFailed: Boolean) {
        binding.tvFailed.visibility = if (isFailed) View.VISIBLE else View.GONE

    }

    companion object {
        const val KEY_USER = "user"
        private const val TAG = "DetailActivity"
        const val KEY_USERNAME = "username"
        const val KEY_ID = "extra id"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

}