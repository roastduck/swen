package com.java.g15;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.java.g15.promise.Callback;
import com.java.g15.promise.Promise;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadingImageView extends FrameLayout {
    private ImageView iv;
    private AVLoadingIndicatorView loading;
    private Promise promsie;

    public LoadingImageView(Context context) {
        super(context);
        init();
    }

    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ImageView getImageView(){
        return iv;
    }

    private void init() {
        inflate(getContext(), R.layout.loading_image_view, this);
        iv = (ImageView)findViewById(R.id.l_image);
        loading = (AVLoadingIndicatorView)findViewById(R.id.l_loading);
    }

    public void clearPicture() {
        iv.setImageBitmap(null);
    }

    public void showPictureByUrl(String url, Storage storage) {
        loading.show();
        promsie = storage.getPicCached(url);
        promsie.thenUI(new Callback<Bitmap, Object>() {
            @Override
            public Object run(Bitmap picture) {
                iv.setImageBitmap(picture);
                loading.hide();
                return null;
            }
        });
    }

    public Promise showPictureByUrl(String url, Storage storage, Callback callback) {
        loading.show();
        promsie = storage.getPicCached(url);
        promsie.thenUI(new Callback<Bitmap, Object>() {
            @Override
            public Object run(Bitmap picture) {
                iv.setImageBitmap(picture);
                loading.hide();
                return null;
            }
        });
        promsie.failUI(callback);
        return promsie;
    }
}
