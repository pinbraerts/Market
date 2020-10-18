package did.pinbraerts.market

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageButton
import android.widget.Toast

class VerifyActivity : AppCompatActivity() {
    /*
    Задание:
    Список из наименований

    разделитель
    Перемещение на следующее поле на энтер
    ПРоверка результата
    Кнопка удалить одно
    Кнопка удалить всё
    Кнопка Добавить (распознавание несколько)
    Менять местами
    Выгрузка для бота

    в будущем: сортировка по типу

     */

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: VerifyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        MarketData.loadPalette(this)

        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById<RecyclerView>(R.id.rv_items).apply {
//            setHasFixedSize(true)
            layoutManager = viewManager
        }
        viewAdapter = VerifyAdapter(
            recyclerView,
            MarketData.data,
            findViewById(R.id.tv_summary_cost),
            findViewById(R.id.tv_summary_error),
        )
        recyclerView.adapter = viewAdapter


//        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
//            viewAdapter.addItem(VerifyItem())
//            val pos = viewAdapter.itemCount - 1
////            recyclerView.scrollToPosition(pos)
////            recyclerView.findViewHolderForAdapterPosition(pos)
//        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        findViewById<ImageButton>(R.id.btn_copy).setOnClickListener {
            clipboard.setPrimaryClip(ClipData.newPlainText("simple text", viewAdapter.toPlainText()))
            Toast.makeText(this, "Text has been copied into clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        MarketData.load(this)
        MarketData.loadPreferences(this)
//        MarketTouchHelper(viewAdapter).attachToRecyclerView(recyclerView)
    }

    override fun onPause() {
        super.onPause()
        MarketData.save(this)
        MarketData.savePreferences(this)
    }
}