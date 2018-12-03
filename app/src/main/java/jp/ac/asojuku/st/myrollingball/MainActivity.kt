package jp.ac.asojuku.st.myrollingball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
        , SensorEventListener, SurfaceHolder.Callback {


    private var surfaceWidth:Int = 0;
    private var surfaceHeight:Int = 0;

    private val radius = 50.0f;
    private val coef = 1000.0f;

    private var ballX:Float = 0f;
    private var ballY:Float = 0f;
    private var vx:Float = 0f;
    private var vy:Float = 0f;
    private var time:Long = 0L;

    private var rr:Float = 500f;
    private var rl:Float = 300f;
    private var rt:Float = 300f;
    private var rb:Float = 400f;

    private var gamingFlg:Boolean = true;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder;
        holder.addCallback(this);
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        btn.setOnClickListener{ onResetButtonTapped() };
    }


    override fun onResume() {
        super.onResume()

    }
    private fun onResetButtonTapped(){
        img1.setImageResource(R.drawable.s_img3);
        txv.setText(R.string.txv);
        ballX = 500f;
        ballY = 800f;

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


    override fun onSensorChanged(event: SensorEvent?) {

        if(event == null) return
        if(time==0L) time = System.currentTimeMillis();

            if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                val x = event.values[0] * -1;
                val y = event.values[1];

                var t = (System.currentTimeMillis() - time).toFloat();

                time = System.currentTimeMillis();
                t /= 1000.0f;

                val dx = (vx * t) + (x * t * t) / 2.0f;
                val dy = (vy * t) + (y * t * t) / 2.0f;

                ballX += (dx * coef)

                ballY += (dy * coef)

                vx += (x * t);
                vy += (y * t);

                if (ballX - radius < 0 && vx < 0) {

                    vx = -vx / 1.5f;
                    ballX = radius;
                } else if (ballX + radius > surfaceWidth && vx > 0) {

                    vx = -vx / 1.5f;
                    ballX = (surfaceWidth - radius);
                }

                if ((ballY - radius) < 0 && vy < 0) {

                    vy = -vy / 1.5f;
                    ballY = radius;
                } else if ((ballY + radius) > surfaceHeight && vy > 0) {

                    vy = -vy / 1.5f;
                    ballY = surfaceHeight - radius;
                }


                // 障害物に当たった時
                if (ballX > rl - radius && ballX < rr + radius && ballY > rt - radius && ballY < rb + radius) {
                    vx = -vx / 0f;
                    vy = -vy / 0f;
                    gamingFlg = false;
                    img1.setImageResource(R.drawable.s_img_5579);
                    txv.setText(R.string.result_Lose);
                }

                if (ballX > (800f - radius) && ballX < (1000f + radius) && ballY > (200f - radius) && ballY < (800f + radius)) {
                    vx = -vx / 0f;
                    vy = -vy / 0f;
                    gamingFlg = false;
                    img1.setImageResource(R.drawable.s_img_5579);
                    txv.setText(R.string.result_Lose);
                }

                if (ballX < (100f + radius) && ballY < (100f + radius)) {
                    vx = -vx / 0f;
                    vy = -vy / 0f;
                    gamingFlg = false;
                    img1.setImageResource(R.drawable.s_img_5579);
                    txv.setText(R.string.result_Lose);
                }

                // ゴールに当たった時
                if (ballX > (1000f - radius) && ballY < (100f + radius)) {
                    vx = -vx / 0f;
                    vy = -vy / 0f;
                    gamingFlg = false;
                    img1.setImageResource(R.drawable.s_img2_400x400);
                    txv.setText(R.string.result_Win);
                }

                if (gamingFlg==false){

                }

                if (gamingFlg) {

                    this.drawCanvas();
                }
            }
    }

    override fun surfaceChanged(
            holder: SurfaceHolder?, format: Int,
            width: Int, height: Int) {

        surfaceWidth = width
        surfaceHeight = height

        ballX = (width/2).toFloat()
        ballY = (height/2).toFloat()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as
                SensorManager;

        sensorManager.unregisterListener(this);
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager;

        val accSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(
                this,
                accSensor,
                SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun drawCanvas(){

        val canvas = surfaceView.holder.lockCanvas();

        canvas.drawColor(Color.BLACK)

        canvas.drawCircle(
                ballX,
                ballY,
                radius,
                Paint().apply{
                    color = Color.RED; }
        );
        canvas.drawRect(
                rl,
                rt,
                rr,
                rb,
                Paint().apply{
                    color = Color.YELLOW; }
        );
        canvas.drawRect(
                0f,
                0f,
                100f,
                100f,
                Paint().apply{
                    color = Color.YELLOW; }
        );

        canvas.drawRect(
                800f,
                200f,
                1000f,
                800f,
                Paint().apply{
                    color = Color.YELLOW; }
        );

        canvas.drawRect(
                1000f,
                0f,
                1200f,
                100f,
                Paint().apply{
                    color = Color.BLUE; }
        );

        surfaceView.holder.unlockCanvasAndPost(canvas)
    }




}
