package com.example.dicepokerretry

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    var playerName = ""
    var roomName = ""
    lateinit var roomList:ArrayList<String>

    lateinit var database: FirebaseDatabase

    lateinit var roomRef: DatabaseReference
    lateinit var roomsRef: DatabaseReference

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        database = FirebaseDatabase.getInstance()

        var preferences:SharedPreferences = getSharedPreferences("PREFS",0)
        playerName = preferences.getString("playerName","")
        roomName = playerName

        roomList = ArrayList<String>()

        button.setOnClickListener {
            button.text = "CREATING ROOM"
            button.isEnabled=false
            roomName = playerName
            roomRef=database.getReference("rooms/$roomName/player1")
            addRoomEventListener()
            roomRef.setValue(playerName)
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            roomName = roomList.get(position)
            roomRef = database.getReference("rooms/$roomName/player2")
            addRoomEventListener()
            roomRef.setValue(playerName)
        }

        addRoomsEventListener()
    }

    private fun addRoomEventListener(){
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                button.text = "CREATING ROOM"
                button.isEnabled=false
                Toast.makeText(this@Main2Activity,"Error!",Toast.LENGTH_SHORT).show()

            }

            override fun onDataChange(p0: DataSnapshot) {
                button.text = "CREATING ROOM"
                button.isEnabled = true
                val intent = Intent(applicationContext,Main3Activity::class.java)
                intent.putExtra("roomName",roomName)
                startActivity(intent)
            }
        })
    }

    private fun addRoomsEventListener(){
        roomsRef = database.getReference("rooms")
        roomsRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                roomList.clear()
                var rooms:Iterable<DataSnapshot> = p0.children
                for (snapshot: DataSnapshot in rooms){
                    roomList.add(snapshot.key.toString())
                    var adapter:ArrayAdapter<String> = ArrayAdapter<String>(this@Main2Activity, android.R.layout.simple_list_item_1, roomList)
                    listView.adapter=adapter
                }
            }

        })
    }
}
