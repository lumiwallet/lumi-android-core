package com.lumiwallet.lumi_core.presentation.bchSigning.addInputScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_transaction_add_input.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface AddInputView : BaseMvpView

class AddInputFragment : BaseFragment(), AddInputView {

    @InjectPresenter
    lateinit var presenter: AddInputPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_transaction_add_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btAddInput.setSafeOnClickListener {
            presenter.onAddInputClick(
                etAddress.text.toString(),
                etAmount.text.toString(),
                etOutputN.text.toString(),
                etScript.text.toString(),
                etHash.text.toString(),
                etPrivateKey.text.toString()
            )
        }

        ivAddInputBack.setSafeOnClickListener {
            presenter.onBackClick()
        }
    }

}