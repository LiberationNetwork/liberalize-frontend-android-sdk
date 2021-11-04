package liberalize.kotlin.sample.payment

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import liberalize.kotlin.sample.databinding.ActivityQrCodeBinding
import liberalize.kotlin.sdk.models.QrCodeData

class QrCodeActivity : AppCompatActivity() {

    companion object {
        const val QR_CODE_DATA = "QR_CODE_DATA"
    }

    private val handler = Handler(Looper.getMainLooper())

    val delay = 3000L

    private val runnable = object : Runnable {
        override fun run() {
            // poll payment every 3s, wait for payment success and dismiss the screen
            viewModel.pollPayment(qrCodeData?.paymentId)
            handler.postDelayed(this, delay)
        }
    }

    private val viewModel: PaymentViewModel by viewModels()

    var qrCodeData: QrCodeData? = null

    private lateinit var binding: ActivityQrCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        // get data from previous activity
        qrCodeData = intent.getParcelableExtra(QR_CODE_DATA) as QrCodeData?
        qrCodeData?.let {
            // show QR code
            binding.qrView.renderQrCode(it)
            // show Pay with OCBC if possible
            binding.payWithOcbc.qrCodeData = qrCodeData
        }

        viewModel.qrPaymentSuccess.observe(this) {
            it?.let { showSuccess(it) }
        }
    }

    private fun showSuccess(id: String?) {
        if (id == null) {
            return
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(id)
            .setTitle("Payment success!")
            .setPositiveButton(
                "Done"
            ) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                finish()
            }
        builder.create().show()
    }

    private fun pollPayment() {
        handler.postDelayed(runnable, delay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        pollPayment()
    }

    private fun setUpActionBar() {
        supportActionBar?.title = "QR Code"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}