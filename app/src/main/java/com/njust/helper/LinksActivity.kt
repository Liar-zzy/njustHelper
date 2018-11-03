package com.njust.helper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityLinksBinding
import com.njust.helper.model.Link
import com.njust.helper.tools.SimpleListVm
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.Single
import java.io.IOException


class LinksActivity : BaseActivity() {
    private val vm = SimpleListVm<Link>().apply {
        listener = { _, link, _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link.url)
            startActivity(intent)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        refresh()
    }

    private fun refresh() {
        Single
                .fromCallable<List<Link>> {
                    resources.openRawResource(R.raw.links).bufferedReader().use { it.readText() }
                            .let { Gson().fromJson<List<Link>>(it, object : TypeToken<List<Link>>() {}.type) }
                }
                .subscribe({ onDataReceived(it) }, { onError(it) })
                .addToLifecycleManagement()
    }

    private fun onDataReceived(list: List<Link>) {
        vm.loading = false
        vm.items = list
    }

    private fun onError(throwable: Throwable) {
        vm.loading = false
        when (throwable) {
            is IOException -> showSnack(R.string.message_net_error)
            else -> {
                if (BuildConfig.DEBUG) {
                    throwable.printStackTrace()
                    throw throwable
                }
                CrashReport.postCatchedException(throwable)
            }
        }
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityLinksBinding>(this, R.layout.activity_links)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
        binding.vm = vm
    }
}
