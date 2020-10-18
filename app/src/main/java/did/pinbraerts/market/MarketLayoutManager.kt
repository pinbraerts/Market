package did.pinbraerts.market

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View

class MarketLayoutManager: LinearLayoutManager {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int):
            super(context, attributeSet, defStyleAttr, defStyleRes)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean):
            super(context, orientation, reverseLayout)

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        val pos = getPosition(focused)

        when(direction) {
            View.FOCUS_BACKWARD -> {
                if(pos == 0)
                    return focused
            }
            View.FOCUS_FORWARD -> {
                if(pos == childCount)
                    return focused
            }
        }

        return super.onInterceptFocusSearch(focused, direction)
    }
}