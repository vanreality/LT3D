package com.lt3d;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ModelActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;

    private String nameSfb;

    public ModelActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String nameModel;
        super.onCreate(savedInstanceState);

        nameModel=getIntent().getStringExtra("modelName");
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        switch (nameModel){
            case "Skull":
                nameSfb="skull/12140_Skull_v3_L2.sfb";
                break;
            case "Venus":
                nameSfb="venus/12328_Statue_v1_L2.sfb";
                break;
            case "Egypt lion":
                nameSfb="egypt/10085_egypt_sphinx_iterations-2.sfb";
                break;
            case "Iron man":
                nameSfb="ironman/IronMan.sfb";
                break;
            case "Deer":
                nameSfb="deer/12961_White-Tailed_Deer_v1_l2.sfb";
                break;
            case "Dog":
                nameSfb="dog/12228_Dog_v1_L2.sfb";

        }


        setContentView(R.layout.activity_model);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.model_scan);


        /**
         * Build the 3D model
         */
        ModelRenderable.builder()
                .setSource(this, Uri.parse(nameSfb))
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });



        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (modelRenderable == null) {
                        return;
                    }
                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable model and add it to the anchor.
                    TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());

                    //set rotation in direction (x,y,z) in degrees 90
                    node.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), -90f));
                    node.setParent(anchorNode);
                    node.setRenderable(modelRenderable);
                    node.select();


                });

    }


    /**
     * Check if the version of device is enough
     * @param activity
     * @return
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        double MIN_OPENGL_VERSION=3.0;
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
           // Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
