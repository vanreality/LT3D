/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lt3d.tools;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.lt3d.fragment.ScanFragment;

import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";
  // The augmented image represented by this node.
  private AugmentedImage image;
  private String imgName;
  private String nodeName;
  private String nameSfb;

  private static CompletableFuture<ModelRenderable> myModel;

  public AugmentedImageNode(Context context,String nodeName) {
    // Upon construction, start loading the models for the corners of the frame.
    if(nodeName.equals("dog.png")){
      nameSfb ="dog/12228_Dog_v1_L2.sfb";
    }
    else if (nodeName.equals("skull3.jpg")){
      nameSfb = "skull/12140_Skull_v3_L2.sfb";
    }else{
      nameSfb="model/frame_lower_left.sfb";
    }

    //Build the renderable corresponding to the current nameSfb
    if (myModel == null) {
      myModel =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse(nameSfb))
                      .build();
    }

  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The corners are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corners.
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setImage(AugmentedImage image,String imgName) {
    this.image = image;
    this.imgName = imgName;

        if(!myModel.isDone()){
          CompletableFuture.allOf(myModel)
                  .thenAccept((Void aVoid) -> setImage(image,imgName))
                  .exceptionally(
                          throwable -> {
                            Log.e(TAG, "Exception loading", throwable);
                            return null;
                          });
        }

      // Set the anchor based on the center of the image.
      setAnchor(image.createAnchor(image.getCenterPose()));

      // Make the node
      Vector3 localPosition = new Vector3();
      Node cornerNode;

      localPosition.set(0.0f, 0.0f, 0.0f);


      cornerNode = new Node();
      cornerNode.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 180f));
      cornerNode.setParent(this);
      cornerNode.setLocalPosition(localPosition);
      cornerNode.setRenderable(myModel.getNow(null));
  }

  public AugmentedImage getImage() {
    return image;
  }
}
