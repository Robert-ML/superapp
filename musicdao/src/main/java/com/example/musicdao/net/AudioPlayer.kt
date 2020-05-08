package com.example.musicdao.net

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.music_app_main.*
import java.io.File

lateinit var instance: AudioPlayer

class AudioPlayer(context: Context, musicService: MusicService) : LinearLayout(context), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener {
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var timerStart: Long = 0
    private var interestedFraction: Float = 0F
    private var piecesInfoLog = ""
//    public var seekProgress = (context as Activity).seek
    private val bufferInfo = musicService.bufferInfo
    private val progressBar = musicService.progressBar
    private val seekBar = musicService.seekBar
    private val playButton = musicService.playButtonAudioPlayer

    init {
        progressBar.max = 100
        progressBar.progress = 0
//        bufferInfo.layoutParams = linearLayoutParams
        bufferInfo.text = "No track currently playing"
//        this.addView(bufferInfo)
        seekBar.setOnSeekBarChangeListener(this)
    }

    companion object {
        fun getInstance(context: Context, musicService: MusicService) : AudioPlayer {
            if (!::instance.isInitialized) {
                createInstance(context, musicService)
            }
            return instance
        }

        @Synchronized
        private fun createInstance(context: Context, musicService: MusicService) {
            instance = AudioPlayer(context, musicService)
        }
    }

    public fun setAudioResource(file: File) {
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(context, file.toUri())
            prepareAsync()
            setOnPreparedListener(this@AudioPlayer)
            setOnErrorListener(this@AudioPlayer)
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp?.reset()
        var message = ""
        when (what) {
            MediaPlayer.MEDIA_ERROR_IO -> {
                message = "Media error: IO"
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                message = "Media error: malformed"
            }
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                message = "Media error: invalid for progressive playback"
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                message = "Media error: server died"
            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                message = "Media error: timed out"
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                message = "Media error: unknown"
            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                message = "Media error: unsupported"
            }
        }
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
        return true
    }

    override fun onPrepared(mp: MediaPlayer) {
        this.playButton.isClickable = true
        this.playButton.isActivated = true
        this.playButton.isEnabled = true
        this.playButton.setOnClickListener {
            if (mp.isPlaying) {
                this.playButton.setImageResource(android.R.drawable.ic_media_play)
                mp.pause()
            } else {
                this.playButton.setImageResource(android.R.drawable.ic_media_pause)
                mp.start()
            }
        }
        //Directly play the track when it is prepared
        this.playButton.callOnClick()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        interestedFraction = (progress.toFloat() / 100.toFloat())
        val duration = mediaPlayer?.duration
        if (duration != null) {
            val seekMs: Int = (duration * interestedFraction).toInt()
            mediaPlayer?.seekTo(seekMs)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    public fun setInfo(text: String) {
        bufferInfo.text = text
    }

}
