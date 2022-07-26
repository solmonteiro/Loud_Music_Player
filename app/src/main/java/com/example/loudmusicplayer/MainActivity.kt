package com.example.loudmusicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0
    private lateinit var playBtn: Button
    private lateinit var volumeBar: SeekBar
    private lateinit var positionBar: SeekBar
    private lateinit var remainingTimeLabel:TextView
    private lateinit var elapsedTimeLabel:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playBtn = findViewById(R.id.playBtn)
        volumeBar = findViewById(R.id.volumeBar)
        positionBar = findViewById(R.id.positionBar)
        remainingTimeLabel=findViewById(R.id.remainingTimeLabel)
        elapsedTimeLabel=findViewById(R.id.elapsedTimeLabel)

        mp = MediaPlayer.create(this, R.raw.producao)
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration

        //Volume Bar


        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        var volumeNum = progress / 100.0f
                        mp.setVolume(volumeNum, volumeNum)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            }
        )

        //Position Bar
        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            }
        )

        //Thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()


        playBtn.setOnClickListener {
            if (mp.isPlaying) {
                //Stop
                mp.pause()
                playBtn.setBackgroundResource(R.drawable.play)
            } else {
                //Start
                mp.start()
                playBtn.setBackgroundResource(R.drawable.stop)
            }

        }


    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            //Update positionBar
            positionBar.progress = currentPosition

            //Update Labels
            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text=elapsedTime

            var remainingTime=createTimeLabel(totalTime-currentPosition)
            remainingTimeLabel.text="-$remainingTime"
        }

    }


    fun createTimeLabel(time:Int):String{
        var timeLabel=""
        var min =time/1000/60
        var sec =time/1000%60

        timeLabel="$min:"
        if (sec<10)timeLabel+="0"
        timeLabel+=sec

        return timeLabel

    }


}