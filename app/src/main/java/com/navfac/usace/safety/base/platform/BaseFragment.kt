package com.navfac.usace.safety.base.platform

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.navfac.usace.safety.base.extension.observe
import com.navfac.usace.safety.features.MainActivity
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import com.navfac.usace.safety.features.formfigure.FormFigureBaseActivity
import io.reactivex.disposables.CompositeDisposable
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

abstract class BaseFragment<V : ViewDataBinding> : Fragment() {
    val disposeBag = CompositeDisposable()

    lateinit var binding: V
    lateinit var baseView: View
    internal lateinit var baseViewPagerAdapter: BaseViewPagerAdapter
    internal lateinit var baseSharedPreference: BaseSharedPreference

    private val bookmarkConfig by lazy {
        RealmConfiguration.create(
            schema = setOf(BookmarkModel::class)
        )
    }

    val realmBookmark: Realm by lazy {
        try {
            Realm.open(bookmarkConfig)
        } catch (e: IllegalStateException) {
            Realm.deleteRealm(bookmarkConfig)//delete if need
            Realm.open(bookmarkConfig)
        }
    }

    internal fun getMainActivity() = this.activity as MainActivity
    internal fun getFormFigureBaseActivity() = this.activity as FormFigureBaseActivity

    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun getViewModel(): BaseViewModel?

    protected abstract fun onCreated(savedInstance: Bundle?)

    abstract fun backPressedAction()

    abstract fun setupActions()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding  = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        baseView = binding.root
        baseViewPagerAdapter = BaseViewPagerAdapter(requireActivity().supportFragmentManager)
        baseSharedPreference = BaseSharedPreference(requireContext())
        return baseView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreated(savedInstanceState)
        initBaseObserver()
    }

    override fun onResume() {
        super.onResume()
        setupActions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeBag.clear()
    }

    fun notify(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    fun showMessage(message: String, isPositive: Boolean = false) {
        (this.activity as BaseActivity<*>).showMessage(message, isPositive)
    }

    fun <T> goToActivity(destination: Class<T>) {
        val intent = Intent(activity, destination)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.activity?.startActivity(intent)
        this.activity?.finish()
    }

    protected fun showLoading(loading: Boolean) {
        activity?.let { a -> (a as BaseActivity<*>).showLoading(loading) }
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