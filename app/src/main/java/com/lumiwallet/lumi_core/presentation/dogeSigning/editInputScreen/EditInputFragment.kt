package com.lumiwallet.lumi_core.presentation.dogeSigning.editInputScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.applyArguments
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_transaction_add_input.*

interface EditInputView: BaseMvpView {
    fun showInput(
        address: String,
        value: String,
        outN: String,
        txHash: String,
        script: String,
        ketAsWif: String
    )
}

class EditInputFragment private constructor(): BaseFragment(), EditInputView {

    constructor(
        input: InputViewModel
    ) : this() {
        applyArguments {
            putParcelable(ARGS_INPUT, input)
        }
    }

    companion object {
        private const val ARGS_INPUT = "args_input"
    }

    @InjectPresenter
    lateinit var presenter: EditInputPresenter

    @ProvidePresenter
    fun provideEditInputPresenter() : EditInputPresenter = EditInputPresenter(
        arguments?.getParcelable(ARGS_INPUT) ?: throw IllegalArgumentException()
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_transaction_add_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btAddInput.setText(R.string.fragment_edit_input_save)
        tvTitleAddress.setText(R.string.fragment_add_input_doge_address)
        etAddress.setHint(R.string.fragment_add_input_doge_address_hint)

        ivAddInputBack.setSafeOnClickListener {
            back()
        }
        btAddInput.setSafeOnClickListener {
            presenter.onSaveClick(
                etAddress.text.toString(),
                etAmount.text.toString(),
                etOutputN.text.toString(),
                etScript.text.toString(),
                etHash.text.toString(),
                etPrivateKey.text.toString()
            )
        }
    }

    override fun showInput(
        address: String,
        value: String,
        outN: String,
        txHash: String,
        script: String,
        ketAsWif: String
    ) {
        etAddress.setText(address)
        etAmount.setText(value)
        etOutputN.setText(outN)
        etScript.setText(script)
        etHash.setText(txHash)
        etPrivateKey.setText(ketAsWif)
    }


}