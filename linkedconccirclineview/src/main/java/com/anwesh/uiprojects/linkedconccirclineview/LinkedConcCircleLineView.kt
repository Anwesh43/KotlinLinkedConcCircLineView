package com.anwesh.uiprojects.linkedconccirclineview

/**
 * Created by anweshmishra on 28/06/18.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.MotionEvent

val CCL_NODES : Int = 5

class LinkedConcCircleLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class CCLState(var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class CCLAnimator(var view : View, var animated : Boolean = false) {

        fun update(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CCLNode (var i : Int) {

        private var next : CCLNode? = null

        private var prev : CCLNode? = null

        private val state : CCLState = CCLState()

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < CCL_NODES - 1) {
                next = CCLNode(i +1 )
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = w / CCL_NODES
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = Math.min(w, h) / 60
            paint.strokeCap = Paint.Cap.ROUND
            val r : Float = gap/5
            val r1 : Float = gap/7
            canvas.save()
            canvas.translate(i * gap + gap * state.scales[0], h/2)
            canvas.drawCircle(0f, 0f, r, paint)
            canvas.drawCircle(0f, 0f, r1, paint)
            val index : Int = i % 2
            val scale : Float = index + (1 - 2 * index) * state.scales[1]
            for (i in 0..(360 * scale).toInt()) {
                val x : Float = r * Math.cos(i * Math.PI/180).toFloat()
                val y : Float = r * Math.sin(i * Math.PI/180).toFloat()
                val x1 : Float = r1 * Math.cos(i * Math.PI/180).toFloat()
                val y1 : Float = r1 * Math.sin(i * Math.PI/180).toFloat()
                canvas.drawLine(x, y, x1, y1, paint)
            }
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CCLNode {
            var curr : CCLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedConcCircleLine(var i : Int) {

        private var curr : CCLNode = CCLNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }
}