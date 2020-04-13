package com.severgames.lpx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        btn_game_new.setOnClickListener(this)
        btn_scores.setOnClickListener(this)
        btn_game_continue.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_game_new -> startActivity(Intent(this@StartActivity, GameActivity::class.java))
            R.id.btn_game_continue -> startActivity(Intent(this@StartActivity, GameActivity::class.java))
            R.id.btn_scores -> startActivity(Intent(this@StartActivity, ScoreActivity::class.java))
        }
    }
}
