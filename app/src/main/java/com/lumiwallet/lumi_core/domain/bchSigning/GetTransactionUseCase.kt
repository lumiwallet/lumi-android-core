package com.lumiwallet.lumi_core.domain.bchSigning

import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.HeaderViewModel
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import com.lumiwallet.lumi_core.presentation.bchSigning.mainScreen.BchTxAdapter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction

class GetTransactionUseCase constructor(
    private val txRepository: BtcTransactionRepository,
    private val resourcesProvider: ResourcesProvider
) {

    operator fun invoke(): Observable<MutableList<BchTxAdapter.ViewItem>> =
        Observable.combineLatest(
            txRepository.getInputs(),
            txRepository.getOutputs(),
            BiFunction { inputs, outputs ->
                val viewItems: MutableList<BchTxAdapter.ViewItem> = mutableListOf()
                viewItems.add(
                    BchTxAdapter.ViewItem(
                        type = BchTxAdapter.ViewItem.TYPE_HEADER,
                        header = HeaderViewModel(
                            resourcesProvider.getString(R.string.view_inputs_header_title).blockingGet(),
                            resourcesProvider.getString(R.string.view_inputs_header_add_button).blockingGet(),
                            HeaderViewModel.HEADER_TYPE_INPUT
                        )
                    )
                )
                viewItems.addAll(inputs.map { input ->
                    BchTxAdapter.ViewItem(
                        type = BchTxAdapter.ViewItem.TYPE_INPUT,
                        input = InputViewModel(
                            input.address,
                            input.value.toString(),
                            input.script,
                            input.txHash
                        )
                    )
                })
                viewItems.add(
                    BchTxAdapter.ViewItem(
                        type = BchTxAdapter.ViewItem.TYPE_HEADER,
                        header = HeaderViewModel(
                            resourcesProvider.getString(R.string.view_outputs_header_title).blockingGet(),
                            resourcesProvider.getString(R.string.view_outputs_header_add_button).blockingGet(),
                            HeaderViewModel.HEADER_TYPE_OUTPUT
                        )
                    )
                )
                viewItems.addAll(outputs.map { output ->
                    BchTxAdapter.ViewItem(
                        type = BchTxAdapter.ViewItem.TYPE_OUTPUT,
                        output = Output(
                            address = output.address,
                            amount = output.amount
                        )
                    )
                })

                viewItems
            }
        )
}