package com.severgames.lpx

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.severgames.lpx.database.ScoreViewModel
import com.severgames.lpx.models.ScoreModel
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList

class GameActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var scoreVM: ScoreViewModel
    private var score = 0
    private var firstPosition = 111
    private var boxes : ArrayList<Int> = arrayListOf()
    private var listOfItems = arrayListOf(
            R.drawable.iv_01,
            R.drawable.iv_02,
            R.drawable.iv_03,
            R.drawable.iv_04,
            R.drawable.iv_05,
            R.drawable.iv_06,
            R.drawable.iv_07,
            R.drawable.iv_08,
            R.drawable.iv_09,
            R.drawable.iv_10,
            R.drawable.iv_11,
            R.drawable.iv_12,
            R.drawable.iv_01,
            R.drawable.iv_02,
            R.drawable.iv_03,
            R.drawable.iv_04,
            R.drawable.iv_05,
            R.drawable.iv_06,
            R.drawable.iv_07,
            R.drawable.iv_08,
            R.drawable.iv_09,
            R.drawable.iv_10,
            R.drawable.iv_11,
            R.drawable.iv_12
        )

    lateinit var timer: CountDownTimer
    lateinit var adapter: ImageAdapter
    var timeCount = 120
    
    init {
        for (i in 0..23){
            boxes.add(R.drawable.iv_box)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        scoreVM = ViewModelProvider(this).get(ScoreViewModel::class.java)

        adapter = ImageAdapter(this, R.layout.list_item, boxes)
        gridview.adapter = adapter

        startNewGame()

        gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            boxes.set(position, listOfItems[position])
            Log.d("TAG", "FirstPosition: " + firstPosition)
            adapter.run{
                notifyDataSetChanged()
            }
            if (firstPosition != 111){
                checkСoincidence(firstPosition, position, adapter)
            }else {
                firstPosition = position
            }
        }
    }

    private fun checkСoincidence(firstPos: Int, secondPos: Int, adapterIA: ImageAdapter) {
        firstPosition = 111
        if (listOfItems[firstPos]==listOfItems[secondPos] && firstPos!=secondPos){
            Log.d("TAG", "Ravno" + ". First: " + firstPos + ". Second: " + secondPos + ". FirstPosition: " + firstPosition)
            score++
            tvPairsCount.text = score.toString() + "/12"
            if (score==12){
                finishGame(true)
            }
        } else {
            Log.d("TAG", "Ne Ravno")
            Handler().postDelayed({
                boxes.set(firstPos, R.drawable.iv_box)
                boxes.set(secondPos, R.drawable.iv_box)
                adapterIA.notifyDataSetChanged()
            }, 1000)
        }
    }

    private fun finishGame(b: Boolean) {
        timer.cancel()
        gridview.visibility = View.GONE
        llButton.visibility = View.VISIBLE
        if (score==12){
            btn_success.visibility = View.VISIBLE
            var scoreModel = ScoreModel(id = timeCount.toLong(), value = timeCount)

            scoreVM.insert(scoreModel)
        }

        btn_again_end.setOnClickListener(this)
        btn_score_end.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_again_end -> startNewGame()
            R.id.btn_score_end -> startActivity(Intent(this@GameActivity, ScoreActivity::class.java))
        }
    }

    private fun startNewGame() {
        timeCount = 120
        score = 0
        tvPairsCount.text = score.toString() + "/12"
        Collections.shuffle(listOfItems)
        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeCount--
                when(timeCount){
                    in 0..59 -> {
                        if (timeCount<10){
                            tvTimeCount.text = "00:0" + timeCount.toString()
                        } else {
                            tvTimeCount.text = "00:" + timeCount.toString()
                        }
                    }
                    in 60..119 -> {
                        if (timeCount<70) {
                            tvTimeCount.text = "01:0" + (timeCount - 60).toString()
                        } else {
                            tvTimeCount.text = "01:" + (timeCount - 60).toString()
                        }
                    }
                }

            }

            override fun onFinish() {
                finishGame(true)
            }
        }
        timer.start()

        boxes.clear()
        for (i in 0..23){
            boxes.add(R.drawable.iv_box)
        }

        gridview.visibility = View.VISIBLE
        llButton.visibility = View.GONE
        btn_success.visibility = View.GONE

        btn_again_end.setOnClickListener(null)
        btn_score_end.setOnClickListener(null)

        adapter.notifyDataSetChanged()
    }

}
