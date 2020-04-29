package com.lumiwallet.lumi_core.presentation

import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output

interface Navigator {
    fun navigateToDerivationFragment(mnemonic: MutableList<String>)
    fun navigateToBtcSigningFragment()
    fun navigateToEosSigningFragment()
    fun navigateToAddInputFragment()
    fun navigateToAddOutputFragment()
    fun navigateToEditInputFragment(input: InputViewModel)
    fun navigateToEditOutputFragment(output: Output)

    fun navigateToGenerateRandomBytesFragment()
    fun navigateToAboutScreen()

    fun back()
}