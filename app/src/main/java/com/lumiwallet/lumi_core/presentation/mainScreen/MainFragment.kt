package com.lumiwallet.lumi_core.presentation.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import com.lumiwallet.lumi_core.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.view_import_mnemonic.*
import kotlinx.android.synthetic.main.view_select_mnemonic_words.*

interface MainView : BaseMvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showImportBottomSheet()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideImportBottomSheet()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCreateBottomSheet(mnemonicSizes: Array<String>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideCreateBottomSheet()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showImportError()

}

class MainFragment : BaseFragment(), MainView {

    private lateinit var createMnemonicBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var importMnemonicBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        importMnemonicBottomSheetBehavior = BottomSheetBehavior.from(clBottomSheetImportMnemonic)
        importMnemonicBottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, scrollValue: Float) = Unit
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    activity?.hideKeyboard()
                }
            }
        })

        createMnemonicBottomSheetBehavior = BottomSheetBehavior.from(clBottomSheetCreateMnemonic)
        createMnemonicBottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, scrollValue: Float) {
                bottomSheetBackground.alpha = scrollValue / 2
            }

            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBackground.isClickable = true
                    bottomSheetBackground.setOnClickListener {
                        presenter.onClickOutsideOfCreateBottomSheet()
                    }
                } else {
                    bottomSheetBackground.setOnClickListener(null)
                    bottomSheetBackground.isClickable = false
                }
            }
        })

        btSelectingMnemonicSizeIsDone.setOnClickListener {
            presenter.onSelectingDoneClicked(numberPicker.displayedValues[numberPicker.value - 1])
        }

        tvImportMnemonic.setOnClickListener {
            presenter.onImportButtonClicked()
        }

        tvCreateMnemonic.setOnClickListener {
            presenter.onCreateMnemonicClick()
        }

        tvCreateBtcTx.setOnClickListener {
            navigateToBtcSigningFragment()
        }

        tvCreateEosTx.setOnClickListener {
            navigateToEosSigningFragment()
        }

        tvCreateBchTx.setOnClickListener {
            navigateToBchSigningFragment()
        }

        tvAbout.setOnClickListener {
            presenter.onAboutClicked()
        }

        ivBack.setOnClickListener {
            presenter.onImportBackClicked()
        }

        btConfirmImport.setOnClickListener {
            presenter.onImportConfirmClicked(etMnemonic.text?.toString().orEmpty())
        }

        tvGenerate.setOnClickListener {
            presenter.onGenerateClick()
        }
    }

    override fun showImportBottomSheet() {
        importMnemonicBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideImportBottomSheet() {
        importMnemonicBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        etMnemonic.text = null
    }

    override fun showCreateBottomSheet(mnemonicSizes: Array<String>) {
        numberPicker.minValue = 1
        numberPicker.maxValue = mnemonicSizes.size
        numberPicker.displayedValues = mnemonicSizes
        createMnemonicBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideCreateBottomSheet() {
        createMnemonicBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun showImportError() {
        Toast.makeText(
            requireContext(),
            R.string.bottom_sheet_import_mnemonic_error,
            Toast.LENGTH_SHORT
        ).show()
    }

}
