package com.severgames.lpx

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.severgames.lpx.database.ScoreViewModel
import com.severgames.lpx.models.ScoreModel
import kotlinx.android.synthetic.main.activity_result.*

class ScoreActivity : AppCompatActivity() {
    private lateinit var scoreVM: ScoreViewModel
    private var mScores = ArrayList<ScoreModel>()
    private var array = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        btn_close.setOnClickListener{
            finish()
        }

        scoreVM = ViewModelProvider(this).get(ScoreViewModel::class.java)
        scoreVM.allScore.observe(this, Observer { scores ->
            scores?.let {
                mScores.addAll(it)
                mScores.sortBy {it.value}
                mScores.reverse()
                var i = 0
                mScores.forEach {
                    if (i<10) {
                        var curTime: String = ""
                        when(it.value) {
                            in 0..59 -> {
                                if (it.value < 10) {
                                    curTime = "00:0" + it.value.toString()
                                } else {
                                    curTime = "00:" + it.value.toString()
                                }
                            }
                            in 60..119 -> {
                                if (it.value < 70) {
                                    curTime = "01:0" + (it.value - 60).toString()
                                } else {
                                    curTime = "01:" + (it.value - 60).toString()
                                }
                            }
                        }
                        array.add(curTime)
                        Log.d("TAG", it.value.toString())
                        i++
                    }
                }

                val adapter = ArrayAdapter(this,
                    R.layout.item_listview, array)

                listViewScore.setAdapter(adapter)
            }
        })
    }
}
