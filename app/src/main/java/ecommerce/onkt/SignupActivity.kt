package ecommerce.onkt

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    lateinit var firstname : EditText
    lateinit var lastname : EditText
    lateinit var email : EditText
    lateinit var contact : EditText
    lateinit var password : EditText
    lateinit var confirmpassword : EditText
    lateinit var gender : RadioGroup
    lateinit var checkBox: CheckBox
    lateinit var submit : Button
    var emailPattern : String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    var apiInterface: ApiInterface? = null
    var pd: ProgressDialog? = null
    lateinit var sGender : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        firstname = findViewById(R.id.signup_firstname)
        lastname = findViewById(R.id.signup_lastname)
        email = findViewById(R.id.signup_email)
        contact = findViewById(R.id.signup_contact)
        password = findViewById(R.id.signup_password)
        confirmpassword = findViewById(R.id.signup_retype_password)
        gender = findViewById(R.id.signup_gender)
        checkBox = findViewById(R.id.signup_terms)
        submit = findViewById(R.id.signup_button)

        gender.setOnCheckedChangeListener { radioGroup, i ->
            var rb : RadioButton = findViewById(i)
            sGender = rb.text.toString()
        }

        submit.setOnClickListener {
            if(firstname.text.toString().trim().equals("")){
                firstname.error = "First Name Required"
            }
            else if(lastname.text.toString().trim().equals("")){
                lastname.error = "Last Name Required"
            }
            else if(email.text.toString().trim().equals("")){
                email.error = "Email Id Required"
            }
            else if(!email.text.toString().trim().matches(Regex(emailPattern))){
                email.error = "Valid Email Id Required"
            }
            else if(contact.text.toString().trim().equals("")){
                contact.error = "Contact Required"
            }
            else if(contact.text.toString().trim().length<10){
                contact.error = "Valid Contact Required"
            }
            else if(password.text.toString().trim().equals("")){
                password.error = "Password Required"
            }
            else if(confirmpassword.text.toString().trim().equals("")){
                confirmpassword.error = "Confirm Password Required"
            }
            else if(!confirmpassword.text.toString().trim().matches(Regex(password.text.toString().trim()))){
                confirmpassword.error = "Password Does Not Match"
            }
            else if(gender.checkedRadioButtonId == -1){
                Toast.makeText(this@SignupActivity,"Please Select Gender",Toast.LENGTH_LONG).show()
            }
            else if(!checkBox.isChecked){
                Toast.makeText(this@SignupActivity,"Please Accept Terms And Conditions",Toast.LENGTH_LONG).show()
            }
            else{
                /*Toast.makeText(this@SignupActivity,"Signup Successfully",Toast.LENGTH_LONG).show()
                onBackPressed()*/
                if(ConnectionDetector(this@SignupActivity).networkConnected()){
                    pd = ProgressDialog(this@SignupActivity)
                    pd!!.setMessage("Please Wait...")
                    pd!!.setCancelable(false)
                    pd!!.show()
                    doRetrofitSignup()
                }
                else{
                    ConnectionDetector(this@SignupActivity).networkDisconnected()
                }
            }
        }

    }

    private fun doRetrofitSignup() {
        //TODO("Not yet implemented")
        var call : Call<GetSignupData> = apiInterface!!.getSignupData(
            firstname.text.toString(),
            lastname.text.toString(),
            email.text.toString(),
            contact.text.toString(),
            password.text.toString(),
            sGender
        )

        call.enqueue(object : Callback<GetSignupData>{
            override fun onResponse(call: Call<GetSignupData>, response: Response<GetSignupData>) {
                //TODO("Not yet implemented")
                pd!!.dismiss()
                if(response.code()==200){
                    if(response.body()!!.status!!){
                        Toast.makeText(this@SignupActivity,response.body()!!.message,Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                    else{
                        Toast.makeText(this@SignupActivity,response.body()!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@SignupActivity,ConstantSp.SERVER_ERROR+response.code(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetSignupData>, t: Throwable) {
                //TODO("Not yet implemented")
                pd!!.dismiss()
                Toast.makeText(this@SignupActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })

    }
}