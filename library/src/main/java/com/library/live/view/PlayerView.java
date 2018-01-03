package com.library.live.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.R;
import com.library.live.IsOutBuffer;
import com.library.live.stream.IsInBuffer;
import com.library.live.stream.upd.WeightCallback;


/**
 * Created by android1 on 2017/11/18.
 */

public class PlayerView extends RelativeLayout implements IsInBuffer, WeightCallback {
    private ImageView loadimag;
    private SurfaceView surfaceview;
    private TextView loadtext;
    private ObjectAnimator rota;
    private IsOutBuffer isOutBuffer;
    private boolean bufferAnimator = true;
    private Handler handler;
    private UIRunnable uiRunnable;
    private boolean isCenterScaleType = false;

    public PlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.player_view, this, true);
        setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        loadimag = findViewById(R.id.loadimag);
        surfaceview = findViewById(R.id.surfaceview);
        loadtext = findViewById(R.id.loadtext);

        rota = ObjectAnimator.ofFloat(loadimag, "rotation", 0f, 360f);
        rota.setDuration(1000);
        rota.setInterpolator(new LinearInterpolator());
        rota.setRepeatCount(-1);

        handler = new Handler(Looper.getMainLooper());
        uiRunnable = new UIRunnable();
    }

    public SurfaceHolder getHolder() {
        return surfaceview.getHolder();
    }

    @Override
    public void isBuffer(boolean isBuffer) {
        if (isOutBuffer != null) {
            isOutBuffer.isBuffer(isBuffer);
        }
        if (bufferAnimator) {
            uiRunnable.setBuffer(isBuffer);
            handler.post(uiRunnable);
        }
    }

    @Override
    public void getWeight(final double weight) {
        if (isCenterScaleType) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams lp = surfaceview.getLayoutParams();
                    if (getHeight() * weight > getWidth()) {
                        lp.width = getWidth();
                        lp.height = (int) (getWidth() / weight);
                    } else {
                        lp.width = (int) (getHeight() * weight);
                        lp.height = getHeight();
                    }
                    surfaceview.setLayoutParams(lp);
                }
            });
        }
    }

    private class UIRunnable implements Runnable {

        private boolean isBuffer;

        @Override
        public void run() {
            if (isBuffer) {
                if (loadimag.getVisibility() == GONE) {
                    loadimag.setVisibility(VISIBLE);
                    loadtext.setVisibility(VISIBLE);
                    rota.start();
                }
            } else {
                if (loadimag.getVisibility() == VISIBLE) {
                    loadimag.setVisibility(GONE);
                    loadtext.setVisibility(GONE);
                    rota.cancel();
                }
            }
        }

        public void setBuffer(boolean buffer) {
            isBuffer = buffer;
        }

    }

    public void setIsOutBuffer(IsOutBuffer isOutBuffer) {
        this.isOutBuffer = isOutBuffer;
    }

    public void setBufferAnimator(boolean bufferAnimator) {
        this.bufferAnimator = bufferAnimator;
    }

    public void setCenterScaleType(boolean centerScaleType) {
        isCenterScaleType = centerScaleType;
    }

    public void stop() {
        uiRunnable.setBuffer(false);
        handler.post(uiRunnable);
    }
}
