/****************************************************************************
Copyright (c) 2014-2016 Chukong Technologies Inc.
Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.

https://axys1.github.io/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/

package org.axys1.lib;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import org.axys1.lib.AxysVideoView.OnVideoEventListener;

import java.lang.ref.WeakReference;

public class AxysVideoHelper {

    private FrameLayout mLayout = null;
    private AxysActivity mActivity = null;  
    private SparseArray<AxysVideoView> sVideoViews = null;
    static VideoHandler mVideoHandler = null;
    
    AxysVideoHelper(AxysActivity activity,FrameLayout layout)
    {
        mActivity = activity;
        mLayout = layout;
        
        mVideoHandler = new VideoHandler(this);
        sVideoViews = new SparseArray<AxysVideoView>();
    }
    
    private static int videoTag = 0;
    private final static int VideoTaskCreate = 0;
    private final static int VideoTaskRemove = 1;
    private final static int VideoTaskSetSource = 2;
    private final static int VideoTaskSetRect = 3;
    private final static int VideoTaskStart = 4;
    private final static int VideoTaskPause = 5;
    private final static int VideoTaskResume = 6;
    private final static int VideoTaskStop = 7;
    private final static int VideoTaskSeek = 8;
    private final static int VideoTaskSetVisible = 9;
    private final static int VideoTaskRestart = 10;
    private final static int VideoTaskKeepRatio = 11;
    private final static int VideoTaskFullScreen = 12;
    private final static int VideoTaskSetLooping = 13;
     private final static int VideoTaskSetUserInputEnabled = 14;
    final static int KeyEventBack = 1000;
    
    static class VideoHandler extends Handler{
        WeakReference<AxysVideoHelper> mReference;
        
        VideoHandler(AxysVideoHelper helper){
            mReference = new WeakReference<AxysVideoHelper>(helper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case VideoTaskCreate: {
                AxysVideoHelper helper = mReference.get();
                helper._createVideoView(msg.arg1);
                break;
            }
            case VideoTaskRemove: {
                AxysVideoHelper helper = mReference.get();
                helper._removeVideoView(msg.arg1);
                break;
            }
            case VideoTaskSetSource: {
                AxysVideoHelper helper = mReference.get();
                helper._setVideoURL(msg.arg1, msg.arg2, (String)msg.obj);
                break;
            }
            case VideoTaskStart: {
                AxysVideoHelper helper = mReference.get();
                helper._startVideo(msg.arg1);
                break;
            }
            case VideoTaskSetRect: {
                AxysVideoHelper helper = mReference.get();
                Rect rect = (Rect)msg.obj;
                helper._setVideoRect(msg.arg1, rect.left, rect.top, rect.right, rect.bottom);
                break;
            }
            case VideoTaskFullScreen:{
                AxysVideoHelper helper = mReference.get();
                Rect rect = (Rect)msg.obj;
                if (msg.arg2 == 1) {
                    helper._setFullScreenEnabled(msg.arg1, true, rect.right, rect.bottom);
                } else {
                    helper._setFullScreenEnabled(msg.arg1, false, rect.right, rect.bottom);
                }
                break;
            }
            case VideoTaskPause: {
                AxysVideoHelper helper = mReference.get();
                helper._pauseVideo(msg.arg1);
                break;
            }
            case VideoTaskResume: {
                AxysVideoHelper helper = mReference.get();
                helper._resumeVideo(msg.arg1);
                break;
            }
            case VideoTaskStop: {
                AxysVideoHelper helper = mReference.get();
                helper._stopVideo(msg.arg1);
                break;
            }
            case VideoTaskSeek: {
                AxysVideoHelper helper = mReference.get();
                helper._seekVideoTo(msg.arg1, msg.arg2);
                break;
            }
            case VideoTaskSetVisible: {
                AxysVideoHelper helper = mReference.get();
                if (msg.arg2 == 1) {
                    helper._setVideoVisible(msg.arg1, true);
                } else {
                    helper._setVideoVisible(msg.arg1, false);
                }
                break;
            }
            case VideoTaskRestart: {
                AxysVideoHelper helper = mReference.get();
                helper._restartVideo(msg.arg1);
                break;
            }
            case VideoTaskKeepRatio: {
                AxysVideoHelper helper = mReference.get();
                if (msg.arg2 == 1) {
                    helper._setVideoKeepRatio(msg.arg1, true);
                } else {
                    helper._setVideoKeepRatio(msg.arg1, false);
                }
                break;
            }
            case VideoTaskSetLooping: {
                AxysVideoHelper helper = mReference.get();
                helper._setLooping(msg.arg1, msg.arg2 != 0);
                break;
            }

            case VideoTaskSetUserInputEnabled: {
                AxysVideoHelper helper = mReference.get();
                helper._setUserInputEnabled(msg.arg1, msg.arg2 != 0);
                break;
            }
            
            case KeyEventBack: {
                AxysVideoHelper helper = mReference.get();
                helper.onBackKeyEvent();
                break;
            }            

            default:
                break;
            }
            
            super.handleMessage(msg);
        }
    }
    
    private class VideoEventRunnable implements Runnable
    {
        private int mVideoTag;
        private int mVideoEvent;
        
        public VideoEventRunnable(int tag,int event) {
            mVideoTag = tag;
            mVideoEvent = event;
        }
        @Override
        public void run() {
            nativeExecuteVideoCallback(mVideoTag, mVideoEvent);
        }
        
    }
    
    public static native void nativeExecuteVideoCallback(int index,int event);
    
    OnVideoEventListener videoEventListener = new OnVideoEventListener() {
        
        @Override
        public void onVideoEvent(int tag,int event) {
            mActivity.runOnGLThread(new VideoEventRunnable(tag, event));
        }
    };
    
    
    public static int createVideoWidget() {
        Message msg = new Message();
        msg.what = VideoTaskCreate;
        msg.arg1 = videoTag;
        mVideoHandler.sendMessage(msg);
        
        return videoTag++;
    }
    
    private void _createVideoView(int index) {
        AxysVideoView videoView = new AxysVideoView(mActivity,index);
        sVideoViews.put(index, videoView);
        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        mLayout.addView(videoView, lParams);
        videoView.setZOrderOnTop(true);
        videoView.setOnCompletionListener(videoEventListener);
    }
    
    public static void removeVideoWidget(int index){
        Message msg = new Message();
        msg.what = VideoTaskRemove;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _removeVideoView(int index) {
        AxysVideoView view = sVideoViews.get(index);
        if (view != null) {
            view.stopPlayback();
            sVideoViews.remove(index);
            mLayout.removeView(view);
        }
    }
    
    public static void setVideoUrl(int index, int videoSource, String videoUrl) {
        Message msg = new Message();
        msg.what = VideoTaskSetSource;
        msg.arg1 = index;
        msg.arg2 = videoSource;
        msg.obj = videoUrl;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoURL(int index, int videoSource, String videoUrl) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            switch (videoSource) {
            case 0:
                videoView.setVideoFileName(videoUrl);
                break;
            case 1:
                videoView.setVideoURL(videoUrl);
                break;
            default:
                break;
            }
        }
    }

    public static void setLooping(int index, boolean looping) {
        Message msg = new Message();
        msg.what = VideoTaskSetLooping;
        msg.arg1 = index;
        msg.arg2 = looping ? 1 : 0;
        mVideoHandler.sendMessage(msg);
    }

    private void _setLooping(int index, boolean looping) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setLooping(looping);
        }
    }

    public static void setUserInputEnabled(int index, boolean enableInput) {
        Message msg = new Message();
        msg.what = VideoTaskSetUserInputEnabled;
        msg.arg1 = index;
        msg.arg2 = enableInput ? 1 : 0;
        mVideoHandler.sendMessage(msg);
    }

    private void _setUserInputEnabled(int index, boolean enableInput) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setUserInputEnabled(enableInput);
        }
    }
    
    public static void setVideoRect(int index, int left, int top, int maxWidth, int maxHeight) {
        Message msg = new Message();
        msg.what = VideoTaskSetRect;
        msg.arg1 = index;
        msg.obj = new Rect(left, top, maxWidth, maxHeight);
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoRect(int index, int left, int top, int maxWidth, int maxHeight) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setVideoRect(left,top,maxWidth,maxHeight);
        }
    }
    
    public static void setFullScreenEnabled(int index, boolean enabled, int width, int height) {
        Message msg = new Message();
        msg.what = VideoTaskFullScreen;
        msg.arg1 = index;
        if (enabled) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        msg.obj = new Rect(0, 0, width, height);
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setFullScreenEnabled(int index, boolean enabled, int width,int height) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setFullScreenEnabled(enabled, width, height);
        }
    }
    
    private void onBackKeyEvent() {
        int viewCount = sVideoViews.size();
        for (int i = 0; i < viewCount; i++) {
            int key = sVideoViews.keyAt(i);
            AxysVideoView videoView = sVideoViews.get(key);
            if (videoView != null) {
                videoView.setFullScreenEnabled(false, 0, 0);
                mActivity.runOnGLThread(new VideoEventRunnable(key, KeyEventBack));
            }
        }
    }
    
    public static void startVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskStart;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _startVideo(int index) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.start();
        }
    }
    
    public static void pauseVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskPause;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _pauseVideo(int index) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.pause();
        }
    }

    public static void resumeVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskResume;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _resumeVideo(int index) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.resume();
        }
    }
    
    public static void stopVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskStop;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _stopVideo(int index) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.stop();
        }
    }
    
    public static void restartVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskRestart;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _restartVideo(int index) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.restart();
        }
    }
    
    public static void seekVideoTo(int index,int msec) {
        Message msg = new Message();
        msg.what = VideoTaskSeek;
        msg.arg1 = index;
        msg.arg2 = msec;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _seekVideoTo(int index,int msec) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.seekTo(msec);
        }
    }
    
    public static void setVideoVisible(int index, boolean visible) {
        Message msg = new Message();
        msg.what = VideoTaskSetVisible;
        msg.arg1 = index;
        if (visible) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoVisible(int index, boolean visible) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            if (visible) {
                videoView.fixSize();
                videoView.setVisibility(View.VISIBLE);
            } else {
                videoView.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    public static void setVideoKeepRatioEnabled(int index, boolean enable) {
        Message msg = new Message();
        msg.what = VideoTaskKeepRatio;
        msg.arg1 = index;
        if (enable) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoKeepRatio(int index, boolean enable) {
        AxysVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setKeepRatio(enable);
        }
    }
}
