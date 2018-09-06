package com.kiwi.ffmpeg;

import android.app.Activity;
import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class Compressor {
    private FFmpeg ffmpeg;

    public Compressor(Context context) {
        ffmpeg = FFmpeg.getInstance(context);
    }

    public void loadBinary(final InitListener mListener) {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                    mListener.onLoadFail("incompatible with this device");
                }

                @Override
                public void onSuccess() {
                    mListener.onLoadSuccess();
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void execCommand(String cmd, final CompressListener mListener) {
        try {
            String[] cmds = cmd.split(" ");
            ffmpeg.execute(cmds, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    mListener.onExecProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    mListener.onExecFail(message);
                }

                @Override
                public void onSuccess(String message) {
                    mListener.onExecSuccess(message);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public FFmpeg getFFmpeg() {
        return ffmpeg;
    }

    public interface CompressListener {
        public void onExecSuccess(String message);

        public void onExecFail(String reason);

        public void onExecProgress(String message);
    }

    public interface InitListener {
        public void onLoadSuccess();

        public void onLoadFail(String reason);
    }

}
