package liberalize.kotlin.sample.payment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import liberalize.kotlin.sample.databinding.ActivityQrCodeBinding;
import liberalize.kotlin.sdk.models.QrCodeData;


public class QrCodeActivityJava extends AppCompatActivity {

  public static final String QR_CODE_DATA = "QR_CODE_DATA";

  private ActivityQrCodeBinding binding;
  private PaymentViewModel viewModel;

  private final Handler handler = new Handler(Looper.getMainLooper());

  static final Long delay = 3000L;
  private Runnable runnable;
  private QrCodeData qrCodeData;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setTitle("QR Code");
      ab.setDisplayHomeAsUpEnabled(true);
      ab.setDisplayShowHomeEnabled(true);
    }

    qrCodeData = getIntent().getParcelableExtra(QR_CODE_DATA);

    if (qrCodeData != null) {
      // show QR code
      binding.qrView.renderQrCode(qrCodeData);
      // show Pay with OCBC if possible
      binding.payWithOcbc.setQrCodeData(qrCodeData);
    }

    viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
    viewModel.getCardPaymentSuccess().observe(this, this::showSuccess);

    runnable = new Runnable() {
      @Override
      public void run() {
        // poll payment every 3s, wait for payment success and dismiss the screen
        viewModel.pollPayment(qrCodeData.paymentId);
        handler.postDelayed(this, delay);
      }
    };
  }

  private void pollPayment() {
    handler.postDelayed(runnable, delay);
  }

  @Override
  protected void onResume() {
    super.onResume();
    pollPayment();
  }

  @Override
  protected void onPause() {
    super.onPause();
    handler.removeCallbacks(runnable);
  }

  private void showSuccess(String id) {
    if (id == null) {
      return;
    }
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(id)
      .setTitle("Payment success!")
      .setPositiveButton("Done", (dialog, which) -> {
        dialog.dismiss();
        finish();
      });
    builder.create().show();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
