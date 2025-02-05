package com.denicks21.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var startTV: Button
    lateinit var stopTV: Button
    lateinit var playTV: Button
    lateinit var stopplayTV: Button
    lateinit var statusTV: TextView
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTV = findViewById(R.id.idTVstatus)
        startTV = findViewById(R.id.btnRecord)
        stopTV = findViewById(R.id.btnStop)
        playTV = findViewById(R.id.btnPlay)
        stopplayTV = findViewById(R.id.btnStopPlay)

        if (!hasPermissions()) {
            requestPermissions()
        }

        startTV.setOnClickListener {
            startRecording()
        }

        stopTV.setOnClickListener {
            stopRecording()
        }

        playTV.setOnClickListener {
            playRecording()
        }

        stopplayTV.setOnClickListener {
            stopPlaying()
        }
    }

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_AUDIO_PERMISSION_CODE)
    }

    private fun startRecording() {
        if (hasPermissions()) {
            audioFilePath = "${externalCacheDir?.absolutePath}/audiorecord.3gp"
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                try {
                    prepare()
                    start()
                    statusTV.text = "Recording in progress"
                } catch (e: IOException) {
                    Log.e("TAG", "prepare() failed")
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        statusTV.text = "Recording stopped"
    }

    private fun playRecording() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
                statusTV.text = "Playing recording"
            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        statusTV.text = "Playback stopped"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val REQUEST_AUDIO_PERMISSION_CODE = 1
    }
}
