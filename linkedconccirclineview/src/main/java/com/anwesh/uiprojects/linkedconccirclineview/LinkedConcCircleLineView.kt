package com.anwesh.uiprojects.linkedconccirclineview

/**
 * Created by anweshmishra on 28/06/18.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.MotionEvent

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
}