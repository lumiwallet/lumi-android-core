package com.lumiwallet.lumi_core.presentation.aboutScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_about.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface AboutView : BaseMvpView {
    fun openLink(url: String)
    fun sendEmailToSupport()
}

class AboutFragment : BaseFragment(), AboutView {

    @InjectPresenter
    lateinit var presenter: AboutPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_about, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btBack.setSafeOnClickListener { back() }
        btFacebook.setSafeOnClickListener(presenter::onButtonFacebookClick)
        btTwitter.setSafeOnClickListener(presenter::onButtonTwitterClick)
        btReddit.setSafeOnClickListener(presenter::onButtonRedditClick)
        btMedium.setSafeOnClickListener(presenter::onButtonMediumClick)
        btTelegram.setSafeOnClickListener(presenter::onButtonTelegramClick)
        tvBlog.setSafeOnClickListener(presenter::onButtonBlogClick)
        tvWebsite.setSafeOnClickListener(presenter::onButtonSiteClick)
        tvDropUsALine.setSafeOnClickListener(presenter::onButtonSupportClick)
    }

    override fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    override fun sendEmailToSupport() {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.support_email), null)),
                ""
            )
        )
    }
}