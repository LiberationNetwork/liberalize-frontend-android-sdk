package liberalize.kotlin.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import liberalize.kotlin.sample.payment.PaymentActivityJava
import liberalize.kotlin.sample.payment.PaymentActivityKt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatButton>(R.id.btnJava).setOnClickListener {
            startActivity(Intent(this, PaymentActivityJava::class.java))
        }
        findViewById<AppCompatButton>(R.id.btnKotlin).setOnClickListener {
            startActivity(Intent(this, PaymentActivityKt::class.java))
        }
    }
}