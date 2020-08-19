package com.lumiwallet.lumi_core.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.presentation.aboutScreen.AboutFragment
import com.lumiwallet.lumi_core.presentation.bchSigning.mainScreen.BchSigningFragment
import com.lumiwallet.lumi_core.presentation.btcSigning.addInputScreen.AddInputFragment
import com.lumiwallet.lumi_core.presentation.btcSigning.addOutputScreen.AddOutputFragment
import com.lumiwallet.lumi_core.presentation.btcSigning.editInputScreen.EditInputFragment
import com.lumiwallet.lumi_core.presentation.btcSigning.editOutputScreen.EditOutputFragment
import com.lumiwallet.lumi_core.presentation.btcSigning.mainScreen.BtcSigningFragment
import com.lumiwallet.lumi_core.presentation.derivationScreen.DerivationFragment
import com.lumiwallet.lumi_core.presentation.eosSigning.EosSigningFragment
import com.lumiwallet.lumi_core.presentation.generateRandomScreen.GenerateRandomFragment
import com.lumiwallet.lumi_core.presentation.mainScreen.MainFragment

class MainActivity : AppCompatActivity(),
    Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigateToFragment(MainFragment(), useBackStack = false)
    }

    override fun onBackPressed() {
        back()
    }

    override fun navigateToDerivationFragment(mnemonic: MutableList<String>) {
        navigateToFragment(DerivationFragment.newInstance(mnemonic))
    }

    override fun navigateToBtcSigningFragment() {
        navigateToFragment(BtcSigningFragment())
    }

    override fun navigateToEosSigningFragment() {
        navigateToFragment(EosSigningFragment())
    }

    override fun navigateToBchSigningFragment() {
        navigateToFragment(BchSigningFragment())
    }

    override fun navigateToGenerateRandomBytesFragment() {
        navigateToFragment(GenerateRandomFragment())
    }

    override fun navigateToAboutScreen() {
        navigateToFragment(AboutFragment())
    }

    override fun back() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun navigateToAddInputFragment() {
        navigateToFragment(AddInputFragment())
    }

    override fun navigateToAddOutputFragment() {
        navigateToFragment(AddOutputFragment())
    }

    override fun navigateToEditInputFragment(input: InputViewModel){
        navigateToFragment(EditInputFragment(input))
    }

    override fun navigateToEditOutputFragment(output: Output) {
        navigateToFragment(EditOutputFragment(output))
    }

    override fun navigateToAddBchInputFragment() {
        navigateToFragment(com.lumiwallet.lumi_core.presentation.bchSigning.addInputScreen.AddInputFragment())
    }

    override fun navigateToAddBchOutputFragment() {
        navigateToFragment(com.lumiwallet.lumi_core.presentation.bchSigning.addOutputScreen.AddOutputFragment())
    }

    override fun navigateToEditBchInputFragment(input: InputViewModel) {
        navigateToFragment(com.lumiwallet.lumi_core.presentation.bchSigning.editInputScreen.EditInputFragment(input))
    }

    override fun navigateToEditBchOutputFragment(output: Output) {
        navigateToFragment(com.lumiwallet.lumi_core.presentation.bchSigning.editOutputScreen.EditOutputFragment(output))
    }

    private fun navigateToFragment(
        fragment: Fragment,
        useBackStack: Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.screen_right_in, R.anim.screen_right_out,
                R.anim.screen_right_in, R.anim.screen_right_out
            )
            .add(R.id.flFragmentContainer, fragment, fragment::class.java.simpleName)

        if (useBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }

        transaction.commit()
    }
}
