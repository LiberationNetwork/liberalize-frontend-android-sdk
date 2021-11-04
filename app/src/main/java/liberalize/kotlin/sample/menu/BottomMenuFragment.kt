package liberalize.kotlin.sample.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import liberalize.kotlin.sample.data.PaymentProvider
import liberalize.kotlin.sample.databinding.LayoutLiberalizeBottomSheetMenuBinding

interface BottomMenuCallback {
    fun onProviderSelected(provider: PaymentProvider?)
}

class BottomMenuFragment(
    private val selected: BottomMenuCallback
) : BottomSheetDialogFragment() {

    private val viewModel: BottomMenuViewModel by viewModels()

    private lateinit var viewBinding: LayoutLiberalizeBottomSheetMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = LayoutLiberalizeBottomSheetMenuBinding.inflate(inflater)
        viewBinding.btnCard.setOnClickListener {
            selected.onProviderSelected(viewModel.card.value)
        }
        val adapter = QrProviderAdapter(listOf()) {
            selected.onProviderSelected(it)
        }
        viewBinding.listQrProviders.adapter = adapter

        viewModel.getQrProviders().observe(viewLifecycleOwner) {
            // show QR code providers
            adapter.setNewData(it)
        }
        viewModel.card.observe(viewLifecycleOwner) {
            viewBinding.groupCard.isVisible = it != null
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            // set visibility
            viewBinding.groupCard.isGone = it
            viewBinding.groupQr.isGone = it
            viewBinding.loading.isVisible = it
        }
        return viewBinding.root
    }
}

