package com.lumiwallet.lumi_core.presentation

import com.arellomobile.mvp.MvpView

interface BaseMvpView : MvpView, Navigator {
    fun showMessage(string: String?)
}