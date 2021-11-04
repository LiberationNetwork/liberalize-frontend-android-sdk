package liberalize.kotlin.sample.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import liberalize.kotlin.sample.BuildConfig
import liberalize.kotlin.sample.R
import liberalize.kotlin.sample.data.PaymentProvider
import liberalize.kotlin.sample.databinding.ActivityPaymentBinding
import liberalize.kotlin.sample.menu.BottomMenuCallback
import liberalize.kotlin.sample.menu.BottomMenuFragment
import liberalize.kotlin.sample.utils.DialogUtil
import liberalize.kotlin.sdk.Liberalize
import liberalize.kotlin.sdk.card.MountCardResult
import liberalize.kotlin.sdk.models.MountCardData
import liberalize.kotlin.sdk.models.QrCodeData


class PaymentActivityKt : AppCompatActivity() {
    companion object {
        const val BOTTOM_MENU = "BOTTOM_MENU"
    }

    private val viewModel: PaymentViewModel by viewModels()
    lateinit var binding: ActivityPaymentBinding
    private var bottomMenuFragment: BottomMenuFragment? = null
    private var loading: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        // initialize lib
        initLib()
        initObservers()

        binding.btnSelectPayment.setOnClickListener { showMenu() }

        // register for card result
        Liberalize.instance.registerCardResult(this, object : MountCardResult {
            override fun onSuccess(cardResponse: MountCardData?) {
                bottomMenuFragment?.dismiss()
                cardResponse?.source?.let { viewModel.createCardPayment(it) }
            }

            override fun onError(msg: String?) {
                if (msg.isNullOrEmpty()) return
                showFail(msg)
            }
        })
    }

    // initObservers
    private fun initObservers() {
        viewModel.qrData.observe(this) { data ->
            data?.qrData?.let {
                startQrScreen(QrCodeData(it, data.source, data.paymentId))
            }
        }
        viewModel.isLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.cardPaymentSuccess.observe(this) {
            it?.let { it1 -> showSuccess(it1) }
        }
    }

    private fun showSuccess(id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(id).setTitle("Payment success!")
            .setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun showFail(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle("Error")
            .setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun showLoading() {
        if (!this.isFinishing) {
            if (loading == null) {
                loading = DialogUtil.createLoadingProgress(this)
                loading?.show()
            }
        }
    }

    private fun hideLoading() {
        loading?.dismiss()
        loading = null
    }

    private fun startQrScreen(data: QrCodeData) {
        val intent = Intent(this, QrCodeActivity::class.java).apply {
            putExtra(QrCodeActivity.QR_CODE_DATA, data)
        }
        startActivity(intent)
    }

    private fun setUpActionBar() {
        supportActionBar?.title = "Payment"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun onQrSelected(paymentProvider: PaymentProvider?) {
        bottomMenuFragment?.dismiss()
        paymentProvider?.source?.let { viewModel.createPayment(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showMenu() {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(BOTTOM_MENU)
        prev?.let { ft.remove(it) }
        ft.addToBackStack(null)

        bottomMenuFragment = null
        bottomMenuFragment = BottomMenuFragment(object : BottomMenuCallback {
            override fun onProviderSelected(provider: PaymentProvider?) {
                if (provider?.source == "card") {
                    Liberalize.instance.mountCard(this@PaymentActivityKt)
                } else {
                    onQrSelected(provider)
                }
            }
        })

        bottomMenuFragment?.show(supportFragmentManager, "BOTTOM_MENU")
    }

    private fun initLib() {
        // set debug mode to see logs
        Liberalize.enabledDebug = BuildConfig.DEBUG
        // initialize lib with key and env
        Liberalize.init(
            getString(R.string.libaralize_public_key),
            Liberalize.Env.STAGING
        )
    }
}