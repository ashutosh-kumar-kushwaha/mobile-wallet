package org.mifospay.bank.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import butterknife.BindView
import butterknife.ButterKnife
import com.alimuzaffar.lib.pin.PinEntryEditText
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.R
import org.mifospay.bank.ui.SetupUpiPinActivity
import org.mifospay.base.BaseFragment
import org.mifospay.common.Constants
import org.mifospay.utils.Toaster

/**
 * Created by ankur on 13/July/2018
 */
@AndroidEntryPoint
class OtpFragment : BaseFragment() {
    @JvmField
    @BindView(R.id.tv_title)
    var mTvTitle: TextView? = null

    @JvmField
    @BindView(R.id.pe_otp)
    var mPeOtp: PinEntryEditText? = null
    private var otp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            otp = requireArguments().getString(Constants.OTP)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_otp, container, false) as ViewGroup
        ButterKnife.bind(this, rootView)
        mPeOtp!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                okayClicked()
                return@OnEditorActionListener true
            }
            false
        })
        mPeOtp!!.requestFocus()
        return rootView
    }

    fun okayClicked() {
        if (activity is SetupUpiPinActivity) {
            if (mPeOtp!!.text.toString() == otp) {
                (activity as SetupUpiPinActivity?)!!.otpVerified()
            } else {
                showToast(getString(R.string.wrong_otp))
            }
        }
    }

    fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }

    companion object {
        fun newInstance(otp: String?): OtpFragment {
            val args = Bundle()
            args.putString(Constants.OTP, otp)
            val fragment = OtpFragment()
            fragment.arguments = args
            return fragment
        }
    }
}