package com.dating.padosi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN: Int = 1
    val FB_SIng_In: Int = 2
    var TAG="@@"
    var storedVerificationId:String?=null
    public var ccp: CountryCodePicker?=null
    public var countryCode:String?=null
    public var countryName:String?=null
    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var b1:Button
    lateinit var b2:ImageView
    lateinit var facebook_button:ImageView
    lateinit var mfirebasedatabase:FirebaseDatabase
    lateinit var mRef:DatabaseReference
    public var callbackManager: CallbackManager? = null
    public var resendToken:String?=null
    lateinit var buttonFacebookLogin:LoginButton
    var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    lateinit var e1:EditText
    lateinit var e2:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        b1 = findViewById<Button>(R.id.b1)
        b2 = findViewById<ImageView>(R.id.img1)
        facebook_button = findViewById(R.id.img2)
        e1 = findViewById<EditText>(R.id.e1)
        e2 = findViewById<EditText>(R.id.e2)
        buttonFacebookLogin = findViewById(R.id.login_button)
        auth = FirebaseAuth.getInstance()
        mfirebasedatabase = FirebaseDatabase.getInstance()
        mRef = mfirebasedatabase.getReference("users")
        val l1: LinearLayout = findViewById(R.id.l1)
        val l_otp: LinearLayout = findViewById(R.id.l_otp)
        val p_otp: EditText = findViewById(R.id.p_otp)
        val linearLayout7: RelativeLayout = findViewById(R.id.linearLayout7)

        ccp = findViewById(R.id.country_code_picker)


        //to set default country code as Japan

        ccp!!.setDefaultCountryUsingNameCode("JP")

        ccp!!.setOnCountryChangeListener {


            countryCode=ccp!!.selectedCountryCode
            countryName=ccp!!.selectedCountryName

            Toast.makeText(this,"Country Code "+countryCode,Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"Country Name "+countryName,Toast.LENGTH_SHORT).show()
        }





        var registert = findViewById<TextView>(R.id.registert)
        registert.setOnClickListener {
            linearLayout7.visibility = View.GONE
            l1.visibility = View.VISIBLE
            l_otp.visibility = View.VISIBLE
        }

        val btn_next: Button = findViewById(R.id.btn_next)
        btn_next.setOnClickListener {
            sent_otp()
            Toast.makeText(baseContext, "wait", Toast.LENGTH_SHORT).show()
        }

        val btn_otp: Button = findViewById(R.id.btn_otp)
        btn_otp.setOnClickListener {
            verifyPhoneNumberWithCode(storedVerificationId, p_otp.text.toString())
            Log.e("@@phonev", p_otp.text.toString())
        }









        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        b1.setOnClickListener {
//            login()
            startActivity(Intent(this@MainActivity,Main2Activity::class.java))
        }



        b2.setOnClickListener{
            signIn()
        }
        facebook_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(
                this@MainActivity, "try later",
                Toast.LENGTH_LONG
            ).show()
        }

        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        })

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                Log.e("@@",account.toString())
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
                // ...
            }
        }
        else if (requestCode==FB_SIng_In)
        {
            callbackManager?.onActivityResult(requestCode, resultCode, data)

        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        // [START_EXCLUDE silent]
//        showProgressBar()
        // [END_EXCLUDE]

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // [START_EXCLUDE]
//                hideProgressBar()
                // [END_EXCLUDE]
            }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth!!.currentUser
//        updateUI(currentUser)
//        val user: FirebaseUser = auth.getCurrentUser()!!


    }



    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success"+task.result)

//                    var user=User(User::class.java)

                    val user: FirebaseUser = auth.getCurrentUser()!!

                    Log.e("@@user",user.displayName)
                    Log.e("@@photo", user.displayName+user.photoUrl.toString()+user.email+user.uid+user.phoneNumber)

                    val prefs: SharedPreferences.Editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                    prefs.putString("uid",user.uid)
                    prefs.putString(DataClass.image,user.photoUrl.toString())
                    prefs.putString(DataClass.email,user.email)
                    prefs.apply()

                    val user1=User(user.displayName,"password",user.email,user.photoUrl.toString(),user.phoneNumber,user.uid,FirebaseInstanceId.getInstance().getToken().toString())
                    mRef.child(user.uid).setValue(user1)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@MainActivity, "Inserted data succesfully",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this,Profile_Update::class.java))
                        }


                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.e("@@user",user.displayName)
            Log.e("@@photo", user.displayName+user.photoUrl.toString()+user.email+user.uid+user.phoneNumber)

            val user1=User(user.displayName,FirebaseInstanceId.getInstance().getToken().toString(),user.photoUrl.toString(),user.email,user.uid,user.phoneNumber,FirebaseInstanceId.getInstance().getToken().toString())
            mRef.child(user.uid).setValue(user1)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@MainActivity, "Inserted data succesfully",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            /*status.setText(R.string.signed_out)
            detail.text = null

            buttonFacebookLogin.visibility = View.VISIBLE
            buttonFacebookSignout.visibility = View.GONE*/
        }
    }
    fun sent_otp()
    {

            val e_number:EditText=findViewById(R.id.e_number)
            callbacks?.let { it1 ->
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91"+e_number.text.toString(), // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    it1
                )
                Log.e("@@p","sent_otp")

        }
        callbacks= object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {



                override fun onVerificationCompleted(credential: PhoneAuthCredential) {


                    signInWithPhoneAuthCredential(credential)
                    Log.e("@@phonec","code"+credential.smsCode.toString())

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.




                    // Show a message and update the UI
                    // ...
                    Log.e("@@p",e.message)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.e("@@phone",verificationId.toString()+token.toString())

                    // Save verification ID and resending token so we can use them later
                    storedVerificationId = verificationId
                    val l2:LinearLayout=findViewById(R.id.l2)
                    val l1:LinearLayout=findViewById(R.id.l1)
                    l1.visibility=View.GONE
                    l2.visibility=View.VISIBLE



                    // ...
                }
        }
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

//                    val user = task.result?.user
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
    fun login()
    {
        if (e1.text.toString().isEmpty())
        {
            e1.setError("Enter Email id")
        }
        else if(e2.text.toString().isEmpty())
        {
            e2.setError("Enter Password")
        }
        else if(!e1.text.toString().isEmpty()&&!e2.text.toString().isEmpty())
        {
            auth.signInWithEmailAndPassword(e1.text.toString().trim(),e2.text.toString().trim()).addOnCompleteListener(this, OnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Profile_Update::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    Toast.makeText(this, "Registration Failed"+ task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
                Log.e("@@expecttion", task.exception?.message.toString())
            })
        }
    }
}
