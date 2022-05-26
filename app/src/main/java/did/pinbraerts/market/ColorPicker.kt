package did.pinbraerts.market

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ColorPicker: View {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    interface OnColorPickedListener {
        fun onColorPicked(color: Int)
    }

    private var listener: OnColorPickedListener? = null
    private var itemSize: Int = 0
    private var spacing: Int = 0
    private val paint: Paint = Paint()
    private val palette
        get() = ((parent as View).findViewById<RecyclerView>(R.id.rv_items).adapter as MarketItemAdapter).palette

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(measuredHeight > measuredWidth)
            recompute(measuredWidth, measuredHeight)
        else
            recompute(measuredHeight, measuredWidth)
    }

    private fun recompute(minDim: Int, maxDim: Int) {
        if(palette.isEmpty()) {
            itemSize = 0
            spacing = 0
            return
        }

        itemSize = minDim
        spacing = maxDim / palette.size - itemSize
        if(spacing < 0) {
            itemSize = maxDim / palette.size
            spacing = 0
        }
    }

    override fun onDraw(canvas: Canvas) {
        if(measuredWidth > measuredHeight) {
            var l = spacing / 2f
            palette.forEach {
                paint.color = it
                canvas.drawRect(l, 0f, l + itemSize, itemSize.toFloat(), paint)
                l += spacing + itemSize
            }
        }
        else {
            var t = spacing / 2f
            palette.forEach {
                paint.color = it
                canvas.drawRect(0f, t, itemSize.toFloat(), t + itemSize, paint)
                t += spacing + itemSize
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_UP -> {
                val i = (event.x / (itemSize + spacing)).toInt()
                if(i < 0 || i >= palette.size)
                    return true
                listener?.onColorPicked(i)
            }
        }

        return true
    }

    fun setOnColorPickerListener(l: OnColorPickedListener) {
        listener = l
    }
}