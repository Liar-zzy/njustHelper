package com.njust.helper.grade

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.jwc.GradeLevelBean
import com.njust.helper.api.jwc.JwcApi
import com.njust.helper.databinding.ActivityGradeLevelBinding
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.ParseErrorException
import com.njust.helper.tools.Prefs
import com.njust.helper.api.ServerErrorException
import com.njust.helper.tools.SimpleListVm
import com.tencent.bugly.crashreport.CrashReport
import java.io.IOException

class GradeLevelActivity : BaseActivity() {
    private val vm = SimpleListVm<GradeLevelBean>()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        refresh()
    }

    private fun refresh() {
        JwcApi.gradeLevel(Prefs.getId(this), Prefs.getJwcPwd(this))
                .subscribe({ onDataReceived(it) }, { onError(it) })
                .addToLifecycleManagement()
    }

    private fun onDataReceived(list: List<GradeLevelBean>) {
        vm.loading = false
        if (list.isEmpty()) {
            showSnack(R.string.message_no_result)
        } else {
            vm.items = list
        }
    }

    private fun onError(throwable: Throwable) {
        vm.loading = false
        when (throwable) {
            is ServerErrorException -> showSnack(R.string.message_server_error)
            is LoginErrorException -> AccountActivity.alertPasswordError(this, AccountActivity.REQUEST_JWC)
            is IOException -> showSnack(R.string.message_net_error)
            is ParseErrorException -> showSnack(R.string.message_parse_error)
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
        val binding = DataBindingUtil.setContentView<ActivityGradeLevelBinding>(this, R.layout.activity_grade_level)
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
        binding.vm = vm
    }
}
