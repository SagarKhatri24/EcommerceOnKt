package ecommerce.onkt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var login : Button
    lateinit var signup : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                Toast.makeText(this@MainActivity, "Login Successfully", Toast.LENGTH_SHORT).show()
                Snackbar.make(it, "Login Successfully", Snackbar.LENGTH_LONG).show()
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
}