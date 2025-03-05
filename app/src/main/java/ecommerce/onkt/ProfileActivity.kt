package ecommerce.onkt

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {

    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var contact: EditText
    lateinit var password: EditText
    lateinit var retypePassword: EditText
    lateinit var gender: RadioGroup
    lateinit var male: RadioButton
    lateinit var female: RadioButton
    lateinit var submit: Button
    lateinit var editProfile: Button
    lateinit var logout: Button
    lateinit var delete: Button

    var emailPattern: String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var db: SQLiteDatabase
    lateinit var sGender: String
    lateinit var sp: SharedPreferences
    lateinit var apiInterface: ApiInterface
    lateinit var pd: ProgressDialog

    lateinit var profileIv: CircleImageView
    lateinit var cameraIv: CircleImageView

    var appPermission: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var appPermission33: Array<String> = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.CAMERA
    )

    val PERMISSION_REQUEST_CODE: Int = 1240
    val REQUEST_CODE_CHOOSE: Int = 123

    lateinit var mSelected: List<Uri>
    var sSelectedPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE)

        db = openOrCreateDatabase("EcomApp.db", MODE_PRIVATE, null)
        val tableQuery =
            "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,FIRSTNAME VARCHAR(50),LASTNAME VARCHAR(50),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(10))"
        db.execSQL(tableQuery)

        firstName = findViewById(R.id.profile_firstname)
        lastName = findViewById(R.id.profile_lastname)
        email = findViewById(R.id.profile_email)
        contact = findViewById(R.id.profile_contact)
        password = findViewById(R.id.profile_password)
        retypePassword = findViewById(R.id.profile_retype_password)

        male = findViewById(R.id.profile_male)
        female = findViewById(R.id.profile_female)

        gender = findViewById(R.id.profile_gender)
        submit = findViewById(R.id.profile_submit)
        editProfile = findViewById(R.id.profile_edit)
        logout = findViewById(R.id.profile_logout)

        cameraIv = findViewById(R.id.profile_camera)
        profileIv = findViewById(R.id.profile_image)

        cameraIv.setOnClickListener {
            if (checkAndRequestPermission()) {
                selectImageData()
            }
        }

        delete = findViewById(R.id.profile_delete)

        delete.setOnClickListener {
            val builder = AlertDialog.Builder(this@ProfileActivity)
            builder.setTitle("Account Delete!")
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setMessage("Are You Sure Want to Delete Your Account!")

            builder.setPositiveButton(
                "No"
            ) { dialogInterface, i -> dialogInterface.dismiss() }

            builder.setNegativeButton(
                "Yes"
            ) { dialogInterface, i ->
                //doSqliteDelete();
                if (ConnectionDetector(this@ProfileActivity).networkConnected()) {
                    //new doDelete().execute();
                    pd = ProgressDialog(this@ProfileActivity)
                    pd.setMessage("Please Wait...")
                    pd.setCancelable(false)
                    pd.show()
                    doDeleteRetrofit()
                } else {
                    ConnectionDetector(this@ProfileActivity).networkDisconnected()
                }
            }
            builder.show()
        }

        logout.setOnClickListener {
            val builder = AlertDialog.Builder(this@ProfileActivity)
            builder.setTitle("Logout!")
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setMessage("Are You Sure Want to Logout!")

            builder.setPositiveButton(
                "No"
            ) { dialogInterface, i -> dialogInterface.dismiss() }

            builder.setNegativeButton(
                "Yes"
            ) { dialogInterface, i ->
                sp.edit().clear().commit()
                val intent = Intent(
                    this@ProfileActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }

            builder.setNeutralButton(
                "Rate Us"
            ) { dialogInterface, i ->
                Toast.makeText(this@ProfileActivity, "Rate Us", Toast.LENGTH_SHORT)
                    .show()
                dialogInterface.dismiss()
            }
            builder.show()
        }

        gender.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = findViewById<RadioButton>(i)
            sGender = radioButton.text.toString()
        }

        submit.setOnClickListener {
            if (firstName.text.toString().trim { it <= ' ' } == "") {
                firstName.error = "First Name Required"
            } else if (lastName.text.toString().trim { it <= ' ' } == "") {
                lastName.error = "Last Name Required"
            } else if (email.text.toString().trim { it <= ' ' } == "") {
                email.error = "Email Id Required"
            } else if (!email.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                email.error = "Valid Email Id Required"
            } else if (contact.text.toString().trim { it <= ' ' } == "") {
                contact.error = "Contact No. Required"
            } else if (contact.text.toString().trim { it <= ' ' }.length < 10) {
                contact.error = "Valid Contact No. Required"
            } else if (password.text.toString().trim { it <= ' ' } == "") {
                password.error = "Password Required"
            } else if (password.text.toString().trim { it <= ' ' }.length < 6) {
                password.error = "Min. 6 Char Password Required"
            } else if (retypePassword.text.toString().trim { it <= ' ' } == "") {
                retypePassword.error = "Retype Password Required"
            } else if (retypePassword.text.toString().trim { it <= ' ' }.length < 6) {
                retypePassword.error = "Min. 6 Char Retype Password Required"
            } else if (!password.text.toString().trim { it <= ' ' }
                    .matches(retypePassword.text.toString().trim { it <= ' ' }.toRegex())) {
                retypePassword.error = "Password Does Not Match"
            } else if (gender.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Please Select Gender",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //doSqliteUpdate();
                if (ConnectionDetector(this@ProfileActivity).networkConnected()) {
                    //new doUpdate().execute();
                    pd = ProgressDialog(this@ProfileActivity)
                    pd.setMessage("Please Wait...")
                    pd.setCancelable(false)
                    pd.show()
                    if (sSelectedPath.trim { it <= ' ' }.equals("", ignoreCase = true)) {
                        doUpdateRetrofit()
                    } else {
                        doUpdateImageRetrofit()
                    }
                } else {
                    ConnectionDetector(this@ProfileActivity).networkDisconnected()
                }
            }
        }

        editProfile.setOnClickListener { setData(true) }

        setData(false)

    }

    fun checkAndRequestPermission(): Boolean {
        val listPermission: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (perm in appPermission33) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        perm
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    listPermission.add(perm)
                }
            }
            if (!listPermission.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermission.toTypedArray<String>(),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                return true
            }
        } else {
            for (perm in appPermission) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        perm
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    listPermission.add(perm)
                }
            }
            if (!listPermission.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermission.toTypedArray<String>(),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                return true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val permissionResult = HashMap<String, Int>()
            var deniedCount = 0
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult[permissions[i]] = grantResults[i]
                    deniedCount++
                }
            }
            if (deniedCount == 0) {
                selectImageData()
            } else {
                for ((permName, permResult) in permissionResult) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@ProfileActivity,
                            permName
                        )
                    ) {
                        /*showDialogPermission("", "This App needs Read External Storage And Location permissions to work whithout and problems.",*/
                        showDialogPermission(
                            "",
                            "This App needs Read External Storage And Camera permissions to work whithout and problems.",
                            "Yes, Grant permissions",
                            { dialogInterface, i ->
                                dialogInterface.dismiss()
                                checkAndRequestPermission()
                            },
                            "No",
                            { dialogInterface, i ->
                                dialogInterface.dismiss()
                                //finishAffinity();
                            },
                            false
                        )
                    } else {
                        showDialogPermission(
                            "",
                            "You have denied some permissions. Allow all permissions at [Setting] > [Permissions]",
                            "Go to Settings",
                            { dialogInterface, i ->
                                dialogInterface.dismiss()
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            },
                            "No",
                            { dialogInterface, i ->
                                dialogInterface.dismiss()
                                //finish();
                            },
                            false
                        )
                        break
                    }
                }
            }
        }
    }

    fun showDialogPermission(
        title: String?,
        msg: String?,
        positiveLable: String?,
        positiveOnClickListener: DialogInterface.OnClickListener?,
        negativeLable: String?,
        negativeOnClickListener: DialogInterface.OnClickListener?,
        isCancelable: Boolean
    ): AlertDialog {
        val builder = AlertDialog.Builder(this@ProfileActivity)
        builder.setTitle(title)
        builder.setCancelable(isCancelable)
        builder.setMessage(msg)
        builder.setPositiveButton(positiveLable, positiveOnClickListener)
        builder.setNegativeButton(negativeLable, negativeOnClickListener)
        val alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }

    private fun selectImageData() {
        FishBun.with(this@ProfileActivity)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(1)
            .isStartInAllView(false)
            .setIsUseDetailView(false)
            .setReachLimitAutomaticClose(true)
            .setSelectCircleStrokeColor(android.R.color.transparent)
            .setActionBarColor(Color.parseColor("#F44336"), Color.parseColor("#F44336"), false)
            .setActionBarTitleColor(Color.parseColor("#ffffff"))
            .startAlbumWithOnActivityResult(REQUEST_CODE_CHOOSE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //mSelected = Matisse.obtainResult(data);
            mSelected = data!!.getParcelableArrayListExtra(FishBun.INTENT_PATH)!!!!
            Log.d("RESPONSE_IMAGE_URI", mSelected[0].toString())
            sSelectedPath = getImage(mSelected[0])
            Log.d("RESPONSE_IMAGE_ORIGINAL_PATH", sSelectedPath)
            profileIv.setImageURI(mSelected[0])
        }
    }

    private fun getImage(uri: Uri?): String {
        if (uri != null) {
            var path: String? = null
            val s_array = arrayOf(MediaStore.Images.Media.DATA)
            val c = managedQuery(uri, s_array, null, null, null)
            val id = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (c.moveToFirst()) {
                do {
                    path = c.getString(id)
                } while (c.moveToNext())
                //c.close();
                if (path != null) {
                    return path
                }
            }
        }
        return ""
    }

    private fun setData(b: Boolean) {
        if (b) {
            retypePassword.visibility = View.VISIBLE
            editProfile.visibility = View.GONE
            submit.visibility = View.VISIBLE
            cameraIv.visibility = View.VISIBLE
        } else {
            retypePassword.visibility = View.GONE
            editProfile.visibility = View.VISIBLE
            submit.visibility = View.GONE
            cameraIv.visibility = View.GONE
        }

        firstName.isEnabled = b
        lastName.isEnabled = b
        email.isEnabled = b
        contact.isEnabled = b
        password.isEnabled = b
        retypePassword.isEnabled = b
        male.isEnabled = b
        female.isEnabled = b

        firstName.setText(sp.getString(ConstantSp.FIRSTNAME, ""))
        lastName.setText(sp.getString(ConstantSp.LASTNAME, ""))
        email.setText(sp.getString(ConstantSp.EMAIL, ""))
        contact.setText(sp.getString(ConstantSp.CONTACT, ""))
        password.setText(sp.getString(ConstantSp.PASSWORD, ""))
        retypePassword.setText(sp.getString(ConstantSp.PASSWORD, ""))

        sGender = sp.getString(ConstantSp.GENDER, "")!!
        if (sGender.equals("Male", ignoreCase = true)) {
            male.isChecked = true
        } else if (sGender.equals("Female", ignoreCase = true)) {
            female.isChecked = true
        } else {
        }

        if (sp.getString(ConstantSp.PROFILE, "").equals("", ignoreCase = true)) {
            profileIv.setImageResource(R.drawable.ecom_icon)
        } else {
            Glide.with(this@ProfileActivity).load(sp.getString(ConstantSp.PROFILE, ""))
                .placeholder(R.drawable.ecom_icon).into(profileIv)
        }
    }

    private fun doDeleteRetrofit() {
        val call = apiInterface.deleteProfileData(sp.getString(ConstantSp.USERID, ""))
        call!!.enqueue(object : Callback<GetSignupData> {
            override fun onResponse(call: Call<GetSignupData>, response: Response<GetSignupData>) {
                pd.dismiss()
                if (response.code() == 200) {
                    if (response.body()!!.status!!) {
                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        sp.edit().clear().commit()
                        val intent = Intent(
                            this@ProfileActivity,
                            MainActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        ConstantSp.SERVER_ERROR + response.code(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GetSignupData>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(this@ProfileActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doUpdateRetrofit() {
        val call = apiInterface.updateProfileData(
            firstName.text.toString(),
            lastName.text.toString(),
            email.text.toString(),
            contact.text.toString(),
            password.text.toString(),
            sGender,
            sp.getString(ConstantSp.USERID, "")
        )
        call!!.enqueue(object : Callback<GetSignupData> {
            override fun onResponse(call: Call<GetSignupData>, response: Response<GetSignupData>) {
                pd.dismiss()
                if (response.code() == 200) {
                    if (response.body()!!.status!!) {
                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        sp.edit().putString(ConstantSp.FIRSTNAME, firstName.text.toString())
                            .commit()
                        sp.edit().putString(ConstantSp.LASTNAME, lastName.text.toString()).commit()
                        sp.edit().putString(ConstantSp.EMAIL, email.text.toString()).commit()
                        sp.edit().putString(ConstantSp.CONTACT, contact.text.toString()).commit()
                        sp.edit().putString(ConstantSp.PASSWORD, password.text.toString()).commit()
                        sp.edit().putString(ConstantSp.GENDER, sGender).commit()

                        setData(false)
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        ConstantSp.SERVER_ERROR + response.code(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GetSignupData>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(this@ProfileActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doUpdateImageRetrofit() {
        val userPart = RequestBody.create(MultipartBody.FORM, sp.getString(ConstantSp.USERID, ""))
        val firstnamePart = RequestBody.create(MultipartBody.FORM, firstName.text.toString())
        val lastnamePart = RequestBody.create(MultipartBody.FORM, lastName.text.toString())
        val emailPart = RequestBody.create(MultipartBody.FORM, email.text.toString())
        val contactPart = RequestBody.create(MultipartBody.FORM, contact.text.toString())
        val passwordPart = RequestBody.create(MultipartBody.FORM, password.text.toString())
        val genderPart = RequestBody.create(MultipartBody.FORM, sGender)

        val file = File(sSelectedPath)
        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            RequestBody.create(MediaType.parse("image/*"), file)
        )

        val call = apiInterface.updateProfileImageData(
            firstnamePart,
            lastnamePart,
            emailPart,
            contactPart,
            passwordPart,
            genderPart,
            userPart,
            filePart
        )
        call!!.enqueue(object : Callback<UpdateProfileImageData> {
            override fun onResponse(
                call: Call<UpdateProfileImageData>,
                response: Response<UpdateProfileImageData>
            ) {
                pd.dismiss()
                if (response.code() == 200) {
                    if (response.body()!!.status!!) {
                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        sp.edit().putString(ConstantSp.FIRSTNAME, firstName.text.toString())
                            .commit()
                        sp.edit().putString(ConstantSp.LASTNAME, lastName.text.toString()).commit()
                        sp.edit().putString(ConstantSp.EMAIL, email.text.toString()).commit()
                        sp.edit().putString(ConstantSp.CONTACT, contact.text.toString()).commit()
                        sp.edit().putString(ConstantSp.PASSWORD, password.text.toString()).commit()
                        sp.edit().putString(ConstantSp.GENDER, sGender).commit()
                        sp.edit().putString(ConstantSp.PROFILE, response.body()!!.profile).commit()

                        setData(false)
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        ConstantSp.SERVER_ERROR + response.code(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UpdateProfileImageData>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(this@ProfileActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}