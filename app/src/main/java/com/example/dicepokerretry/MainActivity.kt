package com.example.dicepokerretry

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var playerName = ""

    lateinit var database: FirebaseDatabase
    lateinit var playerReference: DatabaseReference
    var backCheck = 0


    override fun onBackPressed(){
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()


        var preferences:SharedPreferences = getSharedPreferences("PREFS",0)
        playerName = preferences.getString("playerName", "")
        if (!playerName.equals("")){
            playerReference = database.getReference("players/$playerName")
            addEventListener()
            playerReference.setValue("")
        }

        button.setOnClickListener{
            playerName = editText.text.toString()
            editText.setText("")
            if (!playerName.equals("")){
                button.text = "LOGGING IN"
                button.isEnabled=false
                playerReference = database.getReference("players/$playerName")
                addEventListener()
                playerReference.setValue("")
            }
        }
    }
    private fun addEventListener(){
        playerReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                button.text = "LOG IN"
                button.isEnabled = true
                Toast.makeText(this@MainActivity, "Error!",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                    if (!playerName.equals("")&&backCheck.equals(0)){
                        var preferences:SharedPreferences = getSharedPreferences("PREFS",0)
                        var editor:SharedPreferences.Editor = preferences.edit()
                        editor.putString("playerName",playerName)
                        editor.apply()
                        backCheck++
                        startActivity(Intent(this@MainActivity,Main2Activity::class.java))
                    }
            }
        })
    }
}
