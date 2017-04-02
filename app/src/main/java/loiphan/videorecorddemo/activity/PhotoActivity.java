package loiphan.videorecorddemo.activity;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import butterknife.Bind;
import butterknife.ButterKnife;
import loiphan.videorecorddemo.R;

/**
 * Copyright (c) 2017, Stacck Pte Ltd. All rights reserved.
 *
 * @author Lio <lphan@stacck.com>
 * @version 1.0
 * @since March 27, 2017
 */

public class PhotoActivity extends AppCompatActivity {

    @Bind(R.id.imvPhoto)
    ImageView imvPhoto;
    @Bind(R.id.btnLoad)
    Button btnLoad;
    @Bind(R.id.btnLoad2)
    Button btnLoad2;

    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions option;

    private String url = "https://stacck.s3.amazonaws.com/uploads/photos/000/011/917/original/open-uri20170327-13268-vhvw97?1490608632";
    private String url2 = "https://stacck.s3.amazonaws.com/uploads/photos/000/011/917/display/open-uri20170327-13268-vhvw97.jpg?1490608632";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);

        option = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.color.gray_55)
                .showImageOnLoading(R.color.gray_55)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.NONE)
                .considerExifParams(true)
                .cacheOnDisk(true).build();

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhoto();
            }
        });

        btnLoad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhoto2();
            }
        });
    }

    private void loadPhoto() {
//        mImageLoader.displayImage(url, imvPhoto, option, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                Log.e("LIO", "loadedImage " + loadedImage.getByteCount() + " - " + loadedImage.getHeight() + " - " + loadedImage.getWidth());
//            }
//        });

        mImageLoader.loadImage(url, option, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.e("LIO", "loadedImage " + loadedImage.getByteCount() + " - " + loadedImage.getWidth() + " - " + loadedImage.getHeight() + " | " + (loadedImage.getWidth() * 9 / 10) + " - " + (loadedImage.getHeight() * 9 / 10));
                imvPhoto.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, loadedImage.getWidth() * 10 / 10, loadedImage.getHeight() * 10 / 10, false));
            }
        });
    }

    private void loadPhoto2() {
//        mImageLoader.displayImage(url, imvPhoto, option, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                Log.e("LIO", "loadedImage " + loadedImage.getByteCount() + " - " + loadedImage.getHeight() + " - " + loadedImage.getWidth());
//            }
//        });

//        mImageLoader.loadImage(url2, option, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                imvPhoto.setImageBitmap(loadedImage);
//                Log.e("LIO", "loadedImage " + loadedImage.getByteCount() + " - " + loadedImage.getHeight() + " - " + loadedImage.getWidth());
//            }
//        });

        int[] maxTextureSize = new int[1];
        GLES20.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

        Log.e("LIO", "maxTextureSize " + maxTextureSize[0]);

        Log.e("LIO", "getMaximumTextureSize " + getMaximumTextureSize());
    }

    public int getMaximumTextureSize()
    {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++)
        {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
            {
                maximumTextureSize = textureSize[0];
            }

            Log.i("GLHelper", Integer.toString(textureSize[0]));
        }

        // Release
        egl.eglTerminate(display);
        Log.i("GLHelper", "Maximum GL texture size: " + Integer.toString(maximumTextureSize));

        return maximumTextureSize;
    }
}
