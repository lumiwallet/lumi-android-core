package com.lumiwallet.lumi_core.presentation.dogeSigning.editOutputScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.applyArguments
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_transaction_add_output.*

interface EditOutputView : BaseMvpView {
    fun showOutput(address: String, amount: String)
}

class EditOutputFragment private constructor() : BaseFragment(), EditOutputView {

    constructor(
        output: Output
    ) : this() {
        applyArguments {
            putParcelable(ARGS_OUTPUT, output)
        }
    }

    companion object {
        private const val ARGS_OUTPUT = "args_output"
    }


    @InjectPresenter
    lateinit var presenter: EditOutputPresenter

    @ProvidePresenter
    fun provideEditOutputPresenter() : EditOutputPresenter = EditOutputPresenter(
        arguments?.getParcelable(ARGS_OUTPUT) ?: throw IllegalArgumentException()
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_transaction_add_output, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btAddOutput.setText(R.string.fragment_edit_input_save)
        tvTitleAddress.setText(R.string.fragment_add_output_doge_address)
        etOutAddress.setHint(R.string.fragment_add_output_doge_address_hint)

        ivAddOutputBack.setSafeOnClickListener {
            back()
        }
        btAddOutput.setSafeOnClickListener {
            presenter.onSaveClick(
                etOutAddress.text.toString(),
                etOutAmount.text.toString()
            )
        }
    }

    override fun showOutput(
        address: String,
        amount: String
    ) {
        etOutAddress.setText(address)
        etOutAmount.setText(amount)
    }
}