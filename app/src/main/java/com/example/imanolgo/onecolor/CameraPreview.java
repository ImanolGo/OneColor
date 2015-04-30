package com.example.imanolgo.onecolor;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private OneColorDraw mOneColorDraw;

    //This variable is responsible for getting and setting the camera settings
    private Camera.Parameters parameters;
    //this variable stores the camera preview size
    private Camera.Size previewSize;
    //this array stores the pixels as hexadecimal pairs
    private int[] pixels;

    public CameraPreview(Context context, Camera camera, OneColorDraw oneColorDraw) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mOneColorDraw = oneColorDraw;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();

                ///initialize the variables
                parameters = mCamera.getParameters();
                previewSize = parameters.getPreviewSize();
                pixels = new int[previewSize.width * previewSize.height];

            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
            mCamera.release();
            mCamera = null;
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            mCamera.setPreviewDisplay(mHolder);

            parameters = mCamera.getParameters();
            previewSize = parameters.getPreviewSize();
            pixels = new int[previewSize.width * previewSize.height];


            // Preview callback used whenever new viewfinder frame is available
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera)
                {
                    //transforms NV21 pixel data into RGB pixels
                    //decodeYUV420SP(pixels, data, previewSize.width, previewSize.height);

                    int position = (previewSize.width*previewSize.height/2) +previewSize.width/2 ; //the center position
                    //Log.i("Pixels", "width = " + previewSize.width);
                    //Log.i("Pixels", "height = " + previewSize.height);
                    //Log.i("Pixels", "position = " + position);
                    //Log.i("Pixels", "j = " + position/previewSize.width);
                    //Log.i("Pixels", "i = " + position%previewSize.width);
                    int pixel = getPixel( data, previewSize.width, previewSize.height,position);
                    mOneColorDraw.setColor(pixel);
                    mOneColorDraw.invalidate();
                    //Outuput the value of the top center pixel in the preview to LogCat
                    //Log.i("Pixels", "The top right pixel has the following RGB (hexadecimal) values:"
                            //+Integer.toHexString(pixel));
                }
            });

            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(mCamera);

    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
            mCamera.release();
    }

    //Method from Ketai project! Not mine! See below...
    void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {       int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)                  r = 0;               else if (r > 262143)
                    r = 262143;
                if (g < 0)                  g = 0;               else if (g > 262143)
                    g = 262143;
                if (b < 0)                  b = 0;               else if (b > 262143)
                    b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    int getPixel(byte[] yuv420sp, int width, int height, int position) {

        final int frameSize = width * height;
        int j = position/width;
        int i = position%width;
        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;

        int y = (0xff & ((int) yuv420sp[position])) - 16;
        if (y < 0)
            y = 0;
        if ((i & 1) == 0) {
            v = (0xff & yuv420sp[uvp++]) - 128;
            u = (0xff & yuv420sp[uvp++]) - 128;
        }

        int y1192 = 1192 * y;
        int r = (y1192 + 1634 * v);
        int g = (y1192 - 833 * v - 400 * u);
        int b = (y1192 + 2066 * u);

        if (r < 0)                  r = 0;               else if (r > 262143)
            r = 262143;
        if (g < 0)                  g = 0;               else if (g > 262143)
            g = 262143;
        if (b < 0)                  b = 0;               else if (b > 262143)
            b = 262143;

        int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        return pixel;
    }
}