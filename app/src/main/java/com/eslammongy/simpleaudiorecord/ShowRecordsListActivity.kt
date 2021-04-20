package com.eslammongy.simpleaudiorecord

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_show_records_list.*
import kotlinx.android.synthetic.main.play_record_layout.*
import java.io.File
import java.io.IOException

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ShowRecordsListActivity : AppCompatActivity(), VoiceRecordAdapter.OnItemClickListener{

    private lateinit var allFiles: List<File>
    var voiceRecordAdapter: VoiceRecordAdapter? = null
    lateinit var animationOne: Animation
    lateinit var animationTwo: Animation
    var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
    var controlPlayer:String? = null
    var deletedItem:String? = null
    private var playedFile: File? = null
    private var seekBarHandler: Handler? = null
    private var updateSeekBar:Runnable? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_records_list)


        animationOne = AnimationUtils.loadAnimation(this, R.anim.overbox_dialog)
        animationTwo = AnimationUtils.loadAnimation(this, R.anim.animation_one)

        val filePath: String = this.getExternalFilesDir("/")!!.absolutePath
        val fileDirectory = File(filePath)
        allFiles = fileDirectory.listFiles().toList()
        allFiles.sortedDescending()
        // set recyclerView and Adapter functions
        voiceRecordAdapter = VoiceRecordAdapter(this, allFiles, this)
        audio_RecyclerView.setHasFixedSize(true)
        audio_RecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        audio_RecyclerView.adapter = voiceRecordAdapter

        // fun to close audio player dialog
        closePlayer_Layout.setOnClickListener {
            recordPlayer_Layout.visibility = View.GONE
            recordPlayer_Layout.startAnimation(animationTwo)
            audio_RecyclerView.alpha = 1.0F
            audio_RecyclerView.visibility = View.VISIBLE
            stopAudio()

        }

        add_fab.setOnClickListener {
            val intent = Intent(this , AddNewRecordActivity::class.java)
            startActivity(intent)
            // overridePendingTransition(R)
            finish()
        }
        // fun to playing audio record
        btn_Play_Record.setOnClickListener {

            if (isPlaying){
                pauseAudio()
            }else{
                resumeAudio()
            }
        }

        // fun to control seekBar
        player_SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pauseAudio()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                val progress = seekBar!!.progress
                mediaPlayer!!.seekTo(progress)
                resumeAudio()
            }
        })


    }

    override fun onClick(file: File, position: Int) {

        recordPlayer_Layout.visibility = View.VISIBLE
        recordPlayer_Layout.alpha = 1.0F
        recordPlayer_Layout.startAnimation(animationOne)
        audio_RecyclerView.alpha = 0.2F
        audio_RecyclerView.visibility = View.GONE
        playedFile = file
        record_FileName.text = playedFile!!.name
        checkMediaPlayer(playedFile!!)

    }

    private fun pauseAudio(){

        mediaPlayer!!.pause()
        isPlaying = false
        btn_Play_Record.setImageResource(R.drawable.ic_round_play_circle_filled_24)
        player_header_State.text = ("Stopped..")
        seekBarHandler!!.removeCallbacks(updateSeekBar!!)
    }

    private fun resumeAudio(){
        mediaPlayer!!.start()
        isPlaying = true
        btn_Play_Record.setImageResource(R.drawable.ic_round_stop_24)
        player_header_State.text = ("Playing..")
        updateRunnable()
        seekBarHandler!!.postDelayed(updateSeekBar!! , 0)
    }

    private fun checkMediaPlayer(file: File) {

        playedFile = file
        if (isPlaying) {
            stopAudio()
            playAudio(playedFile!!)

        } else {
            playAudio(file)
        }
    }

    private fun playAudio(file: File) {

        mediaPlayer = MediaPlayer()
        try {

            mediaPlayer!!.setDataSource(file.absolutePath)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            btn_Play_Record.setImageResource(R.drawable.ic_round_stop_24)
            player_header_State.text = ("Playing..")
            controlPlayer = "AudioIsPlayed"

        } catch (e: IOException) {
            e.printStackTrace()
        }

        isPlaying = true
        mediaPlayer!!.setOnCompletionListener {
            stopAudio()
            player_header_State.text = ("Finished")
        }

        player_SeekBar.max = mediaPlayer!!.duration
        seekBarHandler = Handler()
        updateRunnable()
        seekBarHandler!!.postDelayed(updateSeekBar!!, 0)
    }

    private fun updateRunnable() {
        updateSeekBar = object : Runnable {
            override fun run() {
                player_SeekBar!!.progress = mediaPlayer!!.currentPosition
                seekBarHandler!!.postDelayed(this, 500)
            }
        }
    }

    private fun stopAudio() {
        isPlaying = false
        btn_Play_Record.setImageResource(R.drawable.ic_round_play_circle_filled_24)
        player_header_State.text = ("Stopped..")
        controlPlayer = "AudioIsStopped"
        mediaPlayer!!.stop()
    }



}