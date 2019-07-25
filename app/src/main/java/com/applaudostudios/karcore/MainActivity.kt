package com.applaudostudios.karcore

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //lateinit for Augmented Reality Fragment
    private lateinit var arFragment: ArFragment
    //lateinit for the model uri
    private lateinit var selectedObject: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init Fragment
        arFragment = supportFragmentManager.findFragmentById(sceneform_fragment_view.id) as ArFragment

        //default model
        selectedObject = Uri.parse("model.sfb")

        //tab listener for the ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            //if surface is not horizontal and upward facing
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                //return for the callback
                return@setOnTapArPlaneListener
            }
            //create a new anchor
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor, selectedObject)
        }

        //Click listener for lamp and table objects
        smallTable.setOnClickListener {
            setModelPath("model.sfb")
        }
        bigLamp.setOnClickListener {
            setModelPath("LampPost.sfb")
        }

    }

    /***
     * function to handle the renderable object and place objecto in scene
     */
    private fun placeObject(fragment: ArFragment, anchor: Anchor, modelUri: Uri) {
        val modelRenderable = ModelRenderable.builder()
            .setSource((fragment.requireContext()), modelUri)
            .build()
        //when the model render is builded add node to scene
        modelRenderable.thenAccept { renderableObject -> addNodeToScene(fragment, anchor, renderableObject) }
        //handle error
        modelRenderable.exceptionally {
            val toast = Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
            toast.show()
            null
        }
    }

    /***
     * Function to a child anchor to a new scene.
     */
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderableObject: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderableObject
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    /***
     * function to get the model resource on assets directory for each figure.
     */
    private fun setModelPath(modelFileName: String) {
        selectedObject = Uri.parse(modelFileName)
        val toast = Toast.makeText(applicationContext, "bigLamp", Toast.LENGTH_SHORT)
        toast.show()
    }
}
