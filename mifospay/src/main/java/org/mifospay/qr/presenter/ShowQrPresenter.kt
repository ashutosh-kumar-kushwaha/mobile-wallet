package org.mifospay.qr.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.base.BaseView
import org.mifospay.qr.QrContract
import org.mifospay.qr.QrContract.ShowQrView
import org.mifospay.qr.domain.usecase.GenerateQr
import javax.inject.Inject

/**
 * Created by naman on 8/7/17.
 */
class ShowQrPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    QrContract.ShowQrPresenter {

    @Inject
    lateinit var generateQrUseCase: GenerateQr

    private lateinit var mShowQrView: ShowQrView
    override fun attachView(baseView: BaseView<*>?) {
        mShowQrView = baseView as ShowQrView
        mShowQrView.setPresenter(this)
    }

    override fun generateQr(data: String?) {
        mUsecaseHandler.execute(
            generateQrUseCase, GenerateQr.RequestValues(data!!),
            object : UseCaseCallback<GenerateQr.ResponseValue?> {
                override fun onSuccess(response: GenerateQr.ResponseValue?) {
                    if (response != null) {
                        mShowQrView.showGeneratedQr(response.bitmap)
                    }
                }

                override fun onError(message: String) {}
            })
    }
}