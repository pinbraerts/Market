package did.pinbraerts.market

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import kotlin.math.abs

class SwipeDetector(
    context: Context,
    config: ViewConfiguration = ViewConfiguration.get(context),
    private val touchSlop: Int = config.scaledTouchSlop,
    private val maxVelocity: Int = config.scaledMaximumFlingVelocity,
) {
    private var isSwiping = false
    private var initialX = 0f
    private var initialY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var velocityTracker: VelocityTracker? = null
    private var activePointerId: Int = MotionEvent.INVALID_POINTER_ID

    interface SwipeListener {
        fun onSwipe(deltaX: Float, deltaY: Float)
    }

    private var swipeListener: SwipeListener? = null

    fun setSwipeListener(listener: SwipeListener) {
        swipeListener = listener
    }

    private fun reset() {
        isSwiping = false
        if(velocityTracker != null) {
            velocityTracker?.recycle()
            velocityTracker = null
        }
        activePointerId = MotionEvent.INVALID_POINTER_ID
        initialX = 0f
        initialY = 0f
        lastX = 0f
        lastY = 0f
    }

    private fun startMotion(event: MotionEvent) {
        initialX = event.x
        initialY = event.y
        lastX = initialX
        lastY = initialY
        activePointerId = event.getPointerId(0)
    }

    fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked

        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
            return isSwiping

        if(action != MotionEvent.ACTION_DOWN || isSwiping)
            return true

        when(action) {
            MotionEvent.ACTION_MOVE -> if(activePointerId != MotionEvent.INVALID_POINTER_ID) {
                val index = event.findPointerIndex(activePointerId)
                val x = event.getX(index)
                val y = event.getY(index)
                val dx = abs(x - lastX)
                val dy = abs(y - lastY)

                if(dx > touchSlop && dx * 0.5 > dy) {
                    lastX = if(x > initialX) initialX + touchSlop
                        else initialX - touchSlop
                    lastY = y
                    isSwiping = true
                }
            }
            MotionEvent.ACTION_DOWN -> startMotion(event)
        }

        if(velocityTracker == null)
            velocityTracker = VelocityTracker.obtain()
        velocityTracker?.addMovement(event)

        return isSwiping
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if(event.action == MotionEvent.ACTION_DOWN && event.edgeFlags != 0)
            return false

        if(velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(event)

        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> startMotion(event)
            MotionEvent.ACTION_MOVE -> {
                if(!isSwiping) {
                    val index = event.findPointerIndex(activePointerId)
                    val x = event.getX(index)
                    val y = event.getY(index)
                    val dx = abs(x - lastX)
                    val dy = abs(y - lastY)

                    if(dx > touchSlop && dx > dy) {
                        isSwiping = true
                        lastX = if(x > initialX) initialX + touchSlop
                            else initialX - touchSlop
                        lastY = y
                    }
                }
//                if(isSwiping) {
//                    TOOD("Perform swipe")
//                }
            }
            MotionEvent.ACTION_UP -> if(isSwiping) {
                val index = event.findPointerIndex(activePointerId)
                val x = event.getX(index)
                val y = event.getY(index)

                velocityTracker?.apply {
                    computeCurrentVelocity(1000, maxVelocity.toFloat())
                    swipeListener?.onSwipe(x - initialX, y - initialY)
                }
                reset()
            }
            MotionEvent.ACTION_CANCEL -> if(isSwiping)
                reset()
        }

        return true
    }
}