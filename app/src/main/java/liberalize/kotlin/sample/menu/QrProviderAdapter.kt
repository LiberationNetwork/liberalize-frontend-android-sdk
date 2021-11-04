package liberalize.kotlin.sample.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.widget.ANImageView
import liberalize.kotlin.sample.R
import liberalize.kotlin.sample.data.PaymentProvider


class QrProviderAdapter(
    private var dataSet: List<PaymentProvider>,
    private val onItemClick: (PaymentProvider) -> Unit
) : RecyclerView.Adapter<QrProviderAdapter.ViewHolder>() {

    fun setNewData(newData: List<PaymentProvider>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ANImageView = view.findViewById(R.id.logo)
        val tv: AppCompatTextView = view.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_qr_provider, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = dataSet[position]
        viewHolder.img.setImageUrl(data.image)
        viewHolder.tv.text = data.source
        viewHolder.itemView.setOnClickListener {
            onItemClick.invoke(data)
        }
    }

    override fun getItemCount() = dataSet.size

}
