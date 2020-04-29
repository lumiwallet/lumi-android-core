package com.lumiwallet.lumi_core.presentation.eosSigning

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import com.lumiwallet.lumi_core.utils.toStringOrEmpty
import kotlinx.android.synthetic.main.fragment_eos_signing.*
import kotlinx.android.synthetic.main.view_eos_transaction_signed.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface EosSigningView : BaseMvpView {
    fun clearSignetTransactionFields()
    fun hideSignTransactionDialog()
    fun showRawTransaction(packedTx: String, signature: String)
    fun share(text: String)
}

class EosSigningFragment : BaseFragment(), EosSigningView {

    @InjectPresenter
    lateinit var presenter: EosSigningPresenter

    private lateinit var txSignedBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_eos_signing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txSignedBehavior = BottomSheetBehavior.from(vTxSigned)
        txSignedBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, p1: Float) {}
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    presenter.txSignedDialogCollapsed()
                }
            }
        })

        btBuild.setSafeOnClickListener {
            presenter.onBuildClick(
                etAddress?.text.toStringOrEmpty(),
                etAmount?.text.toStringOrEmpty(),
                etMemo?.text.toStringOrEmpty(),
                etActor?.text.toStringOrEmpty(),
                etPermission?.text.toStringOrEmpty(),
                etExpiration?.text.toStringOrEmpty(),
                etBin?.text.toStringOrEmpty(),
                etBlockPrefix?.text.toStringOrEmpty(),
                etBlockNum?.text.toStringOrEmpty(),
                etChainId?.text.toStringOrEmpty(),
                etPrivateKey?.text.toStringOrEmpty()
            )
        }

        ivBack.setSafeOnClickListener {
            back()
        }

        btDone.setSafeOnClickListener {
            presenter.onDoneClicked()
        }

        ivShare.setSafeOnClickListener {
            presenter.onShareClick(tvTx.text.toString(), tvSignatures.text.toString())
        }

        tvShare.setSafeOnClickListener {
            presenter.onShareClick(tvTx.text.toString(), tvSignatures.text.toString())
        }
    }

    override fun clearSignetTransactionFields() {
        tvTx.text = ""
        tvSignatures.text = ""
    }

    override fun hideSignTransactionDialog() {
        txSignedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun showRawTransaction(packedTx: String, signature: String) {
        tvSignatures.text = signature
        tvTx.text = packedTx
        txSignedBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun share(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }
}