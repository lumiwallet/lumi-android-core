package com.lumiwallet.lumi_core.presentation.dogeSigning.mainScreen

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_btc_signing.*
import kotlinx.android.synthetic.main.view_transaction_signed.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface DogeSigningView : BaseMvpView {
    fun notifyDataChanges()
    fun setData(data: MutableList<DogeTxAdapter.ViewItem>)
    fun setupView()
    fun showRawTransaction(hash: String, rawTransaction: String)
    fun showFeePerTx(feePerTx: String)
    fun hideSignTransactionDialog()
    fun clearSignetTransactionFields()
    fun share(text: String)
}

class DogeSigningFragment : BaseFragment(),
    DogeSigningView {

    @InjectPresenter
    lateinit var presenter: DogeSigningPresenter

    private val adapter: DogeTxAdapter =
        DogeTxAdapter(
            onDeleteInputClick = { input ->
                presenter.deleteInput(input)
            },
            onDeleteOutputClick = { output ->
                presenter.deleteOutput(output)
            },
            onAddClick = { headerType ->
                presenter.addClick(headerType)
            },
            onInputClick = { input ->
                presenter.onInputClick(input)
            },
            onOutputClick = { output ->
                presenter.onOutputClick(output)
            }
        )

    private lateinit var txSignedBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getOrCreateDogeSigningComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_btc_signing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.setText(R.string.view_doge_transaction_signed_title)
        rvTransaction.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvTransaction.adapter = adapter
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
            presenter.onBuildClick()
        }

        btDone.setSafeOnClickListener {
            presenter.onDoneClicked()
        }

        ivShare.setSafeOnClickListener {
            presenter.onShareClick(tvTxHash.text.toString(), tvRawtx.text.toString())
        }

        tvShare.setSafeOnClickListener {
            presenter.onShareClick(tvTxHash.text.toString(), tvRawtx.text.toString())
        }

        ivTxSignedBack.setSafeOnClickListener {
            presenter.onSignTransactionBackClicked()
        }

        ivBack.setSafeOnClickListener {
            back()
        }

        etFee.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                presenter.onFeeChanged(editable.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        etFee.setHint(R.string.zero_placeholder)
    }

    override fun setData(data: MutableList<DogeTxAdapter.ViewItem>) {
        adapter.setData(data)
    }

    override fun hideSignTransactionDialog() {
        txSignedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun notifyDataChanges() {
        adapter.notifyDataSetChanged()
    }

    override fun setupView() {
        tvTotalFeeAmount.text = getString(R.string.fragment_btc_transaction_fee_amount).format("0")
    }

    override fun clearSignetTransactionFields() {
        tvTxHash.text = ""
        tvRawtx.text = ""
    }

    override fun showRawTransaction(hash: String, rawTransaction: String) {
        tvRawtx.text = rawTransaction
        tvTxHash.text = hash
        txSignedBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun showFeePerTx(feePerTx: String) {
        tvTotalFeeAmount.text = getString(R.string.fragment_btc_transaction_fee_amount)
            .format(feePerTx)
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