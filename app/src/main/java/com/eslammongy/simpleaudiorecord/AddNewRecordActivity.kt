package com.eslammongy.simpleaudiorecord

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_add_new_record.*
import kotlinx.android.synthetic.main.set_record_name.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddNewRecordActivity : AppCompatActivity() {

    private val PERMISSION_CODE: Int = 102
    private var isRecording: Boolean = false
    private var recordPermission: String = android.Manifest.permission.RECORD_AUDIO
    private var mediaRecorder: MediaRecorder? = null
    private var recordFilePath: String? = null
    private var audioName: String? = null
    private lateinit var animationOne: Animation
    private lateinit var animationTwo: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_record)

        animationOne = AnimationUtils.loadAnimation(this, R.anim.overbox_dialog)
        animationTwo = AnimationUtils.loadAnimation(this, R.anim.animation_one)

    }

    fun backToHomeFromVoice(view: View) {

        val intent = Intent(this,ShowRecordsListActivity::class.java)
        intent.putExtra("BackToHome", 3)
        startActivity(intent)
        finish()

    }

    fun startRecording(view: View) {

        if (isRecording) {
            // stop recording case
            stopRecording()
            btn_Start_Record.setImageResource(R.drawable.ic_round_stop_24)
            isRecording = false

        } else {
            // play recording case
            if (checkAudioPermission()) {
                startAudioRecording()
                btn_Start_Record.setImageResource(R.drawable.ic_round_mic_24)
                isRecording = true
            }
        }
    }

    fun setChangeAudioName(view: View) {

        showAudioNameDialog()
    }

    private fun showAudioNameDialog() {
        setRecordName.visibility = View.VISIBLE
        setRecordName.alpha = 1.0F
        dialog_inputName.hint = "audio name"
        UIUtil.showKeyboard(this, dialog_inputName)
        setRecordName.startAnimation(animationOne)
        audioParentView.alpha = 0.3F
    }

    fun exitEditDialog(view: View) {
        setRecordName.visibility = View.GONE
        setRecordName.startAnimation(animationTwo)
        audioParentView.alpha = 1.0F
        UIUtil.hideKeyboard(this)
    }

    fun saveEditDialog(view: View) {

        setRecordName.visibility = View.GONE
        setRecordName.startAnimation(animationTwo)
        audioParentView.alpha = 1.0F
        audio_Name.text = dialog_inputName.text
        UIUtil.hideKeyboard(this)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun startAudioRecording() {

        record_timer.base = SystemClock.elapsedRealtime()
        record_timer.start()
        val audioPath = this.getExternalFilesDir("/")?.absolutePath
        val simpleDateFormat = SimpleDateFormat("dd_mm", Locale.CANADA)
        val date = Date()
        val checkName = audio_Name.text.toString()

        if (checkName == getString(R.string.press_the_mic_button_n_to_start_recording)) {
            audio_Name.text = "Audio..${simpleDateFormat.format(date)}"
            audioName = audio_Name.text.toString()

        } else {

            audioName = audio_Name.text.toString()
        }

        recordFilePath = audioName
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setOutputFile("$audioPath/$recordFilePath")
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {

            mediaRecorder!!.prepare()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaRecorder!!.start()
    }

    @SuppressLint("SetTextI18n")
    private fun stopRecording() {

        record_timer.stop()

        audio_Name.text = "Recording Stop and Saved.. $recordFilePath"
        mediaRecorder!!.stop()
        mediaRecorder!!.release()
        mediaRecorder = null

    }

    private fun checkAudioPermission(): Boolean {
        // check user audio permission
        return if (ActivityCompat.checkSelfPermission(
                this,
                recordPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(recordPermission), PERMISSION_CODE)
            false
        }
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            stopRecording()
        }
    }

}