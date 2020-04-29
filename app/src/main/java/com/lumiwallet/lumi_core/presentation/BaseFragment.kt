package com.lumiwallet.lumi_core.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.utils.hideKeyboard

open class BaseFragment : MvpAppCompatFragment(), BaseMvpView {

    private lateinit var toast: Toast

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toast = Toast.makeText(requireActivity(), R.string.app_name, Toast.LENGTH_SHORT)
    }

    private val navigator: Navigator?
        get() = (activity as? Navigator)

    override fun navigateToDerivationFragment(mnemonic: MutableList<String>) {
        navigator?.navigateToDerivationFragment(mnemonic)
    }

    override fun navigateToBtcSigningFragment() {
        navigator?.navigateToBtcSigningFragment()
    }

    override fun navigateToEosSigningFragment() {
        navigator?.navigateToEosSigningFragment()
    }

    override fun navigateToAddInputFragment() {
        navigator?.navigateToAddInputFragment()
    }

    override fun navigateToAddOutputFragment() {
        navigator?.navigateToAddOutputFragment()
    }

    override fun navigateToEditInputFragment(input: InputViewModel) {
        navigator?.navigateToEditInputFragment(input)
    }

    override fun navigateToEditOutputFragment(output: Output) {
        navigator?.navigateToEditOutputFragment(output)
    }

    override fun navigateToGenerateRandomBytesFragment() {
        navigator?.navigateToGenerateRandomBytesFragment()
    }

    override fun navigateToAboutScreen() {
        navigator?.navigateToAboutScreen()
    }

    override fun back() {
        activity?.hideKeyboard()
        navigator?.back()
    }

    override fun showMessage(string: String?) {
        if (string == null) toast.setText(R.string.default_error_message)
        else toast.setText(string)
        toast.show()
    }
}