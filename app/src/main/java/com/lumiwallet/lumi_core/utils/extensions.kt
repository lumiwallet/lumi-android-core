package com.lumiwallet.lumi_core.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.DisposableContainer
import io.reactivex.rxjava3.schedulers.Schedulers

fun View.showKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
        this,
        0
    )
}

fun Context.showKeyboardForced() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun Activity.hideKeyboard() {
    this.currentFocus?.let { v ->
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun Disposable.addTo(compositeDisposable: DisposableContainer) {
    compositeDisposable.add(this)
}

fun <T> Single<T>.androidAsync(): Single<T> = this.observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())

fun <T> Observable<T>.androidAsync(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())

fun Completable.androidAsync(): Completable = this.observeOn(AndroidSchedulers.mainThread())
    .subscribeOn(Schedulers.io())

fun View.setSafeOnClickListener(onSafeClick: () -> Unit) {
    setOnClickListener(SafeClickListener {
        onSafeClick()
    })
}

fun <T: Fragment> T.applyArguments(f: Bundle.() -> Unit): T = apply {
    arguments = Bundle().apply(f)
}

fun Context.copyTextToClipboard(text: String): Boolean =
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
        it.setPrimaryClip(ClipData.newPlainText("", text))
        true
    } ?: false

fun Editable?.toStringOrEmpty(): String = this?.toString().orEmpty()