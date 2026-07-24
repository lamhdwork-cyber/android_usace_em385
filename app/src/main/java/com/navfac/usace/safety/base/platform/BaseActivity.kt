package com.navfac.usace.safety.base.platform

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import com.google.gson.Gson
import com.navfac.usace.safety.R
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.htmlpage.HtmlPage
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

abstract class BaseActivity<V : ViewDataBinding> : AppCompatActivity() {
    protected val gson = Gson()

    internal lateinit var binding: V
    internal lateinit var navController: NavController
    internal lateinit var baseViewPagerAdapter: BaseViewPagerAdapter
    internal lateinit var baseSharedPreference: BaseSharedPreference

    protected lateinit var realmBookmark: Realm

    private val loadingDialog: LoadingDialog by lazy(mode = LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }


    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun getViewModel(): BaseViewModel?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = RealmConfiguration.create(
            schema = setOf(BookmarkModel::class)
        )
        realmBookmark = try {
            Realm.open(config)
        } catch (e: IllegalStateException) {
            Realm.deleteRealm(config)//delete if need
            Realm.open(config)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = this.getColor(R.color.NORMAL_STATE_COLOR)
            }
        }
        binding = DataBindingUtil.setContentView(this, layoutRes)
        baseViewPagerAdapter = BaseViewPagerAdapter(supportFragmentManager)
        baseSharedPreference = BaseSharedPreference(this)
        onCreated(savedInstanceState)
        initBaseObserver()
    }


    override fun onResume() {
        super.onResume()
        val data: Uri? = intent.data
        data?.let {
            processIntentData(data)
        }
        intent.data = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::realmBookmark.isInitialized && !realmBookmark.isClosed()) {
            realmBookmark.close()
        }
        loadingDialog.dismiss()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    fun showLoading(isLoading: Boolean) {
        loadingDialog.let {
            if (isLoading && !loadingDialog.isShowing)
                loadingDialog.show()
            else if (!isLoading && loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        }
    }

    fun <T> goToActivity(destination: Class<T>) {
        val intent = Intent(this, destination)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


    protected abstract fun onCreated(instance: Bundle?)

    protected abstract fun processIntentData(data: Uri)

    fun showMessage(message: String, isPositive: Boolean = false) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun setToolbar(show: Boolean = false, showBackButton: Boolean = false, title: String = "") {
        val actionBar = supportActionBar

        actionBar?.run {
            if (show) {
                show()
                displayOptions = ActionBar.DISPLAY_SHOW_TITLE

                setHomeButtonEnabled(showBackButton)
                setDisplayHomeAsUpEnabled(showBackButton)

                if (title != "") {
                    setDisplayShowTitleEnabled(true)
                    this@run.title = title
                } else
                    setDisplayShowTitleEnabled(false)
            } else
                hide()
        }
    }

    fun hasBookmark(page: HtmlPage): Boolean {
        return realmBookmark.query<BookmarkModel>("datafile == $0", page.getUrl())
            .first()
            .find() != null
    }

    fun isInBookmark(sectionList: ArrayList<HtmlPage>): Boolean {
        sectionList.forEach { page ->
            if (hasBookmark(page)) {
                return true
            }
        }
        return false
    }

    fun updateBookmark(@DrawableRes res: Int, button: ImageView) {
        button.setImageDrawable(ResourcesCompat.getDrawable(resources, res, null))
    }

    private fun initBaseObserver() {
        getViewModel()?.apply {
            observe(loading) {
                it?.apply { showLoading(this) }
            }
            observe(error) {
                it?.apply { showMessage(this, false) }
            }
        }
    }
}