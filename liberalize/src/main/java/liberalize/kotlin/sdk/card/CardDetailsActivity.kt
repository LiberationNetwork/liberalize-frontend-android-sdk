package liberalize.kotlin.sdk.card

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import liberalize.kotlin.sdk.Liberalize
import liberalize.kotlin.sdk.R
import liberalize.kotlin.sdk.databinding.ActivityCardDetailsBinding
import liberalize.kotlin.sdk.models.CardRequest
import liberalize.kotlin.sdk.models.Expiry
import liberalize.kotlin.sdk.models.MountCardData
import liberalize.kotlin.sdk.utils.ValidationUtil


internal class CardDetailsActivity : AppCompatActivity() {

    companion object {
        const val CARD_RESULT_EXTRA = "liberalize.kotlin.sdk.card_result"
        const val CARD_ERROR_EXTRA = "liberalize.kotlin.sdk.card_error"
    }

    private lateinit var binding: ActivityCardDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        listenForTextChange()
        setClicks()
    }

    /**
     * Click event
     */
    private fun setClicks() {
        binding.btnSubmit.setOnClickListener {
            if (isAllValid()) {
                submit()
            }
        }
    }

    /**
     * Action bar
     */
    private fun setupActionBar() {
        supportActionBar?.title = getString(R.string.card_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * listen for text change and set error if invalid
     */
    private fun listenForTextChange() {
        binding.edtCardNumber.apply {
            editText?.doAfterTextChanged {
                error = if (isNumberValid()) {
                    null
                } else {
                    getString(R.string.card_number_is_invalid)
                }
            }
        }
        binding.edtExpiration.apply {
            editText?.doAfterTextChanged {
                error = if (isExpirationDateValid()) {
                    null
                } else {
                    getString(R.string.expiration_date_is_invalid)
                }
            }
        }

        binding.edtCardHolderName.apply {
            editText?.doAfterTextChanged {
                error = if (isNameValid()) {
                    null
                } else {
                    getString(R.string.please_input_card_holder_name)
                }
            }
        }
        binding.edtCvv.apply {
            editText?.doAfterTextChanged {
                error = if (isSecurityCodeValid()) {
                    null
                } else {
                    getString(R.string.security_code_is_invalid)
                }
            }
        }
    }

    /**
     * Submit form
     */
    private fun submit() {
        // create request
        val request = CardRequest(
            name = binding.edtCardHolderName.editText?.text.toString(),
            securityCode = binding.edtCvv.editText?.text.toString(),
            number = binding.edtCardNumber.editText?.text.toString(),
            expiry = Expiry(
                month = binding.edtExpiration.editText?.text.toString().substring(0, 2),
                year = binding.edtExpiration.editText?.text.toString().substring(2)
            )
        )
        showLoading()
        // mount card API
        Liberalize.instance.apiClient?.mountCard(request,
            onSuccess = {
                runOnUiThread {
                    hideLoading()
                }
                // set result back
                setResult(RESULT_OK, Intent().apply {
                    it?.securityCode = request.securityCode
                    putExtra(CARD_RESULT_EXTRA, MountCardData(it?.id, it?.securityCode))
                })
                finish()
            }, onError = {
                runOnUiThread {
                    hideLoading()
                }
                // set result back
                setResult(RESULT_CANCELED, Intent().apply {
                    putExtra(CARD_ERROR_EXTRA, it)
                })
                finish()
            })
    }

    private fun showLoading() {
        binding.progressBar.show()
        binding.btnSubmit.isGone = true
    }

    private fun hideLoading() {
        binding.progressBar.hide()
        binding.btnSubmit.isVisible = true
    }

    /**
     * Validate card number
     */
    private fun isNumberValid(): Boolean {
        return ValidationUtil.isCardNumberValid(
            binding.edtCardNumber.editText?.text.toString()
        )
    }

    /**
     * Validate name
     */
    private fun isNameValid(): Boolean {
        return binding.edtCardHolderName.editText?.text.toString().isNotEmpty()
    }

    /**
     * Validate expiration date
     */
    private fun isExpirationDateValid(): Boolean {
        val input = binding.edtExpiration.editText?.text.toString()
        val month = if (input.length < 2) "" else input.substring(0, 2)
        val year = if (input.length == 4) input.substring(2) else ""
        return ValidationUtil.isExpiryDateValid(month, year)
    }

    /**
     * Validate security code
     */
    private fun isSecurityCodeValid(): Boolean {
        return binding.edtCvv.editText?.text.toString()
            .isNotEmpty() && binding.edtCvv.editText?.text.toString().length == 3
    }

    /**
     * Validate all fields
     */
    private fun isAllValid(): Boolean =
        isNumberValid() && isExpirationDateValid() && isNameValid() && isSecurityCodeValid()


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }
}