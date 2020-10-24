package did.pinbraerts.market

import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private class HeaderViewHolder(
        view: View,
        val ib_weight: ImageButton = view.findViewById(R.id.ib_weight),
        val ib_price: ImageButton = view.findViewById(R.id.ib_price),
        val ib_cost: ImageButton = view.findViewById(R.id.ib_cost),
        val ib_discrepancy: ImageButton = view.findViewById(R.id.ib_discrepancy),
    ) {
        fun setState(state: State) =
            when(state) {
                State.PLAN -> {
                    ib_price.hide()
                    ib_cost.hide()
                    ib_discrepancy.hide()
                }
                State.BUY -> {
                    ib_price.show()
                    ib_cost.show()
                    ib_discrepancy.hide()
                }
                State.VERIFY -> {
                    ib_price.show()
                    ib_cost.show()
                    ib_discrepancy.show()
                }
            }
    }
    private class ActionsViewHolder(
        view: View,
        var ib_add: ImageButton = view.findViewById(R.id.ib_add),
        var ib_paste: ImageButton = view.findViewById(R.id.ib_paste),
        var ib_copy: ImageButton = view.findViewById(R.id.ib_copy),
        var ib_clear: ImageButton = view.findViewById(R.id.ib_clear),
        var ib_previous: ImageButton = view.findViewById(R.id.ib_previous),
        var ib_next: ImageButton = view.findViewById(R.id.ib_next),
    ) {
        fun setState(state: State) =
            when(state) {
                State.PLAN -> {
                    ib_add.show()
                    ib_paste.show()
                    ib_copy.show()
                    ib_clear.show()
                    ib_previous.isEnabled = false
                    ib_next.isEnabled = true
                }
                State.BUY -> {
                    ib_add.hide()
                    ib_paste.show()
                    ib_copy.show()
                    ib_clear.show()
                    ib_previous.isEnabled = true
                    ib_next.isEnabled = true
                }
                State.VERIFY -> {
                    ib_add.hide()
                    ib_paste.hide()
                    ib_copy.show()
                    ib_clear.hide()
                    ib_previous.isEnabled = true
                    ib_next.isEnabled = false
                }
            }
    }
    class SummaryViewHolder(
        var ll_summary: LinearLayout,
        var tv_summary_cost: TextView = ll_summary.findViewById(R.id.tv_summary_cost),
        var tv_summary_discrepancy: TextView = ll_summary.findViewById(R.id.tv_summary_discrepancy),
    ) {
        private val context: Context
            get() = ll_summary.context

        var cost: Float = 0f
            get() = field
            set(value) {
                field = value
                tv_summary_cost.text = NumberFormat.getInstance().format(cost)
            }

        var discrepancy: Float = 0f
            get() = field
            set(value) {
                field = value
                tv_summary_discrepancy.text = NumberFormat.getInstance().format(cost)
                if(value > -0.1f)
                    tv_summary_discrepancy.setTextColor(ContextCompat.getColor(context, R.color.correct))
                else
                    tv_summary_discrepancy.setTextColor(ContextCompat.getColor(context, R.color.wrong))
            }

        fun setState(state: State) =
            when(state) {
                State.PLAN -> {
                    ll_summary.hide()
                }
                State.BUY -> {
                    ll_summary.show()
                    tv_summary_discrepancy.hide()
                }
                State.VERIFY -> {
                    ll_summary.show()
                    tv_summary_discrepancy.show()
                }
            }
    }

    private lateinit var rv_items: RecyclerView
    private lateinit var w_color_picker: ColorPicker

    private lateinit var header: HeaderViewHolder
    private lateinit var actions: ActionsViewHolder
    lateinit var summary: SummaryViewHolder

    private lateinit var itemsAdapter: MarketItemAdapter
    private lateinit var gesturesDetector: GestureDetector

    private var data: ArrayList<MarketItem> = arrayListOf()

    private val width: Int
        get() = window.decorView.width

    private val height: Int
        get() = window.decorView.height

    enum class State {
        PLAN,
        BUY,
        VERIFY
    }

    private fun onStateChange(newState: State) {
        rv_items.visibleViewHolders().forEach {
            if(it is MarketItemViewHolder)
                it.setState(newState)
        }
        summary.setState(newState)
        header.setState(newState)
        actions.setState(newState)
    }

    var state: State = State.PLAN
        get() = field
        set(value) {
            if(value != field)
                onStateChange(value)
            field = value
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MarketData.loadPalette(this)

        itemsAdapter = MarketItemAdapter(MarketData.ITEMS_FILE_NAME, data, this)

//        gesturesDetector = GestureDetector(this, object :
//            GestureDetector.SimpleOnGestureListener() {
//            override fun onFling(
//                e1: MotionEvent?,
//                e2: MotionEvent?,
//                velocityX: Float,
//                velocityY: Float
//            ): Boolean {
//                if(e1 != null && e1.x > 0.6f * width) {
//                    onSwipeCompleted(velocityX)
//                    return true
//                }
//                return false
//            }
//        })

        rv_items = findViewById(R.id.rv_items)
        rv_items.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
//            setOnTouchListener { _, event ->
//                return@setOnTouchListener gesturesDetector.onTouchEvent(event)
//            }
        }

        header = HeaderViewHolder(findViewById(R.id.ll_header))
        summary = SummaryViewHolder(findViewById(R.id.ll_summary))
        actions = ActionsViewHolder(findViewById(R.id.ll_actions))

        w_color_picker = findViewById(R.id.w_color_picker)
        w_color_picker.setOnColorPickerListener(itemsAdapter::colorChanged)

        onStateChange(State.PLAN)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        actions.apply {
            ib_add.setOnClickListener {
                itemsAdapter.add(MarketItem())
//            val pos = viewAdapter.itemCount - 1
//            recyclerView.scrollToPosition(pos)
            }
            ib_paste.setOnClickListener {
                if(clipboard.hasPrimaryClip() and
                    (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true)
                ) {
                    val data = clipboard.primaryClip?.getItemAt(0)?.text ?: ""
                    if(data.isEmpty())
                        return@setOnClickListener

                    itemsAdapter.paste(data.toString())
                }
            }
            ib_copy.setOnClickListener {
                clipboard.setPrimaryClip(ClipData.newPlainText("simple text", itemsAdapter.toPlainText()))
                Toast.makeText(this@MainActivity, getString(R.string.popup_copied), Toast.LENGTH_SHORT).show()
            }
            ib_clear.setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.remove_all_title))
                    .setMessage(getString(R.string.remove_all_message))
                    .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                        itemsAdapter.removeAll()
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .show()
            }
            ib_previous.setOnClickListener {
                onPreviousState()
            }
            ib_next.setOnClickListener {
                onNextState()
            }
        }
    }

    private fun onPreviousState() {
        state = when (state) {
            State.PLAN -> State.PLAN
            State.BUY -> State.PLAN
            State.VERIFY -> State.BUY
        }
    }

    private fun onNextState() {
        state = when (state) {
            State.PLAN -> State.BUY
            State.BUY -> State.VERIFY
            State.VERIFY -> State.VERIFY
        }
    }

//    private fun onSwipeCompleted(delta: Float) {
//        when(state) {
//            State.PLAN ->
//                if(delta < 0)
//                    state = State.BUY
//            State.BUY ->
//                state = if(delta > 0)
//                    State.PLAN
//                else State.VERIFY
//            State.VERIFY ->
//                if(delta > 0)
//                    state = State.BUY
//        }
//    }

    override fun onPause() {
        super.onPause()
        MarketData.savePreferences(this, data)
        itemsAdapter.save()
    }

    override fun onResume() {
        super.onResume()
        MarketData.loadPreferences(this)
        MarketTouchHelper(itemsAdapter).attachToRecyclerView(rv_items)
        itemsAdapter.load()
    }
}