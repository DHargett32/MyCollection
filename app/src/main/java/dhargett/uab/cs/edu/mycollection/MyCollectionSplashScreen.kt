package dhargett.uab.cs.edu.mycollection

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import java.util.*

class MyCollectionSplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collection_splash_screen)
        var progressBar = findViewById<ProgressBar>(R.id.pb_loading);

        progressBar.max = 100;
        progressBar.setProgress(50);
        var status = 0;

        Thread(Runnable{
            while(status < 100)
            {
                status++;
                try{
                    Thread.sleep(30);
                    progressBar.setProgress(status);
                }
                catch (e: Exception){
                    Log.e("Error", e.message);
                }
            }
        }).start();

        Timer().schedule(object: TimerTask(){
            override fun run(){
                val intent = Intent(this@MyCollectionSplashScreen, MainActivity::class.java)
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
