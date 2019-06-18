package com.lt3d.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import com.lt3d.R;
import com.lt3d.tools.Alert;
import com.lt3d.tools.AugmentedImageNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScanFragment extends Fragment {
    private ArFragment arFragment;
    private ImageView fitToScanView;
    private View view;

    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getContext());

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = factory.inflate(R.layout.fragment_scan, null);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

//        view = inflater.inflate(R.layout.fragment_scan, container, false);
        arFragmentConfig();
        return view;
    }

    private void arFragmentConfig() {
        arFragment = (ArFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        fitToScanView = view.findViewById(R.id.image_view_fit_to_scan);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    }

    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    String text = "Detected Image " + augmentedImage.getIndex();
                    Alert.show(getContext(), text);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    fitToScanView.setVisibility(View.GONE);
                    /**
                    String modelName = augmentedImage.getName();
                    AugmentedImageNode node = new AugmentedImageNode(getContext(), modelName, arFragment);
                    node.setImage(augmentedImage);
                    Log.d("track",augmentedImage.getName());
                    arFragment.getArSceneView().getScene().addChild(node);*/

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        String modelName = augmentedImage.getName();
                        AugmentedImageNode newNode = new AugmentedImageNode(getContext(),modelName, arFragment);
                        newNode.setImage(augmentedImage);
                        augmentedImageMap.put(augmentedImage, newNode);
                        arFragment.getArSceneView().getScene().addChild(newNode);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }


}
