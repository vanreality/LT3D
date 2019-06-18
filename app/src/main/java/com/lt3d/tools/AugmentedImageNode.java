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

import java.util.ArrayList;
import java.util.List;
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


  private static List<CompletableFuture<ModelRenderable>> myModels=new ArrayList<>();
  private static CompletableFuture<ModelRenderable> myModel;

  public AugmentedImageNode(Context context,String nodeName) {
    // Upon construction, start loading the models for the corners of the frame.

    List<String> nodeNames = new ArrayList<>();
    nodeNames.add("dog.png");
    myModel =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("dog/12228_Dog_v1_L2.sfb"))
                    .build();
    myModels.add(myModel);
    nodeNames.add("skull3.jpg");
    myModel =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("skull/12140_Skull_v3_L2.sfb"))
                    .build();
    myModels.add(myModel);
    nodeNames.add("venus.png");
    myModel =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("venus/12328_Statue_v1_L2.sfb"))
                    .build();
    myModels.add(myModel);
    nodeNames.add("egypt_lion.png");
    myModel =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("egypt/10085_egypt_sphinx_iterations-2.sfb"))
                    .build();
    myModels.add(myModel);
    nodeNames.add("deer.png");
    myModel =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("deer/12961_White-Tailed_Deer_v1_l2.sfb"))
                    .build();
    myModels.add(myModel);

    nodeNames.add("ironman.png");
    myModel =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("ironman/IronMan.sfb"))
                    .build();
    myModels.add(myModel);

    for(int i = 0; i< nodeNames.size(); i++){
      if(nodeNames.get(i).equals(nodeName)){
        myModel=myModels.get(i);
      }
    }
  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The corners are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corners.
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setImage(AugmentedImage image) {
    this.image = image;

    if(!myModel.isDone()){
          CompletableFuture.allOf(myModel)
                  .thenAccept((Void aVoid) -> setImage(image))
                  .exceptionally(
                          throwable -> {
                            Log.e(TAG, "Exception loading", throwable);
                            return null;
                          });
        }

      // Set the anchor based on the center of the image.
      setAnchor(image.createAnchor(image.getCenterPose()));

      // Make the 4 corner nodes.
      Vector3 localPosition = new Vector3();
      Node cornerNode;



    localPosition.set(0.0f, 0.0f, 0.0f);
      cornerNode = new Node();
      cornerNode.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 270f));
      cornerNode.setParent(this);
      cornerNode.setLocalPosition(localPosition);
      cornerNode.setRenderable(myModel.getNow(null));
  }

  public AugmentedImage getImage() {
    return image;
  }
}
