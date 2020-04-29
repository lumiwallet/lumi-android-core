package com.lumiwallet.lumi_core.presentation.derivationScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.DerivedKeyViewModel
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.item_view_key.view.*

class KeysListAdapter(
    private val onClick: (item: DerivedKeyViewModel) -> Unit
) : RecyclerView.Adapter<KeysListAdapter.ViewHolder>() {

    private val data: MutableList<DerivedKeyViewModel> = mutableListOf()

    fun setNewData(data: MutableList<DerivedKeyViewModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_view_key, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(item: DerivedKeyViewModel) {
            with(itemView) {
                tvSequence.text = item.sequence.toString()
                tvAddressBTC.text = item.btcAddress
                tvAddressEos.text = item.eosAddress
                tvPublicKey.text = item.pubKey
                tvPrivateKey.text = item.privKey
                setSafeOnClickListener {
                    onClick(item)
                }
            }
        }
    }
}