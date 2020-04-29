package com.lumiwallet.lumi_core.domain.btcSigning

import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.HeaderViewModel
import com.lumiwallet.lumi_core.domain.entity.Input
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import com.lumiwallet.lumi_core.presentation.btcSigning.mainScreen.BtcTxAdapter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction

class GetTransactionUseCase constructor(
    private val txRepository: BtcTransactionRepository,
    private val resourcesProvider: ResourcesProvider
) {

    operator fun invoke(): Observable<MutableList<BtcTxAdapter.ViewItem>> =
        Observable.combineLatest(
            txRepository.getInputs(),
            txRepository.getOutputs(),
            BiFunction<MutableList<Input>, MutableList<Output>, MutableList<BtcTxAdapter.ViewItem>> { inputs, outputs ->
                val viewItems: MutableList<BtcTxAdapter.ViewItem> = mutableListOf()
                viewItems.add(
                    BtcTxAdapter.ViewItem(
                        type = BtcTxAdapter.ViewItem.TYPE_HEADER,
                        header = HeaderViewModel(
                            resourcesProvider.getString(R.string.view_inputs_header_title).blockingGet(),
                            resourcesProvider.getString(R.string.view_inputs_header_add_button).blockingGet(),
                            HeaderViewModel.HEADER_TYPE_INPUT
                        )
                    )
                )
                viewItems.addAll(inputs.map { input ->
                    BtcTxAdapter.ViewItem(
                        type = BtcTxAdapter.ViewItem.TYPE_INPUT,
                        input = InputViewModel(
                            input.address,
                            input.unspentOutput.value.toString(),
                            input.unspentOutput.script,
                            input.unspentOutput.txHash
                        )
                    )
                })
                viewItems.add(
                    BtcTxAdapter.ViewItem(
                        type = BtcTxAdapter.ViewItem.TYPE_HEADER,
                        header = HeaderViewModel(
                            resourcesProvider.getString(R.string.view_outputs_header_title).blockingGet(),
                            resourcesProvider.getString(R.string.view_outputs_header_add_button).blockingGet(),
                            HeaderViewModel.HEADER_TYPE_OUTPUT
                        )
                    )
                )
                viewItems.addAll(outputs.map { output ->
                    BtcTxAdapter.ViewItem(
                        type = BtcTxAdapter.ViewItem.TYPE_OUTPUT,
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