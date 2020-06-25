package com.dating.padosi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelStore
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.yuyakaido.android.cardstackview.CardStackView
import java.io.IOException
import java.lang.Exception


class Main2Activity : AppCompatActivity() {

    val users = ArrayList<User>()
    var mDatabase: FirebaseDatabase? = null
    var mRef: DatabaseReference? = null
    var mReflike: DatabaseReference? = null
    var currentuser:FirebaseUser?=null
    var uid:String?=null
    private val TAG = "FaceTrackerDemo"
    private var mCameraSource: CameraSource? = null

    private var mPreview: CameraSurfacePreview? = null

    public lateinit var cameraOverlay: CameraOverlay

    val RC_HANDLE_GMS = 9001

    val RC_HANDLE_CAMERA_PERM = 2
    var liketemp:Boolean = true
    var number:Int=0

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        mDatabase= FirebaseDatabase.getInstance()
        mRef= mDatabase!!.getReference("users")
        mReflike= mDatabase!!.getReference("data")
        currentuser = FirebaseAuth.getInstance().currentUser
        mPreview = findViewById<View>(R.id.preview) as CameraSurfacePreview

        cameraOverlay = findViewById<View>(R.id.faceOverlay) as CameraOverlay

        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {

            createCameraSource();
        } else {

            requestCameraPermission();
        }



        val prefs: SharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        uid=prefs.getString("uid","defoult")
        likedata()
        showData()

        val b1:FloatingActionButton =findViewById(R.id.b1)
        b1.setOnClickListener {
            userlist()
        }
        val b2:FloatingActionButton=findViewById(R.id.b2)
        b2.setOnClickListener {
            startActivity(Intent(this,FriendList::class.java))
        }
        val btn_edit:FloatingActionButton=findViewById(R.id.btn_edit)
        btn_edit.setOnClickListener {
            startActivity(Intent(this,Profile_Update::class.java))
        }

    }

    fun temp1()
    {
        Log.e("@@t","temp1")
        //like_fuction(this@Main2Activity.applicationContext)
//        val mActivity = MainActivity()
            var preferences= MainAdpter(this@Main2Activity)
            preferences.temp1(this@Main2Activity)
    }

    fun userlist()
    {
        users.clear()
        number++
        val user=User((number).toString(),"password","email","https://koenig-media.raywenderlich.com/uploads/2019/03/NotificationsDeepDive-feature.png","number","uid","fcm")

        mRef!!.push()?.setValue(user)
            .addOnSuccessListener {
                Log.e("@@","setdata")
            }
            ?.addOnCompleteListener {
                Log.e("@@complete","userlist")
            }
    }
    fun showData()
    {
        mRef?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                mutableListOf<User>()
                p0.children.forEach {
                    Log.e("@@key",it.key)
                    Log.e("@@child",it.children.toString())
                    Log.e("@@data",it.getValue().toString())

                    var user=it?.getValue(User::class.java)
                    Log.e("@@data", user?.number.toString())
                    Log.e("@@data", user?.email.toString())
                    Log.e("@@data", user?.image.toString())
                    Log.e("@@data", user?.name.toString())
                    Log.e("@@data", user?.uid.toString())
                    Log.e("@@data", user?.city.toString())
                    Log.e("@@data",p0.key.toString())

                    if (user != null) {
                        users.add(user)
                    }

                }

                val recyclerView = findViewById(R.id.recyclerView) as CardStackView
                var adapter = MainAdpter(this@Main2Activity,users)
                recyclerView?.adapter = adapter









            }
            override fun onCancelled(p0: DatabaseError) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.e("@@","error")
            }
        })

    }
    fun likedata()
    {
        /*val query: Query = mReflike?.child("")?.equalTo(uid)!!
        query?*/
        mReflike!!.addChildEventListener(object: ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                p0.children.forEach {
                    Log.e("@@likedata1",it.getValue().toString())
                    Log.e("@@likedata2",it.hasChild(uid.toString()).toString())
                    Log.e("@@likedata3",it.toString())
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    companion object {
    private const val TAG = "FaceTrackerDemo"
    private const val RC_HANDLE_GMS = 9001
    private const val RC_HANDLE_CAMERA_PERM = 2
    private var instance: Main2Activity? = null
    }

    private inner class GraphicFaceTrackerFactory :
        MultiProcessor.Factory<Face> {
        override fun create(face: Face): GraphicFaceTracker {
            Log.e("@@f","smile "+face.isSmilingProbability.toString())
            Log.e("@@f","right eye"+face.isRightEyeOpenProbability.toString())
            Log.e("@@f", "left eye"+face.isLeftEyeOpenProbability.toString())
            Log.e("@@f","position"+face.position.toString())
            return GraphicFaceTracker(cameraOverlay)
        }
    }

    fun requestCameraPermission() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                RC_HANDLE_CAMERA_PERM
            )
        } else {
            val thisActivity: Activity = this
            val listener =
                View.OnClickListener {
                    ActivityCompat.requestPermissions(
                        thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM
                    )
                }
            Snackbar.make(
                cameraOverlay, "Camera permission is required",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("OK", listener)
                .show()
        }
    }

    fun createCameraSource() {

        val context = applicationContext

        val detector =
            FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build()
        detector.setProcessor(MultiProcessor.Builder(GraphicFaceTrackerFactory()).build())

        if (!detector.isOperational) {
            Log.e(TAG, "Face detector dependencies are not yet available.")
        }

        mCameraSource = CameraSource.Builder(context, detector)
            .setRequestedPreviewSize(640, 480)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(30.0f)
            .build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        mPreview!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCameraSource != null) {
            mCameraSource!!.release()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(
                TAG,
                "Got unexpected permission result: $requestCode"
            )
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(
                TAG,
                "Camera permission granted - initialize the camera source"
            )
            val activity: Activity = this@Main2Activity
            createCameraSource()
            return
        }
        Log.e(
            TAG,
            "Permission not granted: results len = " + grantResults.size +
                    " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)"
        )
        val listener =
            DialogInterface.OnClickListener { dialog, id -> finish() }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("FaceTrackerDemo")
            .setMessage("Need Camera access permission!")
            .setPositiveButton("OK", listener)
            .show()
    }

    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }
        if (mCameraSource != null) {
            try {
                mPreview!!.start(mCameraSource, cameraOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }
        }
    }

    private inner class GraphicFaceTracker internal constructor(private val mOverlay: CameraOverlay) :
        Tracker<Face?>() {
        private val OPEN_THRESHOLD = 0.85f
        private val CLOSE_THRESHOLD = 0.15f

        private var state = 0
        private val faceOverlayGraphics: FaceOverlayGraphics
        override fun onNewItem(
            faceId: Int,
            item: Face?
        ) {
            faceOverlayGraphics.setId(faceId)

        }

        override fun onUpdate(
            detectionResults: Detections<Face?>,face: Face?) {
            Log.e("@@f","smile "+face!!.isSmilingProbability.toString())
            Log.e("@@f","right eye"+face!!.isRightEyeOpenProbability.toString())
            Log.e("@@f", "left eye"+face!!.isLeftEyeOpenProbability.toString())
            Log.e("@@f","position"+face!!.position.toString())
            var  left = face.getIsLeftEyeOpenProbability();
            var right = face.getIsRightEyeOpenProbability();
            var smilefloat:Float = 0.505848523F

            if ((left == Face.UNCOMPUTED_PROBABILITY) ||
                (right == Face.UNCOMPUTED_PROBABILITY)) {
                // At least one of the eyes was not detected.
                Log.e("@@f","At least one of the eyes was not detected")

                return;
            }

            when (2) {
                 0->
                if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
                    // Both eyes are initially open
                    Log.e("@@f1","Both eyes are initially open")
                    state = 1;
                }

                1->
                if ((left < CLOSE_THRESHOLD) && (right < CLOSE_THRESHOLD)) {
                    Log.e("@@f1","Both eyes become closed")
                    state = 2;
                }

                2->
                if ((left < OPEN_THRESHOLD) && (right > CLOSE_THRESHOLD) && (face.isSmilingProbability > smilefloat)) {
                    Log.e("@@fl", "Blink")
//                    startActivity(Intent(this@Main2Activity,MainActivity::class.java))
//                    userlist()
                    temp1()
                }
                else
                {
                    Log.e("@@f1","else part")
                }
            }

            mOverlay.add(faceOverlayGraphics)
            faceOverlayGraphics.updateFace(face)
        }

        override fun onMissing(detectionResults: Detections<Face?>) {
            mOverlay.remove(faceOverlayGraphics)
        }

        override fun onDone() {
            mOverlay.remove(faceOverlayGraphics)
        }

        init {
            faceOverlayGraphics = FaceOverlayGraphics(mOverlay)
        }
    }
}