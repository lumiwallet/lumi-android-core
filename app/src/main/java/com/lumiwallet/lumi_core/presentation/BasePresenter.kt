package com.lumiwallet.lumi_core.presentation

import com.arellomobile.mvp.MvpPresenter
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BasePresenter<T : BaseMvpView> : MvpPresenter<T>() {

    protected var compositeDisposable = CompositeDisposable()
}