package org.mifospay.qr.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.CaptureActivity
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import org.mifospay.R
import org.mifospay.base.BaseActivity
import org.mifospay.qr.QrContract
import org.mifospay.qr.QrContract.ReadQrView
import org.mifospay.qr.presenter.ReadQrPresenter
import org.mifospay.common.Constants
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Created by naman on 7/9/17.
 */

class ReadQrActivity : CaptureActivity(), ReadQrView, ResultHandler {

    var mReadQrPresenter: QrContract.ReadQrPresenter? = null

    @JvmField
    @BindView(R.id.scannerView)
    var mScannerView: ZXingScannerView? = null

    @JvmField
    @BindView(R.id.btn_flash_on)
    var mFlashOn: ImageButton? = null

    @JvmField
    @BindView(R.id.btn_flash_off)
    var mFlashOff: ImageButton? = null

    @JvmField
    @BindView(R.id.btn_open_gallery)
    var mOpenGallery: ImageButton? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_qr)
        ButterKnife.bind(this@ReadQrActivity)
        mScannerView!!.setAutoFocus(true)
    }

    @OnClick(R.id.btn_flash_on)
    fun turnOnFlash() {
        mScannerView!!.flash = true
        mFlashOn!!.visibility = View.GONE
        mFlashOff!!.visibility = View.VISIBLE
    }

    @OnClick(R.id.btn_flash_off)
    fun turnOffFlash() {
        mScannerView!!.flash = false
        mFlashOn!!.visibility = View.VISIBLE
        mFlashOff!!.visibility = View.GONE
    }

    @OnClick(R.id.btn_open_gallery)
    fun openGallery() {
        val photoPic = Intent(Intent.ACTION_PICK)
        photoPic.type = "image/*"
        startActivityForResult(photoPic, SELECT_PHOTO)
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun handleResult(result: Result) {
        val qrData = result.text
        val returnIntent = Intent()
        returnIntent.putExtra(Constants.QR_DATA, qrData)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun setPresenter(presenter: QrContract.ReadQrPresenter?) {
        mReadQrPresenter = presenter
    }

    fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
            val returnIntent = Intent()
            returnIntent.putExtra(Constants.QR_DATA, contents)
            setResult(RESULT_OK, returnIntent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Error! $e", Toast.LENGTH_SHORT
            ).show()
        }
        return contents
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                val selectedImage = data!!.data
                if (selectedImage != null) {
                    val imageStream = contentResolver.openInputStream(selectedImage)
                    val bMap = BitmapFactory.decodeStream(imageStream)
                    scanQRImage(bMap)
                }
            } catch (e: FileNotFoundException) {
                Toast.makeText(applicationContext, "File not found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        private const val SELECT_PHOTO = 100
    }
}