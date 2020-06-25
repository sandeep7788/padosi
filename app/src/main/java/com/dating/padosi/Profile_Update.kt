package com.dating.padosi

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.hbb20.CountryCodePicker
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile__update.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Profile_Update : AppCompatActivity() {

    lateinit var b1:Button
    lateinit var e1:EditText
    lateinit var e2:EditText
    lateinit var e3:EditText
    var profile_image:CircleImageView?=null
    lateinit var image_edit:ImageView
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var mfirebasedatabase: FirebaseDatabase
    lateinit var mRef: DatabaseReference
    var  uid:String=""
    var country:String?=null
    var gendertext:String="male"
    var cp:CountryCodePicker?=null
    var imagestring:String?=null
    lateinit var e_city:EditText
    var email=""
    lateinit var e_calender:CalendarView
    lateinit var i_name:ImageView
    lateinit var i_about:ImageView
    lateinit var i_number:ImageView
    lateinit var i_state:ImageView
    lateinit var i_city:ImageView
    lateinit var i_date:ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile__update)
        b1=findViewById(R.id.btn_done)
        e1=findViewById(R.id.e_name)
        e2=findViewById(R.id.e_about)
        e3=findViewById(R.id.e_state)
        profile_image=findViewById(R.id.imageview)
        image_edit=findViewById(R.id.p_edit)
        cp =findViewById(R.id.cp)
        e_calender=findViewById(R.id.e_calender)
        e_city=findViewById(R.id.e_city)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference= firebaseStore!!.getReference()
        mfirebasedatabase= FirebaseDatabase.getInstance()
        mRef=mfirebasedatabase.getReference("users")
        i_name=findViewById(R.id.i_name)
        i_about=findViewById(R.id.i_about)
        i_number=findViewById(R.id.i_number)
        i_state=findViewById(R.id.i_state)
        i_city=findViewById(R.id.i_city)
        i_date=findViewById(R.id.i_date)
        e1.isFocusable=false


        i_name.setOnClickListener {
            e1.isFocusable=true
            e1.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(e1, InputMethodManager.SHOW_IMPLICIT)
            Log.e("@@i_name","edittext")
        }

        i_about.setOnClickListener {
            e2.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(e2, InputMethodManager.SHOW_IMPLICIT)
            Log.e("@@i_name","edittext")
        }
        i_number.setOnClickListener {
            Toast.makeText(this,"click on Country name",Toast.LENGTH_LONG).show()
        }
        i_state.setOnClickListener {
            e3.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(e3, InputMethodManager.SHOW_IMPLICIT)
            Log.e("@@i_name","edittext")
        }
        i_city.setOnClickListener {
            e_city.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(e_city, InputMethodManager.SHOW_IMPLICIT)
            Log.e("@@i_name","edittext")
        }




        var textView=findViewById<TextView>(R.id.t_date)
        textView.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            textView.text = sdf.format(cal.time)

        }

        i_date.setOnClickListener {
            DatePickerDialog(this@Profile_Update, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }




        image_edit.setOnClickListener {
            launchGallery()
        }
        var rg:RadioGroup=findViewById(R.id.rg)

        rg.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { rg, checkedId ->
                val btn_gender: RadioButton = findViewById(checkedId)
                Toast.makeText(applicationContext," On checked change :"+
                        " ${btn_gender.text}",
                    Toast.LENGTH_SHORT).show()
            })

        b1.setOnClickListener {
            Log.e("@@p","b1")
            if (e1!!.text.isEmpty())
            {
                e1!!.setError("enter value")
            }
                else if (e2!!.text.isEmpty())
            {
                e2!!.setError("enter value")
            }
                    else if (e3!!.text.isEmpty())
            {
                e3!!.setError("enter value")
            }
                        else if (e1!!.text.isEmpty())
            {
                e1!!.setError("enter value")
            }
            else
            {
                uploadImage()
            }
        }
        setdata()
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.e("@@p",task.exception.toString()+" "+task.result)
                        throw it

//                            Log.e("@@p",task.toString()+" "+it.toString())
                    }
                }

                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updatedata(task.getResult().toString())

                } else {
                    // Handle failures
                }

                Log.e("@@p",uploadTask.exception.toString()+uploadTask.isCanceled+uploadTask.isComplete+uploadTask.isInProgress)
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
            val prefs: SharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
            uid= prefs.getString("uid","defoultuid")!!
            imagestring=prefs.getString(DataClass.image,"defoult")
            email= prefs.getString(DataClass.email,"defoult")!!
            updatedata(imagestring.toString())
        }
    }
    fun updatedata(url:String)
    {
        cp!!.setOnCountryChangeListener {
            country= cp!!.selectedCountryName
        }
        Log.e("@@p",url)
        var user=User(e1!!.text.toString(),"passwod",email,url.toString(),"123456789",uid, e2!!.text.toString(),gendertext.toString(),e_city.text?.toString(), e3!!.text.toString(),
            cp!!.selectedCountryName.toString(),
            FirebaseInstanceId.getInstance().getToken().toString(),"dob")
        mRef.child(uid.toString()).setValue(user).addOnSuccessListener {
            Toast.makeText(applicationContext,"updated data",Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Profile_Update,Main2Activity::class.java))
        }.addOnCanceledListener {
            Toast.makeText(applicationContext,"error",Toast.LENGTH_LONG).show()
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
    fun setdata()
    {

        val prefs: SharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        mRef.child(prefs.getString("uid","defoult").toString()).addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                    var user=p0.getValue(User::class.java)
                    Log.e("@@data", user?.number.toString())
                    Log.e("@@data", user?.email.toString())
                    Log.e("@@data", user?.image.toString())
                    Log.e("@@data", user?.name.toString())
                    Log.e("@@data", user?.uid.toString())
                    Log.e("@@data", user?.gender.toString())
                    Log.e("@@data",p0.key.toString())
                    e1?.setText(user?.name)
                    e2?.setText(user?.about)
                    e3?.setText(user?.state)
                    e_city.setText(user?.city)

                    if(user?.gender=="female")
                    {
                        rg.check(R.id.btn_female)
                    }
                    if(user?.gender=="male")
                    {
                        rg.check(R.id.btn_male)
                    }

                    Picasso.with(this@Profile_Update).load(user?.image).error(R.drawable.myaccount)
                        .into(imageview)

                val prefs: SharedPreferences.Editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                prefs.putString("uid",user?.uid)
                prefs.putString(DataClass.image,user?.image)
                prefs.apply()


            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("@@",p0.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                profile_image?.setImageBitmap(bitmap)
                Log.e("@@p",bitmap.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
