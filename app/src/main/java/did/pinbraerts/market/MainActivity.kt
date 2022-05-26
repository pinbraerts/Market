package did.pinbraerts.market

import android.content.*
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), SwipeDetector.SwipeListener {
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
    ) {
        fun setState(state: State) =
            when(state) {
                State.PLAN -> {
                    ib_add.show()
                    ib_paste.show()
                    ib_copy.show()
                    ib_clear.show()
                }
                State.BUY -> {
                    ib_add.hide()
                    ib_paste.show()
                    ib_copy.show()
                    ib_clear.show()
                }
                State.VERIFY -> {
                    ib_add.hide()
                    ib_paste.hide()
                    ib_copy.show()
                    ib_clear.hide()
                }
            }
    }
    class SummaryViewHolder(
        var ll_summary: LinearLayout,
        var tv_summary_cost: TextView = ll_summary.findViewById(R.id.tv_summary_cost),
        var tv_summary_discrepancy: TextView = ll_summary.findViewById(R.id.tv_summary_discrepancy),
    ) {
        private val format = DecimalFormat()

        var cost: Float = 0f
            set(value) {
                field = value
                tv_summary_cost.text = format.format(value)
            }

        var discrepancy: Float = 0f
            set(value) {
                field = value
                tv_summary_discrepancy.apply {
                    text = format.format(value)
                    setTextColor(ContextCompat.getColor(context,
                        if (value > -0.1f) R.color.correct
                        else R.color.wrong
                    ))
                }
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

    lateinit var rv_items: RecyclerView

    private lateinit var w_color_picker: ColorPicker

    private lateinit var header: HeaderViewHolder
    internal lateinit var summary: SummaryViewHolder
    private lateinit var actions: ActionsViewHolder

    private lateinit var itemsAdapter: MarketItemAdapter
    private lateinit var swipeDetector: SwipeDetector

    internal val filename: String = MarketData.ITEMS_FILE_NAME

    private val width: Float
        get() = rv_items.rootView.width.toFloat()

    private val height: Float
        get() = rv_items.rootView.height.toFloat()

    enum class State {
        PLAN,
        BUY,
        VERIFY
    }

    private fun onStateChange(newState: State) {
        itemsAdapter.setState(newState)
        summary.setState(newState)
        header.setState(newState)
        actions.setState(newState)
    }

    var state: State = State.PLAN
        set(value) {
            if(value != field)
                onStateChange(value)
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsAdapter = MarketItemAdapter(this)
        itemsAdapter.load()

        rv_items = findViewById(R.id.rv_items)
        rv_items.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
            recycledViewPool.setMaxRecycledViews(0, 10)
        }.post {
            swipeDetector = SwipeDetector(
                this, RectF(
                    width * 0.6f,
                    0f,
                    width,
                    height
                )
            )
            swipeDetector.setSwipeListener(this)
            itemsAdapter.post()
        }

        header = HeaderViewHolder(findViewById(R.id.ll_header))
        summary = SummaryViewHolder(findViewById(R.id.ll_summary))
        actions = ActionsViewHolder(findViewById(R.id.ll_actions))

        w_color_picker = findViewById(R.id.w_color_picker)
        w_color_picker.setOnColorPickerListener(itemsAdapter)

        onStateChange(State.PLAN)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        actions.apply {
            ib_add.setOnClickListener {
                itemsAdapter.add(MarketItem())
            }
            ib_paste.setOnClickListener {
                if(clipboard.hasPrimaryClip() &&
                    clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
                ) {
                    val data = clipboard.primaryClip?.getItemAt(0)?.text ?: ""
                    if(data.isEmpty())
                        return@setOnClickListener

                    itemsAdapter.paste(data.toString())
                }
            }
            ib_copy.setOnClickListener {
                clipboard.setPrimaryClip(ClipData.newPlainText(
                    "simple text", itemsAdapter.copy()
                ))
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.popup_copied),
                    Toast.LENGTH_SHORT
                ).show()
            }
            ib_clear.setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.remove_all_title))
                    .setMessage(getString(R.string.remove_all_message))
                    .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                        itemsAdapter.clear()
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .show()
            }
        }
    }

    private fun previousState() {
        state = when (state) {
            State.PLAN -> State.PLAN
            State.BUY -> State.PLAN
            State.VERIFY -> State.BUY
        }
    }

    private fun nextState() {
        state = when (state) {
            State.PLAN -> State.BUY
            State.BUY -> State.VERIFY
            State.VERIFY -> State.VERIFY
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev == null) return false
        if(swipeDetector.onInterceptTouchEvent(ev)) return onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) return false
        if(swipeDetector.onTouchEvent(event)) return true
        return super.onTouchEvent(event)
    }

    override fun onPause() {
        super.onPause()
        itemsAdapter.save()
    }

    override fun onResume() {
        super.onResume()
        itemsAdapter.load()
        MarketTouchHelper(itemsAdapter).attachToRecyclerView(rv_items)
    }

    override fun onSwipe(deltaX: Float, deltaY: Float) {
        if(deltaX < 0)
            nextState()
        else
            previousState()
    }
}
