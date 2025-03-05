package ecommerce.onkt

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var login : Button
    lateinit var signup : Button

    lateinit var pd : ProgressDialog
    lateinit var apiInterface: ApiInterface
    lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        email = findViewById(R.id.main_username)
        password = findViewById(R.id.main_password)

        signup = findViewById(R.id.main_signup)
        login = findViewById(R.id.main_login)

        login.setOnClickListener {
            if(email.text.toString().trim().equals("")){
                email.error = "Username Required"
            }
            else if(password.text.toString().trim().equals("")){
                password.error = "Password Required"
            }
            else if(password.text.toString().trim().length<6){
                password.error = "Min. 6 Char Password Required"
            }
            else {
                /*Toast.makeText(this@MainActivity, "Login Successfully", Toast.LENGTH_SHORT).show()
                Snackbar.make(it, "Login Successfully", Snackbar.LENGTH_LONG).show()*/

                if(ConnectionDetector(this@MainActivity).networkConnected()){
                    pd = ProgressDialog(this@MainActivity)
                    pd.setMessage("Please Wait...")
                    pd.setCancelable(false)
                    pd.show()
                    doLoginRetrofit()
                }
                else{
                    ConnectionDetector(this@MainActivity).networkDisconnected()
                }

            }
        }

        signup.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                //TODO("Not yet implemented")
                Snackbar.make(p0!!,"Signup Successfully",Snackbar.LENGTH_SHORT).show()

                var intent : Intent = Intent(this@MainActivity,SignupActivity::class.java)
                startActivity(intent)
            }
        })

    }

    private fun doLoginRetrofit() {
        //TODO("Not yet implemented")
        var call : Call<GetLoginData> = apiInterface.getLoginData(email.text.toString(),password.text.toString())
        call.enqueue(object : Callback<GetLoginData>{
            override fun onResponse(call: Call<GetLoginData>, response: Response<GetLoginData>) {
                //TODO("Not yet implemented")
                pd.dismiss()
                if(response.code() == 200){
                    if(response.body()!!.status!!){
                        Toast.makeText(this@MainActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                        val i = 0;
                        for(i in 0..response.body()!!.userData!!.size-1){
                            sp.edit().putString(ConstantSp.USERID, response.body()!!.userData!!.get(i).userid).commit()
                            sp.edit().putString(ConstantSp.FIRSTNAME, response.body()!!.userData!!.get(i).firstName).commit()
                            sp.edit().putString(ConstantSp.LASTNAME, response.body()!!.userData!!.get(i).lastName).commit()
                            sp.edit().putString(ConstantSp.EMAIL, response.body()!!.userData!!.get(i).email).commit()
                            sp.edit().putString(ConstantSp.CONTACT, response.body()!!.userData!!.get(i).contact).commit()
                            sp.edit().putString(ConstantSp.PASSWORD, "").commit()
                            sp.edit().putString(ConstantSp.GENDER, response.body()!!.userData!!.get(i).gender).commit()
                            sp.edit().putString(ConstantSp.PROFILE, response.body()!!.userData!!.get(i).profile).commit()
                        }
                        var intent : Intent = Intent(this@MainActivity,ProfileActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this@MainActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@MainActivity,ConstantSp.SERVER_ERROR+ response.code(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetLoginData>, t: Throwable) {
                //TODO("Not yet implemented")
                pd.dismiss()
                Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })
    }
}