package ecommerce.onkt

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {

    var imageView: ImageView? = null
    var sp: SharedPreferences? = null

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
        Manifest.permission.CAMERA,
        Manifest.permission.POST_NOTIFICATIONS
    )

    val PERMISSION_REQUEST_CODE: Int = 1240

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE)

        imageView = findViewById(R.id.splash_image)
        Glide.with(this@SplashActivity).asGif()
            .load("https://cdn.pixabay.com/animation/2022/08/01/20/42/20-42-37-53_512.gif")
            .placeholder(R.mipmap.ic_launcher).into(imageView!!)

        if (checkAndRequestPermission()) {
            doSplash()
        }
    }

    private fun doSplash() {
        Handler().postDelayed({
            if (sp!!.getString(ConstantSp.USERID, "") == "") {
                val intent = Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(
                    this@SplashActivity,
                    ProfileActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }, 3000)
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
                doSplash()
            } else {
                for ((permName, permResult) in permissionResult) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@SplashActivity,
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
        val builder = AlertDialog.Builder(this@SplashActivity)
        builder.setTitle(title)
        builder.setCancelable(isCancelable)
        builder.setMessage(msg)
        builder.setPositiveButton(positiveLable, positiveOnClickListener)
        builder.setNegativeButton(negativeLable, negativeOnClickListener)
        val alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }

}