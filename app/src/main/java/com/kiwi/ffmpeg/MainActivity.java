package com.kiwi.ffmpeg;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();


    String[] manifest = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int PERMISSIOM_CODE = 0x01;

    Compressor compressor;


    EditText editCmd;
    TextView txCmd;

    /**
     * Available Methods
     * <p>
     * loadBinary(FFmpegLoadBinaryResponseHandler ffmpegLoadBinaryResponseHandler) throws FFmpegNotSupportedException
     * execute(Map<String, String> environvenmentVars, String cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException
     * execute(String cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException
     * getDeviceFFmpegVersion() throws FFmpegCommandAlreadyRunningException
     * getLibraryFFmpegVersion()
     * isFFmpegCommandRunning()
     * killRunningProcesses()
     * setTimeout(long timeout)
     * <p>
     * 1、首先找一个mp4文件，将之命名为in.mp4
     * 2、使用命令adb push in.mp4 /sdcard/in.mp4  将in.mp4文件push到手机sd卡上
     * 3、执行界面上的命令
     * 4、执行结束，可以使用命令 adb pull /sdcard/out.mp4 out.mp4 将手机上的out.mp4导出到电脑上
     * 5、对比in.mp4与out.mp4的视频质量和大小
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editCmd = findViewById(R.id.edit_cmd);
        txCmd = findViewById(R.id.tx_result);
        txCmd.setMovementMethod(ScrollingMovementMethod.getInstance());

        compressor = new Compressor(this);

        ActivityCompat.requestPermissions(this, manifest, PERMISSIOM_CODE);

        //将sdcard中的视频in.mp4 压缩输出到sdcard卡中out.mp4
        String cmd = "-y -i " + "/sdcard/in.mp4" + " -strict -2 -vcodec libx264 -preset ultrafast " +
                "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 640x480 -aspect 16:9 " + "/sdcard/out.mp4";

        editCmd.setText(cmd);
    }

    /**
     * 将ffmpeg加载进来
     *
     * @param view
     */
    public void load(View view) {
        compressor.loadBinary(new Compressor.InitListener() {
            @Override
            public void onLoadSuccess() {
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadFail(String reason) {
                Toast.makeText(MainActivity.this, "onLoadFail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void execute(View view) {
        String cmd = editCmd.getText().toString();

        Log.e(TAG, "execute: " + cmd);
        compressor.execCommand(cmd, new Compressor.CompressListener() {
            @Override
            public void onExecSuccess(String message) {
                txCmd.setText(message + "\n");
            }

            @Override
            public void onExecFail(String reason) {
                txCmd.setText(reason + "\n");
            }

            @Override
            public void onExecProgress(String message) {
                txCmd.setText(message + "\n");
            }
        });
    }

}
