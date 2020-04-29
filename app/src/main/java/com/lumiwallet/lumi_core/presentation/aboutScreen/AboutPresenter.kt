package com.lumiwallet.lumi_core.presentation.aboutScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.presentation.BasePresenter

@InjectViewState
class AboutPresenter : BasePresenter<AboutView>() {

    companion object {
        private const val FACEBOOK_URL = "https://www.facebook.com/lumiwallet"
        private const val TWITTER_URL = "https://twitter.com/Lumi_wallet"
        private const val REDDIT_URL = "https://www.reddit.com/r/LumiWallet"
        private const val MEDIUM_URL = "https://medium.com/lumiwallet"
        private const val TELEGRAM_URL = "https://t.me/lumipublic"
        private const val BLOG_URL = "https://blog.lumiwallet.com/"
        private const val SITE_URL = "https://lumiwallet.com/"
    }

    fun onButtonFacebookClick() {
        viewState.openLink(FACEBOOK_URL)
    }

    fun onButtonTwitterClick() {
        viewState.openLink(TWITTER_URL)
    }

    fun onButtonRedditClick() {
        viewState.openLink(REDDIT_URL)
    }

    fun onButtonMediumClick() {
        viewState.openLink(MEDIUM_URL)
    }

    fun onButtonTelegramClick() {
        viewState.openLink(TELEGRAM_URL)
    }

    fun onButtonBlogClick() {
        viewState.openLink(BLOG_URL)
    }

    fun onButtonSiteClick() {
        viewState.openLink(SITE_URL)
    }

    fun onButtonSupportClick() {
        viewState.sendEmailToSupport()
    }
}