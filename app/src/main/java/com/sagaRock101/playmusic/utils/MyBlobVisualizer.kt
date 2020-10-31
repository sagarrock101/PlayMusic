package com.sagaRock101.playmusic.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.gauravk.audiovisualizer.model.AnimSpeed
import com.gauravk.audiovisualizer.model.PaintStyle
import com.gauravk.audiovisualizer.utils.BezierSpline

class MyBlobVisualizer : MyBaseVisualizer {
    private var mBlobPath: Path? = null
    private var mRadius = 0
    private var nPoints = 0
    private lateinit var mBezierPoints: Array<PointF?>
    private var mBezierSpline: BezierSpline? = null
    private var mAngleOffset = 0f
    private var mChangeFactor = 0f

    constructor(context: Context?) : super(context!!) {}
    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?
    ) : super(context!!, attrs) {
    }

    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
    }

    override fun init() {
        mRadius = -1
        nPoints = (mDensity * BLOB_MAX_POINTS).toInt()
        if (nPoints < BLOB_MIN_POINTS) nPoints =
            BLOB_MIN_POINTS
        mAngleOffset = 360.0f / nPoints
        updateChangeFactor(mAnimSpeed, false)
        mBlobPath = Path()

        //initialize mBezierPoints, 2 extra for the smoothing first and last point
        mBezierPoints = arrayOfNulls(nPoints + 2)
        for (i in mBezierPoints.indices) {
            mBezierPoints[i] = PointF()
        }
        mBezierSpline = BezierSpline(mBezierPoints.size)
    }

    override fun setAnimationSpeed(animSpeed: AnimSpeed) {
        super.setAnimationSpeed(animSpeed)
        updateChangeFactor(animSpeed, true)
    }

    private fun updateChangeFactor(animSpeed: AnimSpeed, useHeight: Boolean) {
        var height = 1
        if (useHeight) height = if (getHeight() > 0) getHeight() else 1000
        mChangeFactor =
            if (animSpeed == AnimSpeed.SLOW) height * 0.003f else if (animSpeed == AnimSpeed.MEDIUM) height * 0.006f else height * 0.01f
    }

    override fun onDraw(canvas: Canvas) {
        var angle = 0.0
        //first time initialization
        if (mRadius == -1) {
            mRadius = if (height < width) height else width
            mRadius = (mRadius * 0.65 / 2).toInt()
            mChangeFactor = height * mChangeFactor

            //initialize bezier points
            var i = 0
            while (i < nPoints) {
                val posX = (width / 2
                        + mRadius
                        * Math.cos(Math.toRadians(angle))).toFloat()
                val posY = (height / 2
                        + mRadius
                        * Math.sin(Math.toRadians(angle))).toFloat()
                mBezierPoints[i]!![posX] = posY
                i++
                angle += mAngleOffset.toDouble()
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes?.size == 0) {
                return
            }
            mBlobPath!!.rewind()

            //find the destination bezier point for a batch
            run {
                var i = 0
                while (i < nPoints) {
                    val x =
                        Math.ceil((i + 1) * (mRawAudioBytes?.size!! / nPoints).toDouble()).toInt()
                    var t = 0
                    if (x < 1024) t =
                        (-Math.abs(mRawAudioBytes?.get(x)!!.toInt()) + 128) as Byte * (canvas.height / 4) / 128
                    val posX = (width / 2
                            + (mRadius + t)
                            * Math.cos(Math.toRadians(angle))).toFloat()
                    val posY = (height / 2
                            + (mRadius + t)
                            * Math.sin(Math.toRadians(angle))).toFloat()

                    //calculate the new x based on change
                    if (posX - mBezierPoints[i]!!.x > 0) {
                        mBezierPoints[i]!!.x += mChangeFactor
                    } else {
                        mBezierPoints[i]!!.x -= mChangeFactor
                    }

                    //calculate the new y based on change
                    if (posY - mBezierPoints[i]!!.y > 0) {
                        mBezierPoints[i]!!.y += mChangeFactor
                    } else {
                        mBezierPoints[i]!!.y -= mChangeFactor
                    }
                    i++
                    angle += mAngleOffset.toDouble()
                }
            }
            //set the first and last point as first
            mBezierPoints[nPoints]!![mBezierPoints[0]!!.x] = mBezierPoints[0]!!.y
            mBezierPoints[nPoints + 1]!![mBezierPoints[0]!!.x] = mBezierPoints[0]!!.y

            //update the control points
            mBezierSpline!!.updateCurveControlPoints(mBezierPoints)
            val firstCP = mBezierSpline!!.firstControlPoints
            val secondCP = mBezierSpline!!.secondControlPoints

            //create the path
            mBlobPath!!.moveTo(mBezierPoints[0]!!.x, mBezierPoints[0]!!.y)
            for (i in firstCP.indices) {
                mBlobPath!!.cubicTo(
                    firstCP[i].x, firstCP[i].y,
                    secondCP[i].x, secondCP[i].y,
                    mBezierPoints[i + 1]!!.x, mBezierPoints[i + 1]!!.y
                )
            }
            //add an extra line to center cover the gap generated by last cubicTo
            if (mPaintStyle == PaintStyle.FILL) mBlobPath!!.lineTo(
                width / 2.toFloat(),
                height / 2.toFloat()
            )
            canvas.drawPath(mBlobPath!!, mPaint!!)
        }
        super.onDraw(canvas)
    }

    companion object {
        private const val BLOB_MAX_POINTS = 60
        private const val BLOB_MIN_POINTS = 3
    }
}