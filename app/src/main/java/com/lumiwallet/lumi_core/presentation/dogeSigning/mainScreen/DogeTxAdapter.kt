package com.lumiwallet.lumi_core.presentation.dogeSigning.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.entity.HeaderViewModel
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.presentation.StickyHeaderItemDecoration
import com.lumiwallet.lumi_core.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.item_view_transaction_header.view.*
import kotlinx.android.synthetic.main.item_view_transaction_input.view.*
import kotlinx.android.synthetic.main.item_view_transaction_output.view.*
import kotlinx.android.synthetic.main.view_delete_button.view.*
import kotlinx.android.synthetic.main.view_input.view.*
import kotlinx.android.synthetic.main.view_output.view.*

class DogeTxAdapter(
    private val onDeleteInputClick: (input: InputViewModel) -> Unit,
    private val onDeleteOutputClick: (output: Output) -> Unit,
    private val onAddClick: (type: Int) -> Unit,
    private val onInputClick: (input: InputViewModel) -> Unit,
    private val onOutputClick: (output: Output) -> Unit
) : RecyclerView.Adapter<DogeTxAdapter.ViewHolder>(),
    StickyHeaderItemDecoration.StickyHeaderInterface {

    private var data: MutableList<ViewItem> = mutableListOf()
    private val mViewBinderHelper = ViewBinderHelper()

    init {
        mViewBinderHelper.setOpenOnlyOne(true)
    }

    data class ViewItem(
        val type: Int,
        val input: InputViewModel? = null,
        val output: Output? = null,
        val header: HeaderViewModel? = null
    ) {
        companion object {
            const val TYPE_HEADER = 0
            const val TYPE_INPUT = 1
            const val TYPE_OUTPUT = 2
        }
    }

    fun setData(data: MutableList<ViewItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            ViewItem.TYPE_INPUT -> InputViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_transaction_input, parent, false)
            )
            ViewItem.TYPE_OUTPUT -> OutputViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_transaction_output, parent, false)
            )
            ViewItem.TYPE_HEADER -> HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_transaction_header, parent, false)
            )
            else -> throw  IllegalStateException()
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        when (holder) {
            is InputViewHolder -> {
                val id = data[position].input.toString()
                mViewBinderHelper.bind(holder.itemView.slRootInput, id)
            }
            is OutputViewHolder -> {
                val id = data[position].output.toString()
                mViewBinderHelper.bind(holder.itemView.slRootOutput, id)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = data[position].type

    override fun getHeaderPositionForItem(itemPosition: Int): Int =
        (itemPosition downTo 0)
            .map { Pair(isHeader(it), it) }
            .firstOrNull { it.first }?.second ?: RecyclerView.NO_POSITION

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.item_view_transaction_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        header.tvHeaderTitle.text = data[headerPosition].header?.title
        header.tvLabel.text = data[headerPosition].header?.button
    }

    override fun isHeader(itemPosition: Int): Boolean =
        if (itemPosition >= data.size || itemPosition == -1) {
            false
        } else {
            data[itemPosition].type == ViewItem.TYPE_HEADER
        }

    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ViewItem)
    }

    inner class HeaderViewHolder(view: View) : ViewHolder(view) {
        override fun bind(item: ViewItem) {
            itemView.tvHeaderTitle.text = item.header?.title
            itemView.tvLabel.text = item.header?.button
            itemView.tvLabel.setSafeOnClickListener {
                onAddClick(item.header?.headerType ?: -1)
            }
        }
    }

    inner class InputViewHolder(view: View) : ViewHolder(view) {
        override fun bind(item: ViewItem) {
            itemView.post {
                itemView.vDelete.layoutParams.apply {
                    this.height = itemView.height
                }
                itemView.vDelete.requestLayout()
            }
            itemView.tvValue.text = item.input?.amount.toString()
            itemView.tvAddress.text = item.input?.address.toString()
            itemView.tvHash.text = item.input?.txHash.toString()
            itemView.tvScript.text = item.input?.script.toString()
            itemView.vDelete.setSafeOnClickListener {
                itemView.slRootInput.close(true)
                onDeleteInputClick(item.input!!)
            }
            itemView.clInputView.setSafeOnClickListener {
                onInputClick(item.input!!)
            }
        }
    }

    inner class OutputViewHolder(view: View) : ViewHolder(view) {
        override fun bind(item: ViewItem) {
            itemView.post {
                itemView.vDelete.layoutParams.apply {
                    this.height = itemView.height
                }
                itemView.vDelete.requestLayout()
            }
            itemView.tvAmount.text = item.output?.amount.toString()
            itemView.tvOutAddress.text = item.output?.address.toString()
            itemView.vDelete.setSafeOnClickListener {
                itemView.slRootOutput.close(true)
                onDeleteOutputClick(item.output!!)
            }
            itemView.clOutputView.setSafeOnClickListener {
                onOutputClick(item.output!!)
            }
        }
    }
}