package com.example.myoneproject

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object TimerSoundPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context) {
        Log.e("AAA_TIMER", "START() CALLED")

        if (mediaPlayer != null) {
            Log.e("AAA_TIMER", "MediaPlayer already exists. isPlaying = ${mediaPlayer?.isPlaying}")
            return
        }

        Log.e("AAA_TIMER", "Creating new MediaPlayer")

        mediaPlayer = MediaPlayer.create(
            context.applicationContext,
            R.raw.alarm_sound
        )?.apply {

            Log.e("AAA_TIMER", "MediaPlayer created successfully")

            isLooping = true

            setOnPreparedListener {
                Log.e("AAA_TIMER", "onPrepared triggered")
            }

            setOnCompletionListener {
                Log.e("AAA_TIMER", "onCompletion triggered")
            }

            setOnErrorListener { mp, what, extra ->
                Log.e("AAA_TIMER", "MediaPlayer ERROR: what=$what extra=$extra")
                try {
                    mp.release()
                } catch (e: Exception) {
                    Log.e("AAA_TIMER", "Error releasing after failure", e)
                }
                mediaPlayer = null
                true
            }

            Log.e("AAA_TIMER", "Calling start() on MediaPlayer")
            start()
        }

        Log.e("AAA_TIMER", "START() FINISHED")
    }

    fun stop() {
        Log.e("AAA_TIMER", "STOP() CALLED")

        mediaPlayer?.let {
            Log.e("AAA_TIMER", "MediaPlayer exists. isPlaying = ${it.isPlaying}")

            try {
                if (it.isPlaying) {
                    Log.e("AAA_TIMER", "Calling stop()")
                    it.stop()
                } else {
                    Log.e("AAA_TIMER", "MediaPlayer was NOT playing")
                }

                Log.e("AAA_TIMER", "Releasing MediaPlayer")
                it.release()

            } catch (e: Exception) {
                Log.e("AAA_TIMER", "Exception during stop()", e)
            }
        } ?: Log.e("AAA_TIMER", "STOP() called but mediaPlayer is NULL")

        mediaPlayer = null

        Log.e("AAA_TIMER", "STOP() FINISHED")
    }
}



