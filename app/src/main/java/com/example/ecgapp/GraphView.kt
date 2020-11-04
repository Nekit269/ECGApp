package com.example.ecgapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


class GraphView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    private val dataSet = mutableListOf<DataPoint>()
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    private val windowWidth = 500

    private var windowMin = 0
    private var windowMax = windowWidth

    private val dataPointPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    private val dataPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    private val dataPointLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val sectorSize = height.toFloat() / 10
        var sectorCur = 0f
        while(sectorCur < height.toFloat() + 2f)
        {
            canvas.drawLine(0f, sectorCur, width.toFloat(), sectorCur, axisLinePaint)
            sectorCur += sectorSize
        }

        sectorCur = 0f
        while(sectorCur < width.toFloat() * 1.11)
        {
            canvas.drawLine(sectorCur - windowMin%sectorSize / windowWidth * width, 0f,
                sectorCur - windowMin%sectorSize/ windowWidth * width, height.toFloat(), axisLinePaint)
            sectorCur += sectorSize
        }

        for (i in (windowMin..windowMax))
        {
//            Log.d("TESTVAL", i.toString())
            if (i >= 0 && i < dataSet.size - 1) {
//                Log.d("TESTVAL", dataSet[i].toString())
                val nextDataPoint = dataSet[i + 1]
                val startX = (i - windowMin).toRealX()
                val startY = height - dataSet[i].yVal.toRealY()
                val endX = (i+1 - windowMin).toRealX()
                val endY = height - nextDataPoint.yVal.toRealY()
                canvas.drawLine(startX, startY, endX, endY, dataPointLinePaint)
            }
        }

//        dataSet.forEachIndexed { index, currentDataPoint ->
//            if (index < dataSet.size - 1) {
//                val nextDataPoint = dataSet[index + 1]
//                val startX = currentDataPoint.xVal.toRealX()
//                val startY = height - currentDataPoint.yVal.toRealY()
//                val endX = nextDataPoint.xVal.toRealX()
//                val endY = height - nextDataPoint.yVal.toRealY()
//                canvas.drawLine(startX, startY, endX, endY, dataPointLinePaint)
//            }
//        }
    }

    fun setData(newDataSet: List<DataPoint>) {
        xMin = newDataSet.minBy { it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxBy { it.xVal }?.xVal ?: 0
//        xMin = visible_start
//        xMax = visible_start + visible_width
        yMin = newDataSet.minBy { it.yVal }?.yVal?.minus(0) ?: 0
        yMax = newDataSet.maxBy { it.yVal }?.yVal?.plus(500) ?: 0
        dataSet.clear()
        dataSet.addAll(newDataSet)
        invalidate()
    }

    private fun Int.toRealX() = toFloat() / windowWidth * width
    private fun Int.toRealY() = toFloat() / yMax * height

    public fun moveWindow(value: Int){
        if(windowMin + value < 0)
        {
            windowMin = 0;
            windowMax = windowWidth;
        }
        else if(windowMax + value > xMax)
        {
            windowMin = xMax - windowWidth;
            windowMax = xMax;
        }
        else
        {
            windowMin += value
            windowMax += value
        }
        invalidate()
    }
}



data class DataPoint(
    val xVal: Int,
    val yVal: Int
)