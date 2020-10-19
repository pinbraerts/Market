package did.pinbraerts.market

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.widget.Toast

class EditActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: EditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        MarketData.loadPreferences(this)
        MarketData.loadPalette(this)
        MarketData.load(this)

        recyclerView = findViewById<RecyclerView>(R.id.rv_items).apply {
//            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(context)
        }

        viewAdapter = EditAdapter(
            recyclerView,
            MarketData.data,
            findViewById(R.id.w_color_picker),
            findViewById(R.id.tv_summary_cost),
        )

        recyclerView.adapter = viewAdapter

        findViewById<ImageButton>(R.id.ib_add).setOnClickListener {
            viewAdapter.add(MarketItem())
//            val pos = viewAdapter.itemCount - 1
//            recyclerView.scrollToPosition(pos)
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        findViewById<ImageButton>(R.id.ib_paste).setOnClickListener {
            if(clipboard.hasPrimaryClip() and
                (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true)
            ) {
                val data = clipboard.primaryClip?.getItemAt(0)?.text ?: ""
                if(data.isEmpty())
                    return@setOnClickListener

                viewAdapter.paste(data.toString())
            }
        }

        findViewById<ImageButton>(R.id.ib_copy).setOnClickListener {
            clipboard.setPrimaryClip(ClipData.newPlainText("simple text", viewAdapter.toPlainText()))
            Toast.makeText(this, getString(R.string.popup_copied), Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.btn_verify).setOnClickListener {
//            viewAdapter.save(this)
            startActivity(Intent(this, VerifyActivity::class.java))
        }

        findViewById<ImageButton>(R.id.ib_clear).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.remove_all_title))
                .setMessage(getString(R.string.remove_all_message))
                .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                    viewAdapter.removeAll()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()

        MarketData.loadPreferences(this)
        MarketData.loadPalette(this)
        MarketData.load(this)

        MarketTouchHelper(viewAdapter).attachToRecyclerView(recyclerView)
    }

    override fun onPause() {
        super.onPause()

        MarketData.save(this)
        MarketData.savePreferences(this)
    }
}
