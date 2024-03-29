/*
 *  Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo Caddisfly
 *
 *  Akvo Caddisfly is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo Caddisfly is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.caddisfly.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import org.akvo.caddisfly.Config;
import org.akvo.caddisfly.R;
import org.akvo.caddisfly.util.ImageUtils;
import org.akvo.caddisfly.util.PreferencesUtils;
import org.akvo.caddisfly.util.SoundPoolPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Use the {@link org.akvo.caddisfly.ui.CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("deprecation")
public class CameraFragment extends DialogFragment implements VerboseResultFragment.ResultDialogListener {
    private static final String ARG_PREVIEW_ONLY = "preview";
    public Camera.PictureCallback pictureCallback;
    private int samplingCount;
    private int picturesTaken;
    private boolean mPreviewOnly;
    private SoundPoolPlayer sound;
    private boolean mCancelled = false;
    private AlertDialog progressDialog;
    private Camera mCamera;
    private Fragment mFragment;

    // View to display the camera output.
    private CameraPreview mPreview;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param previewOnly if true will display preview only, otherwise start taking pictures
     * @return A new instance of fragment CameraFragment.
     */
    @SuppressWarnings("SameParameterValue")
    public static CameraFragment newInstance(boolean previewOnly) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PREVIEW_ONLY, previewOnly);
        fragment.setArguments(args);
        return fragment;
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPreviewOnly = getArguments().getBoolean(ARG_PREVIEW_ONLY);
        }
        mFragment = this;

        sound = new SoundPoolPlayer(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Create preview and set it as the content
        boolean opened = safeCameraOpenInView(view);

        if (!opened) {
            return view;
        }
        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//        if (!mPreviewOnly) {
//            dialog.setTitle(R.string.analysisInProgress);
//        } else {
//            dialog.setTitle(R.string.cameraPreview);
//        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mPreviewOnly) {
            startTakingPictures();
        }
    }

    private void startTakingPictures() {

        samplingCount = 0;
        picturesTaken = 0;
        Context context = getActivity();
        progressDialog = new AlertDialog.Builder(context).create();
        progressDialog.setMessage(getString(R.string.analyzingWait));
        progressDialog.setCancelable(false);

        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        progressDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takePicture();
            }
        }, Config.INITIAL_DELAY);

    }

    private void takePicture() {

        if (getActivity() != null) {
            mPreview.startCameraPreview();
            PictureCallback localCallback = new PictureCallback();
            try {
                mCamera.takePicture(null, null, localCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void stopCamera() {
        mCancelled = true;
        releaseCameraAndPreview();
    }

    /**
     * Checks if requested number of pictures were taken
     *
     * @return true if completed, false if not
     */
    public boolean hasTestCompleted() {
        return samplingCount > Config.SAMPLING_COUNT_DEFAULT || picturesTaken > 10;
    }

    private boolean safeCameraOpenInView(View view) {
        boolean qOpened;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        qOpened = (mCamera != null);

        if (qOpened) {
            mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera, mPreviewOnly);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            if (mPreviewOnly) {
                preview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        takePicture();
                        return false;
                    }
                });
            }

            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (getActivity() != null && getActivity() instanceof Cancelled) {
            ((Cancelled) getActivity()).dialogCancelled();
        }
        super.onCancel(dialog);
    }

    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sound.release();
    }

    @Override
    public void onSuccessFinishDialog() {
        this.dismiss();
    }

    public interface Cancelled {
        void dialogCancelled();
    }

    static class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        private final SurfaceHolder mHolder;

        private Camera mCamera;

        private Camera.Size mPreviewSize;

        private List<Camera.Size> mSupportedPreviewSizes;

        private List<String> mSupportedFlashModes;

        private boolean mPreviewOnly = false;

        public CameraPreview(Context context) {
            super(context);
            mHolder = getHolder();
        }

        public CameraPreview(Context context, Camera camera, boolean previewOnly) {
            super(context);
            mPreviewOnly = previewOnly;
            setCamera(camera);
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
        }

        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setCamera(Camera camera) {
            mCamera = camera;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            Camera.Parameters parameters = mCamera.getParameters();

            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);

            List<String> supportedSceneModes = mCamera.getParameters().getSupportedSceneModes();
            if (supportedSceneModes != null && supportedSceneModes.contains(Camera.Parameters.SCENE_MODE_AUTO)) {
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            }

            parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setJpegQuality(100);
            parameters.setZoom(0);

            if (parameters.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringAreas = new ArrayList<>();
                Rect areaRect1 = new Rect(-100, -100, 100, 100);
                meteringAreas.add(new Camera.Area(areaRect1, 1000));
                parameters.setMeteringAreas(meteringAreas);
            }

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (!PreferencesUtils.getBoolean(getContext(), R.string.autoFocusKey, false)) {

                // Attempt to set focus to infinity if supported
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                }
            } else {

                // Force auto focus as per preference
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            }

            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

            Camera.Size mSize;
            for (Camera.Size size : sizes) {
                Log.i("Caddisfly", "Available resolution: " + size.width + " " + size.height);
                if (size.width > 800 && size.width < 1400) {
                    mSize = size;
                    parameters.setPictureSize(mSize.width, mSize.height);
                    break;
                }
            }
            if (mSupportedFlashModes != null && mSupportedFlashModes
                    .contains(Camera.Parameters.FLASH_MODE_ON)) {
                if (mPreviewOnly || PreferencesUtils.getBoolean(getContext(), R.string.useTorchModeKey, false)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                }
            }


            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(parameters);

            requestLayout();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(holder);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            if (mHolder.getSurface() == null) {
                return;
            }

            try {
                Camera.Parameters parameters = mCamera.getParameters();

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                if (mPreviewSize != null) {
                    Camera.Size previewSize = mPreviewSize;
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                }

                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }

        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
            Camera.Size optimalSize = null;

            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) height / width;

            for (Camera.Size size : sizes) {

                if (size.height != width) {
                    continue;
                }
                double ratio = (double) size.width / size.height;
                if (ratio <= targetRatio + ASPECT_TOLERANCE
                        && ratio >= targetRatio - ASPECT_TOLERANCE) {
                    optimalSize = size;
                }
            }

            return optimalSize;
        }
    }

    private class PictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            samplingCount++;
            picturesTaken++;
            if (!mCancelled) {
                if (!hasTestCompleted()) {
                    sound.playShortResource(getActivity(), R.raw.beep);
                    if (pictureCallback == null) {
                        stopCamera();
                        Bitmap bitmap = ImageUtils.getBitmap(bytes);

                        VerboseResultFragment verboseResultFragment =
                                VerboseResultFragment.newInstance(
                                        ImageUtils.getCroppedBitmap(bitmap,
                                                Config.SAMPLE_CROP_LENGTH_DEFAULT),
                                        bitmap, bitmap.getWidth() + " x " + bitmap.getHeight(), mFragment);

                        final FragmentTransaction ft = getFragmentManager().beginTransaction();

                        Fragment prev = getFragmentManager().findFragmentByTag("resultDialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }

                        verboseResultFragment.setCancelable(true);
                        verboseResultFragment.show(ft, "resultDialog");

                        //AlertUtils.showError(getActivity(), R.string.app_name, "Result", ImageUtils.getBitmap(bytes), R.string.ok, null, null);
                    } else {
                        pictureCallback.onPictureTaken(bytes, camera);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                takePicture();
                            }
                        }, Config.INITIAL_DELAY);

                    }
                } else {
                    pictureCallback.onPictureTaken(bytes, camera);
                }
            }
        }
    }
}
