package did.pinbraerts.market

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

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
    private class SummaryViewHolder(
        var ll_summary: LinearLayout,
        var tv_summary_cost: TextView = ll_summary.findViewById(R.id.tv_summary_cost),
        var tv_summary_discrepancy: TextView = ll_summary.findViewById(R.id.tv_summary_discrepancy),
    ) {
        fun setState(state: State) =
            when(state) {
                State.PLAN -> {
                    ll_summary.show()
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
    private lateinit var summary: SummaryViewHolder

    private lateinit var itemsAdapter: MarketItemAdapter
    private lateinit var gesturesDetector: GestureDetector

    private var data: ArrayList<String> = arrayListOf()

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

    private var state: State = State.PLAN
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

        itemsAdapter = MarketItemAdapter(data, WeakReference(state))

        rv_items = findViewById(R.id.rv_items)
        rv_items.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }

        header = HeaderViewHolder(findViewById(R.id.ll_header))
        summary = SummaryViewHolder(findViewById(R.id.ll_summary))
        actions = ActionsViewHolder(findViewById(R.id.ll_actions))

        w_color_picker = findViewById(R.id.w_color_picker)

        data.add("one")
        data.add("two")
        data.add("three")

        onStateChange(State.PLAN)

        rv_items.setOnTouchListener { _, event ->
            gesturesDetector.onTouchEvent(event)
        }
        gesturesDetector = GestureDetector(this, object :
            GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if(e1 != null && e1.x > 0.6f * width)
                    onSwipeCompleted(velocityX)
                return true
            }
        })
    }

    private fun onSwipeCompleted(delta: Float) {
        when(state) {
            State.PLAN ->
                if(delta < 0)
                    state = State.BUY
            State.BUY ->
                state = if(delta > 0)
                    State.PLAN
                else State.VERIFY
            State.VERIFY ->
                if(delta > 0)
                    state = State.BUY
        }
    }
}