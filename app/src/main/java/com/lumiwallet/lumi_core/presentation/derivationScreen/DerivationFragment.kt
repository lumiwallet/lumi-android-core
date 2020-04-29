package com.lumiwallet.lumi_core.presentation.derivationScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.DerivedKeyViewModel
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.copyTextToClipboard
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_derivation.*
import kotlinx.android.synthetic.main.view_derivation_details.*
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface DerivationView : BaseMvpView {
    fun showBaseInfo(
        mnemonic: String,
        seed: String
    )

    fun showDerivedKey(key: DerivedKeyViewModel)
    fun showKeysInRange(keys: MutableList<DerivedKeyViewModel>)
}

class DerivationFragment : BaseFragment(), DerivationView {

    companion object {

        private const val ARGS_MNEMONIC = "args_mnemonic"

        fun newInstance(
            mnemonic: MutableList<String>
        ): DerivationFragment = DerivationFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList(ARGS_MNEMONIC, mnemonic as ArrayList<String>)
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: DerivationPresenter

    @ProvidePresenter
    fun provideDerivationPresenter() = DerivationPresenter(
        requireArguments().getStringArrayList(ARGS_MNEMONIC) as MutableList<String>
    )

    private val layoutListener = {
        if (stub_view?.layoutParams != null) {
            BottomSheetBehavior.from(view_derivation_details).peekHeight = stub_view.height
        }
    }

    private val adapter: KeysListAdapter = KeysListAdapter {
        if (requireContext().copyTextToClipboard(it.toString())) {
            showMessage(getString(R.string.copied))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_derivation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvKeysList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvKeysList.adapter = adapter

        stub_view.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

        btBack.setSafeOnClickListener {
            presenter.onBackClicked()
        }

        btGenerate.setSafeOnClickListener {
            presenter.deriveKey(
                etBip32Path?.text.toString()
            )
        }

        btSetKeys.setSafeOnClickListener {
            presenter.deriveKeysInRange(
                etFromKey?.text.toString(),
                etToKey?.text.toString()
            )
        }

        tvMnemonic.setSafeOnClickListener {
            if (requireContext().copyTextToClipboard(tvMnemonic.text.toString())) {
                showMessage(getString(R.string.copied))
            }
        }

        tvSeed.setSafeOnClickListener {
            if (requireContext().copyTextToClipboard(tvSeed.text.toString())) {
                showMessage(getString(R.string.copied))
            }
        }

        tvXprv.setSafeOnClickListener {
            if (requireContext().copyTextToClipboard(tvXprv.text.toString())) {
                showMessage(getString(R.string.copied))
            }
        }
    }

    override fun showBaseInfo(
        mnemonic: String,
        seed: String
    ) {
        tvMnemonic.text = mnemonic
        tvSeed.text = seed
    }

    override fun showDerivedKey(key: DerivedKeyViewModel) {
        tvXprv.text = key.privKey
    }

    override fun showKeysInRange(keys: MutableList<DerivedKeyViewModel>) {
        adapter.setNewData(keys)
    }

}