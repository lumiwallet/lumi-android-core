package com.lumiwallet.lumi_core.presentation.generateRandomScreen

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.presentation.BaseFragment
import com.lumiwallet.lumi_core.presentation.BaseMvpView
import kotlinx.android.synthetic.main.fragment_generate_random.*

interface GenerateRandomView : BaseMvpView {
    fun requestFilesystemPermission()
}

class GenerateRandomFragment : BaseFragment(), GenerateRandomView {

    companion object {

        private const val PERMISSION_REQUEST_CODE = 42
    }

    @InjectPresenter
    lateinit var presenter: GenerateRandomPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_generate_random, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btGenerate.setOnClickListener {
            presenter.onGenerateClick(etLength.text.toString())
        }
        btBack.setOnClickListener {
            back()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            presenter.onPermissionGranted(etLength.text.toString())
        } else {
            showMessage("permission denied!")
        }
    }

    override fun requestFilesystemPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

}