package com.example.ecgapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment

import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE: Int = 101

    private var first_vals: MutableList<DataPoint> = mutableListOf()
    private var second_vals: MutableList<DataPoint> = mutableListOf()

    private val moveNextPrevVal = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button2.setOnClickListener {
            if (checkPersmission())
            {
                openFile()
            }
            else
            {
                requestPermission()
            }
        }
        button3.setOnClickListener{
            onPrevClick()
        }
        button5.setOnClickListener{
            onNextClick()
        }
    }

    private fun swipeLeft()
    {
        graph_view_1.moveWindow(10)
        graph_view_2.moveWindow(10)

        redrawGraphs()
    }

    private fun onNextClick()
    {
        graph_view_1.moveWindow(moveNextPrevVal)
        graph_view_2.moveWindow(moveNextPrevVal)

        redrawGraphs()
    }

    private fun onPrevClick()
    {
        graph_view_1.moveWindow(-moveNextPrevVal)
        graph_view_2.moveWindow(-moveNextPrevVal)

        redrawGraphs()
    }

    private fun redrawGraphs(){
        graph_view_1.setData(first_vals)
        graph_view_2.setData(second_vals)
    }

    private fun openFile() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    @ExperimentalStdlibApi
    @ExperimentalUnsignedTypes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data!!.data //The uri with the location of the file
            lateinit var fileName: String

            val split = selectedFile!!.path!!.split(":".toRegex()).toTypedArray()
            val type = split[0]
            fileName = Environment.getExternalStorageDirectory().toString() + "/" + split[1]

            val stream = File(fileName).inputStream()

            for(i in (0..650000))
            {
                val bytes = ByteArray(3)
                stream.read(bytes)
                val nums = bytes.asUByteArray()

                val num_1 = nums[0].toInt() + 256 * (nums[1] and (15).toUByte()).toInt()
                val num_2 = nums[2].toInt() + 256 * (nums[1].rotateRight(4) and (15).toUByte()).toInt()


                first_vals.add(DataPoint(i, num_1))
                second_vals.add(DataPoint(i, num_2))
            }
            stream.close()

            redrawGraphs()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openFile()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ), PERMISSION_REQUEST_CODE)
    }
}
