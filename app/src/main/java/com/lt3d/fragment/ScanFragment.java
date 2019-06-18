package com.lt3d.fragment;

import android.net.Uri;
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

public class ScanFragment extends Fragment {
    private ArFragment arFragment;
    private ImageView fitToScanView;
    private View view;
    private String modelName;

    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    /**
     * Create a view corresponding to the ScanFragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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

        arFragmentConfig();
        return view;
    }

    /**
     * Set the view of the fragment to the scanned image
     * Set the UpdateListener to this view
     */
    private void arFragmentConfig() {
        arFragment = (ArFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        fitToScanView = view.findViewById(R.id.image_view_fit_to_scan);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    }

    /**
     * Call function for image detection every frame
     * @param frameTime
     */
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        //Collect the result of tracking
        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        //Get the state of tracking
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
                    modelName = augmentedImage.getName();
                    // Create a augmentedImageNode correponding to the image scanned
                    AugmentedImageNode node = new AugmentedImageNode(getContext(),modelName);
                    // Set the 3D model
                    node.setImage(augmentedImage,modelName);
                    Log.d("track",augmentedImage.getName());
                    arFragment.getArSceneView().getScene().addChild(node);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        AugmentedImageNode newNode = new AugmentedImageNode(getContext(),"add");
                        newNode.setImage(augmentedImage,"add");
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }
}
