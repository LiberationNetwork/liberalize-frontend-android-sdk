package liberalize.kotlin.sample.payment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import liberalize.kotlin.sample.BuildConfig;
import liberalize.kotlin.sample.R;
import liberalize.kotlin.sample.data.PaymentProvider;
import liberalize.kotlin.sample.databinding.ActivityPaymentBinding;
import liberalize.kotlin.sample.menu.BottomMenuFragment;
import liberalize.kotlin.sample.utils.DialogUtil;
import liberalize.kotlin.sdk.Liberalize;
import liberalize.kotlin.sdk.card.MountCardResult;
import liberalize.kotlin.sdk.models.MountCardData;
import liberalize.kotlin.sdk.models.QrCodeData;

public class PaymentActivityJava extends AppCompatActivity {
  static final String BOTTOM_MENU = "BOTTOM_MENU";
  private PaymentViewModel viewModel;
  private BottomMenuFragment bm;
  private ActivityPaymentBinding binding;

  private AlertDialog loading;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityPaymentBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setTitle("Payment");
      ab.setDisplayHomeAsUpEnabled(true);
      ab.setDisplayShowHomeEnabled(true);
    }
    // initialize lib
    initLib();

    binding.btnSelectPayment.setOnClickListener(v -> {
      showMenu();
    });

    viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
    // observer for QR code data
    viewModel.getQrData().observe(this, responseDataWrapper -> {
      if (responseDataWrapper.qrData != null) {
        startQrScreen(new QrCodeData(
          responseDataWrapper.qrData,
          responseDataWrapper.source,
          responseDataWrapper.paymentId));
      }
    });
    // loading observer
    viewModel.isLoading().observe(this, isLoading -> {
      if (isLoading) {
        showLoading();
      } else {
        hideLoading();
      }
    });

    // card payment success observer
    viewModel.getCardPaymentSuccess().observe(this, this::showSuccess);

    // register for card result
    Liberalize.getInstance().registerCardResult(this, new MountCardResult() {
      @Override
      public void onSuccess(@Nullable MountCardData data) {
        bm.dismiss();
        if (data != null && data.source != null) {
          // create payment with source
          viewModel.createCardPayment(data.source);
        }
      }
      @Override
      public void onError(@Nullable String msg) {
        if (!TextUtils.isEmpty(msg)) {
          showFail(msg);
        }
      }
    });
  }

  private void startQrScreen(QrCodeData data) {
    Intent intent = new Intent(this, QrCodeActivityJava.class);
    intent.putExtra(QrCodeActivityJava.QR_CODE_DATA, data);
    startActivity(intent);
  }

  private void showMenu() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment prev = getSupportFragmentManager().findFragmentByTag(BOTTOM_MENU);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);
    bm = null;
    bm = new BottomMenuFragment(provider -> {
      if (provider == null)
        return;
      if (provider.source.equals("card")) {
        Liberalize.getInstance().mountCard(this);
      } else {
        onQrSelected(provider);
      }
    });
    bm.show(getSupportFragmentManager(), "BOTTOM_MENU");
  }

  private void onQrSelected(PaymentProvider paymentProvider) {
    bm.dismiss();
    // create QR payment with selected source
    viewModel.createPayment(paymentProvider.source);
  }

  private void showFail(String msg) {
    if (msg == null) {
      return;
    }
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(msg)
      .setTitle("Error")
      .setPositiveButton("Done", (dialog, which) -> {
        dialog.dismiss();
      });
    builder.create().show();
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
      });
    builder.create().show();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
  private void initLib() {
    // set debug mode to see logs
    Liberalize.setEnabledDebug(BuildConfig.DEBUG);
    // initialize lib with key and env
    Liberalize.init(
      getString(R.string.libaralize_public_key),
      Liberalize.Env.STAGING
    );
  }

  private void showLoading() {
    if (!this.isFinishing()) {
      if (loading == null) {
        loading = DialogUtil.createLoadingProgress(this);
        loading.show();
      }
    }
  }

  private void hideLoading() {
    if (loading != null) {
      loading.dismiss();
      loading = null;
    }
  }
}
