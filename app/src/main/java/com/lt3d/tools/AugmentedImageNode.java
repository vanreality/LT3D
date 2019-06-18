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
  private String imgName;
  private String nodeName;
  private List<String> nodeNames=new ArrayList<>();
  private String nameSfb;

  // Models of the 4 corners.  We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
//  private static CompletableFuture<ModelRenderable> ulCorner;
//  private static CompletableFuture<ModelRenderable> urCorner;
//  private static CompletableFuture<ModelRenderable> lrCorner;
//  private static CompletableFuture<ModelRenderable> llCorner;

  private static List<CompletableFuture<ModelRenderable>> myModels=new ArrayList<>();
  private static CompletableFuture<ModelRenderable> myModel;

  public AugmentedImageNode(Context context,String nodeName) {
    // Upon construction, start loading the models for the corners of the frame.

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

    for(int i=0;i<nodeNames.size();i++){
      if(nodeNames.get(i).equals(nodeName)){
        myModel=myModels.get(i);
      }
    }



//    if(nodeName.equals("dog.png")){
//      nameSfb ="model/frame_lower_left.sfb";
//    }
//
//    else if (nodeName.equals("skull3.jpg")){
//      nameSfb = "skull/12140_Skull_v3_L2.sfb";
//    }
//
//    else if(nodeName.equals("venus.png")){
//      nameSfb = "venus/12328_Statue_v1_L2.sfb";
//    }
//
//    else if(nodeName.equals("egypt_lion.png")){
//      nameSfb = "egypt/10085_egypt_sphinx_iterations-2.sfb";
//    }
//
//    else if(nodeName.equals("deer.png")){
//      nameSfb = "deer/12961_White-Tailed_Deer_v1_l2.sfb";
//    }
//
//    else if(nodeName.equals("ironman.png")){
//      nameSfb = "ironman/IronMan.sfb";
//    }
//
//    else{
//      nameSfb="model/frame_lower_left.sfb";
//    }
//
//      if(myModel == null){
//        myModel =
//                ModelRenderable.builder()
//                        .setSource(context, Uri.parse(nameSfb))
//                        .build();
//
//      }



//    if(nodeName == "earth.jpg"){
//      if (ulCorner == null) {
//        ulCorner =
//                ModelRenderable.builder()
//                        .setSource(context, Uri.parse("model/frame_upper_left.sfb"))
//                        .build();
//        urCorner =
//                ModelRenderable.builder()
//                        .setSource(context, Uri.parse("model/frame_upper_right.sfb"))
//                        .build();
//        llCorner =
//                ModelRenderable.builder()
//                        .setSource(context, Uri.parse("model/frame_lower_left.sfb"))
//                        .build();
//        lrCorner =
//                ModelRenderable.builder()
//                        .setSource(context, Uri.parse("model/frame_lower_right.sfb"))
//                        .build();
//      }
//    }

//    else if(nodeName == "dog.png"){
//      if(dog == null){
//        dog =
//                ModelRenderable.builder()
//                        .setSource(context, Uri.parse("dog/12228_Dog_v1_L2.sfb"))
//                        .build();
//      }
//    }
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

      // Make the 4 corner nodes.
      Vector3 localPosition = new Vector3();
      Node cornerNode;



    localPosition.set(0.0f, 0.0f, 0.0f);
      cornerNode = new Node();
      cornerNode.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 270f));
      cornerNode.setParent(this);
      cornerNode.setLocalPosition(localPosition);
      cornerNode.setRenderable(myModel.getNow(null));



//    else if (this.imgName == "earth.jpg") {
////      // If any of the models are not loaded, then recurse when all are loaded.
////      if (!ulCorner.isDone() || !urCorner.isDone() || !llCorner.isDone() || !lrCorner.isDone()) {
////        CompletableFuture.allOf(ulCorner, urCorner, llCorner, lrCorner)
////                .thenAccept((Void aVoid) -> setImage(image,"earth.jpg"))
////                .exceptionally(
////                        throwable -> {
////                          Log.e(TAG, "Exception loading", throwable);
////                          return null;
////                        });
////      }
////
////      // Set the anchor based on the center of the image.
////      setAnchor(image.createAnchor(image.getCenterPose()));
////
////      // Make the 4 corner nodes.
////      Vector3 localPosition = new Vector3();
////      Node cornerNode;
////
////      // Upper left corner.
////      localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
////      cornerNode = new Node();
////      cornerNode.setParent(this);
////      cornerNode.setLocalPosition(localPosition);
////      cornerNode.setRenderable(ulCorner.getNow(null));
////
////      // Upper right corner.
////      localPosition.set(0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
////      cornerNode = new Node();
////      cornerNode.setParent(this);
////      cornerNode.setLocalPosition(localPosition);
////      cornerNode.setRenderable(urCorner.getNow(null));
////
////      // Lower right corner.
////      localPosition.set(0.5f * image.getExtentX(), 0.0f, 0.5f * image.getExtentZ());
////      cornerNode = new Node();
////      cornerNode.setParent(this);
////      cornerNode.setLocalPosition(localPosition);
////      cornerNode.setRenderable(lrCorner.getNow(null));
////
////      // Lower left corner.
////      localPosition.set(-0.5f * image.getExtentX(), 0.0f, 0.5f * image.getExtentZ());
////      cornerNode = new Node();
////      cornerNode.setParent(this);
////      cornerNode.setLocalPosition(localPosition);
////      cornerNode.setRenderable(llCorner.getNow(null));
////    }
  }

  public AugmentedImage getImage() {
    return image;
  }
}
