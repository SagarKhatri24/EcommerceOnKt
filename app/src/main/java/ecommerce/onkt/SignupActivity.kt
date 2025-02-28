package ecommerce.onkt

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firstname = findViewById(R.id.signup_firstname)
        lastname = findViewById(R.id.signup_lastname)
        email = findViewById(R.id.signup_email)
        contact = findViewById(R.id.signup_contact)
        password = findViewById(R.id.signup_password)
        confirmpassword = findViewById(R.id.signup_retype_password)
        gender = findViewById(R.id.signup_gender)
        checkBox = findViewById(R.id.signup_terms)
        submit = findViewById(R.id.signup_button)

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
                Toast.makeText(this@SignupActivity,"Signup Successfully",Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }

    }
}