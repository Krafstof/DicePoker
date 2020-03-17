package com.example.dicepokerretry

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main2.button
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {

    //var dice_one:Drawable = resources.getDrawable()

    var playerName = ""
    var roomName = ""
    var role = ""
    var messageH = ""
    var messageG = ""
    var status = ""
    var userCombination = " "
    var userHigh = 0
    var enemyCombination = " "
    var enemyHigh = 0
    var userCombinationWeight = 0
    var enemyCombinationWeight = 0
    var userHandHigh = 0
    var enemyHandHigh = 0
    var enemyHandLast = " "
    var enemyScore = IntArray(5)
    var userScore = IntArray(5)
    var userChange = booleanArrayOf(false, false, false, false, false)



    lateinit var database: FirebaseDatabase
    lateinit var messageHReference: DatabaseReference
    lateinit var messageGReference: DatabaseReference
    lateinit var roomReference: DatabaseReference
    lateinit var statusReference: DatabaseReference


    override fun onPause() {
        super.onPause()
        database = FirebaseDatabase.getInstance()
        /*var preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        playerName = preferences.getString("playerName", "")
        var extras: Bundle = intent.extras
        roomName = extras.getString("roomName")*/
        roomReference = database.getReference("rooms/$roomName")
        roomReference.removeValue()
        status = "opponent_exit"
        statusReference.setValue(status)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        rulesButton.setOnClickListener {
            val builder = Builder(this@Main3Activity)
            builder.setTitle("Правила")
            builder.setMessage(
                "Игра идет по очереди, правила схожи с техасским покером. Сначала игроки бросают кости, затем они могут выбрать, какие из своих костей перебросить." +
                        "Побеждает игрок с сильнейшей комбинацией. \nСписок комбинаций по возрастанию:\nПара - две одинаковые кости\nДве пары - пара двух одинаковых костей\n" +
                        "Сет - три одинаковые кости\nФулхаус - три и еще две одинаковые кости\nМалый стрейт - 5, 4, 3, 2, 1\nБольшой стрейт - 6, 5, 4, 3, 2, 1\n" +
                        "Каре - четыре одинаковые кости\nПокер - пять одинаковых костей"
            )

            builder.setPositiveButton("OK") { _, _ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


        button.isEnabled = false
        rethrow.isEnabled = false

        database = FirebaseDatabase.getInstance()


        var preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        playerName = preferences.getString("playerName", "")

        var extras: Bundle = intent.extras
        roomName = extras.getString("roomName")
        if (roomName.equals(playerName)) {
            role = "host"
        } else role = "guest"


        exitButton.setOnClickListener {
            val intent = Intent(applicationContext, Main3Activity::class.java)
            startActivity(intent)
        }

        checkState()


        messageHReference = database.getReference("rooms/$roomName/messageH")
        messageH = " "
        messageGReference = database.getReference("rooms/$roomName/messageG")
        messageG = " "
        statusReference = database.getReference("rooms/$roomName/status")
        status = " "
        messageHReference.setValue(messageH)
        messageGReference.setValue(messageG)
        statusReference.setValue(status)
        addStatusEventListener()
        if (role.equals("host") && status.equals(" ")) {
            button.setOnClickListener {
                messageH = "hf"
                for (value in (1..5)) {
                    messageH += (1..6).random().toString()
                }

                setValueToDices()
                messageHReference.setValue(messageH)
                status = "host_throwed"
                button.isEnabled = false
                statusReference.setValue(status)
            }
            addMessageHEventListener()
        }

        if (role.equals("guest")) {
            button.setOnClickListener {
                messageG = "gf"
                for (value in (1..5)) {
                    messageG += (1..6).random().toString()
                }

                setValueToDices()
                messageGReference.setValue(messageG)
                button.isEnabled = false
                status = "guest_throwed"
                statusReference.setValue(status)
            }
            messageG = "guest_joined"
            messageGReference.setValue(messageG)
            addMessageGEventListener()
        }

        if (role.equals("host")) {
            rethrow.setOnClickListener {
                var messageHtemp = messageH
                var C1Image = findViewById<ImageView>(R.id.С1Image)
                var C2Image = findViewById<ImageView>(R.id.С2Image)
                var C3Image = findViewById<ImageView>(R.id.С3Image)
                var C4Image = findViewById<ImageView>(R.id.С4Image)
                var C5Image = findViewById<ImageView>(R.id.С5Image)
                messageH = "hs"
                if (userChange[0]) {
                    messageH += (1..6).random().toString()
                } else messageH += messageHtemp.get(2)
                if (userChange[1]) {
                    messageH += (1..6).random().toString()
                } else messageH += messageHtemp.get(3)
                if (userChange[2]) {
                    messageH += (1..6).random().toString()
                } else messageH += messageHtemp.get(4)
                if (userChange[3]) {
                    messageH += (1..6).random().toString()
                } else messageH += messageHtemp.get(5)
                if (userChange[4]) {
                    messageH += (1..6).random().toString()
                } else messageH += messageHtemp.get(6)
                D1Image.isEnabled = false
                D2Image.isEnabled = false
                D3Image.isEnabled = false
                D4Image.isEnabled = false
                D5Image.isEnabled = false
                setValueToDices()
                C1Image.visibility = View.INVISIBLE
                C2Image.visibility = View.INVISIBLE
                C3Image.visibility = View.INVISIBLE
                C4Image.visibility = View.INVISIBLE
                C5Image.visibility = View.INVISIBLE
                messageHReference.setValue(messageH)
                addMessageHEventListener()
                status = "host_rethrowed"
                rethrow.isEnabled = false
                statusReference.setValue(status)
            }
        }

        if (role.equals("guest")) {
            rethrow.setOnClickListener {
                var messageGtemp = messageG
                var C1Image = findViewById<ImageView>(R.id.С1Image)
                var C2Image = findViewById<ImageView>(R.id.С2Image)
                var C3Image = findViewById<ImageView>(R.id.С3Image)
                var C4Image = findViewById<ImageView>(R.id.С4Image)
                var C5Image = findViewById<ImageView>(R.id.С5Image)
                messageG = "gs"
                if (userChange[0]) {
                    messageG += (1..6).random().toString()
                } else messageG += messageGtemp.get(2)
                if (userChange[1]) {
                    messageG += (1..6).random().toString()
                } else messageG += messageGtemp.get(3)
                if (userChange[2]) {
                    messageG += (1..6).random().toString()
                } else messageG += messageGtemp.get(4)
                if (userChange[3]) {
                    messageG += (1..6).random().toString()
                } else messageG += messageGtemp.get(5)
                if (userChange[4]) {
                    messageG += (1..6).random().toString()
                } else messageG += messageGtemp.get(6)
                setValueToDices()
                messageGReference.setValue(messageG)
                addMessageGEventListener()
                D1Image.isEnabled = false
                D2Image.isEnabled = false
                D3Image.isEnabled = false
                D4Image.isEnabled = false
                D5Image.isEnabled = false
                C1Image.visibility = View.INVISIBLE
                C2Image.visibility = View.INVISIBLE
                C3Image.visibility = View.INVISIBLE
                C4Image.visibility = View.INVISIBLE
                C5Image.visibility = View.INVISIBLE
                rethrow.isEnabled = false
                status = "guest_rethrowed"
                statusReference.setValue(status)
            }
        }

    }


    fun checkState(){
        var animationChecked:RotateAnimation = RotateAnimation(0f, 350f, 50f, 50f)
        var animationElse:RotateAnimation = RotateAnimation(0f, -350f, 50f, 50f)
        animationChecked.duration = 750
        animationElse.duration = 150
        var D1Image = findViewById<ImageView>(R.id.D1Image)
        var D2Image = findViewById<ImageView>(R.id.D2Image)
        var D3Image = findViewById<ImageView>(R.id.D3Image)
        var D4Image = findViewById<ImageView>(R.id.D4Image)
        var D5Image = findViewById<ImageView>(R.id.D5Image)
        var C1Image = findViewById<ImageView>(R.id.С1Image)
        var C2Image = findViewById<ImageView>(R.id.С2Image)
        var C3Image = findViewById<ImageView>(R.id.С3Image)
        var C4Image = findViewById<ImageView>(R.id.С4Image)
        var C5Image = findViewById<ImageView>(R.id.С5Image)
        D1Image.setOnClickListener() {
            if (!userChange[0]){
                D1Image.startAnimation(animationChecked)
                userChange[0]=true
                C1Image.visibility = View.VISIBLE

            }
            else {
                D1Image.startAnimation(animationElse)
                userChange[0]=false
                C1Image.visibility = View.INVISIBLE

            }
        }
        D2Image.setOnClickListener() {
            if (!userChange[1]){
                D2Image.startAnimation(animationChecked)
                userChange[1]=true
                C2Image.visibility = View.VISIBLE
            }
            else {
                D2Image.startAnimation(animationElse)
                userChange[1]=false
                C2Image.visibility = View.INVISIBLE
            }
        }
        D3Image.setOnClickListener() {
            if (!userChange[2]){
                D3Image.startAnimation(animationChecked)
                userChange[2]=true
                C3Image.visibility = View.VISIBLE
            }
            else {
                D3Image.startAnimation(animationElse)
                userChange[2]=false
                C3Image.visibility = View.INVISIBLE
            }
        }
        D4Image.setOnClickListener() {
            if (!userChange[3]){
                D4Image.startAnimation(animationChecked)
                userChange[3]=true
                C4Image.visibility = View.VISIBLE
            }
            else {
                D4Image.startAnimation(animationElse)
                userChange[3]=false
                C4Image.visibility = View.INVISIBLE
            }
        }
        D5Image.setOnClickListener() {
            if (!userChange[4]){
                D5Image.startAnimation(animationChecked)
                userChange[4]=true
                C5Image.visibility = View.VISIBLE
            }
            else {
                D5Image.startAnimation(animationElse)
                userChange[4]=false
                C5Image.visibility = View.INVISIBLE
            }
        }
    }

    private fun addStatusEventListener() {
        statusReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var value: String = p0.getValue(String::class.java).toString()
                if (value.equals("host_throwed")) {
                    if (role.equals("guest")) {
                        button.isEnabled = true
                    }
                }
                if (value.equals("guest_throwed")) {
                    if (role.equals("host")) {
                        D1Image.isEnabled = true
                        D2Image.isEnabled = true
                        D3Image.isEnabled = true
                        D4Image.isEnabled = true
                        D5Image.isEnabled = true
                        rethrow.isEnabled = true
                    }
                }
                if (value.equals("host_rethrowed")) {
                    if (role.equals("guest")) {
                        D1Image.isEnabled = true
                        D2Image.isEnabled = true
                        D3Image.isEnabled = true
                        D4Image.isEnabled = true
                        D5Image.isEnabled = true
                        rethrow.isEnabled = true
                    }
                }
                if (value.equals("guest_rethrowed")) {
                    if (role.equals("host")) {
                        checkResult(messageH)
                        checkResultEnemy(enemyHandLast)
                        userCombinationView.text = "Ваша комбинация: $userCombination"
                        enemyCombinationView.text = "Комбинация противника: $enemyCombination"
                        statusReference = database.getReference("rooms/$roomName/status")
                        status = "host_sent_scores"
                        statusReference.setValue(status)
                    }
                }
                if (value.equals("host_sent_scores")) {
                    if (role.equals("guest")) {
                        checkResult(messageG)
                        checkResultEnemy(enemyHandLast)
                        userCombinationView.text = "Ваша комбинация: $userCombination"
                        enemyCombinationView.text = "Комбинация противника: $enemyCombination"
                        statusReference = database.getReference("rooms/$roomName/status")
                        status = "guest_sent_scores"
                        statusReference.setValue(status)
                    }
                }
                if (value.equals("guest_sent_scores")) {
                    comparisonLast()
                    if (userCombinationWeight > enemyCombinationWeight) {
                        textView.text = "WIN"
                    } else if (userCombinationWeight < enemyCombinationWeight) {
                        textView.text = "LOST"
                    } else {
                        if (userHigh > enemyHigh) {
                            textView.text = "WIN"
                        } else if (userHigh < enemyHigh) {
                            textView.text = "LOST"
                        } else {

                            if (userHandHigh > enemyHandHigh) {
                                textView.text = "WIN"
                            } else if (userHandHigh < enemyHandHigh) {
                                textView.text = "LOST"
                            } else textView.text = "DRAW"
                        }
                    }
                }
                if (value.equals("opponent_exit")) {
                    database = FirebaseDatabase.getInstance()
                    roomReference = database.getReference("rooms/$roomName")
                    roomReference.removeValue()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addMessageGEventListener() {
        //if (role.equals("guest")) {
        messageHReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var value: String = p0.getValue(String::class.java).toString()
                if (value.length.equals(7)) {
                    setValueToDices(value)
                    if (value[1].equals('s')) {
                        enemyHandLast = value
                    }
                }
            }

        })
        //}
    }

    private fun addMessageHEventListener() {
        //if (role.equals("host")) {
        messageGReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var value: String = p0.getValue(String::class.java).toString()
                if (value.equals("guest_joined")) {
                    button.isEnabled = true
                }
                if (value.length.equals(7)) {
                    setValueToDices(value)
                    if (value[1].equals('s')) {
                        enemyHandLast = value
                    }
                }
            }

        })
        //}
    }

    fun setValueToDices(value:String){
        var D1Image = findViewById<ImageView>(R.id.O1Image)
        var D2Image = findViewById<ImageView>(R.id.O2Image)
        var D3Image = findViewById<ImageView>(R.id.O3Image)
        var D4Image = findViewById<ImageView>(R.id.O4Image)
        var D5Image = findViewById<ImageView>(R.id.O5Image)
        var hostMess = value
        if (hostMess[2].equals('1')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one_red))
        if (hostMess[2].equals('2')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two_red))
        if (hostMess[2].equals('3')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three_red))
        if (hostMess[2].equals('4')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four_red))
        if (hostMess[2].equals('5')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five_red))
        if (hostMess[2].equals('6')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six_red))
        //D1Image.startAnimation(slideUpAnimation)
        D1Image.visibility=View.VISIBLE
        if (hostMess[3].equals('1')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one_red))
        if (hostMess[3].equals('2')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two_red))
        if (hostMess[3].equals('3')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three_red))
        if (hostMess[3].equals('4')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four_red))
        if (hostMess[3].equals('5')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five_red))
        if (hostMess[3].equals('6')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six_red))
        //D2Image.startAnimation(slideUpAnimation)
        D2Image.visibility=View.VISIBLE
        if (hostMess[4].equals('1')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one_red))
        if (hostMess[4].equals('2')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two_red))
        if (hostMess[4].equals('3')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three_red))
        if (hostMess[4].equals('4')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four_red))
        if (hostMess[4].equals('5')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five_red))
        if (hostMess[4].equals('6')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six_red))
        //D3Image.startAnimation(slideUpAnimation)
        D3Image.visibility=View.VISIBLE
        if (hostMess[5].equals('1')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one_red))
        if (hostMess[5].equals('2')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two_red))
        if (hostMess[5].equals('3')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three_red))
        if (hostMess[5].equals('4')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four_red))
        if (hostMess[5].equals('5')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five_red))
        if (hostMess[5].equals('6')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six_red))
        //D4Image.startAnimation(slideUpAnimation)
        D4Image.visibility=View.VISIBLE
        if (hostMess[6].equals('1')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one_red))
        if (hostMess[6].equals('2')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two_red))
        if (hostMess[6].equals('3')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three_red))
        if (hostMess[6].equals('4')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four_red))
        if (hostMess[6].equals('5')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five_red))
        if (hostMess[6].equals('6')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six_red))
        //D5Image.startAnimation(slideUpAnimation)
        D5Image.visibility=View.VISIBLE

    }

    fun setValueToDices() {
        var D1Image = findViewById<ImageView>(R.id.D1Image)
        var D2Image = findViewById<ImageView>(R.id.D2Image)
        var D3Image = findViewById<ImageView>(R.id.D3Image)
        var D4Image = findViewById<ImageView>(R.id.D4Image)
        var D5Image = findViewById<ImageView>(R.id.D5Image)
        var hostMess = messageH
        var guestMess = messageG
        if (role.equals("host")) {
            if (hostMess[2].equals('1')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (hostMess[2].equals('2')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (hostMess[2].equals('3')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (hostMess[2].equals('4')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (hostMess[2].equals('5')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (hostMess[2].equals('6')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D1Image.visibility=View.VISIBLE
            if (hostMess[3].equals('1')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (hostMess[3].equals('2')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (hostMess[3].equals('3')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (hostMess[3].equals('4')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (hostMess[3].equals('5')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (hostMess[3].equals('6')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D2Image.visibility=View.VISIBLE
            if (hostMess[4].equals('1')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (hostMess[4].equals('2')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (hostMess[4].equals('3')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (hostMess[4].equals('4')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (hostMess[4].equals('5')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (hostMess[4].equals('6')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D3Image.visibility=View.VISIBLE
            if (hostMess[5].equals('1')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (hostMess[5].equals('2')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (hostMess[5].equals('3')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (hostMess[5].equals('4')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (hostMess[5].equals('5')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (hostMess[5].equals('6')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D4Image.visibility=View.VISIBLE
            if (hostMess[6].equals('1')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (hostMess[6].equals('2')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (hostMess[6].equals('3')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (hostMess[6].equals('4')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (hostMess[6].equals('5')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (hostMess[6].equals('6')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D5Image.visibility=View.VISIBLE
        } else {
            //UD1.text = messageG[2].toString()
            if (guestMess[2].equals('1')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (guestMess[2].equals('2')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (guestMess[2].equals('3')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (guestMess[2].equals('4')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (guestMess[2].equals('5')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (guestMess[2].equals('6')) D1Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D1Image.visibility=View.VISIBLE
            if (guestMess[3].equals('1')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (guestMess[3].equals('2')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (guestMess[3].equals('3')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (guestMess[3].equals('4')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (guestMess[3].equals('5')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (guestMess[3].equals('6')) D2Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D2Image.visibility=View.VISIBLE
            if (guestMess[4].equals('1')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (guestMess[4].equals('2')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (guestMess[4].equals('3')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (guestMess[4].equals('4')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (guestMess[4].equals('5')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (guestMess[4].equals('6')) D3Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D3Image.visibility=View.VISIBLE
            if (guestMess[5].equals('1')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (guestMess[5].equals('2')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (guestMess[5].equals('3')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (guestMess[5].equals('4')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (guestMess[5].equals('5')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (guestMess[5].equals('6')) D4Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D4Image.visibility=View.VISIBLE
            if (guestMess[6].equals('1')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_one))
            if (guestMess[6].equals('2')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_two))
            if (guestMess[6].equals('3')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_three))
            if (guestMess[6].equals('4')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_four))
            if (guestMess[6].equals('5')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_five))
            if (guestMess[6].equals('6')) D5Image.setImageDrawable(resources.getDrawable(R.drawable.dice_six))
            D5Image.visibility=View.VISIBLE
        }
    }

    fun comparisonLast() {
        userHandHigh = userScore[4]
        enemyHandHigh = enemyScore[4]
    }

    fun checkResultEnemy(message: String) {
        for (i in (0..4)) {
            enemyScore[i] = message[i + 2].toInt()
        }
        enemyScore.sort()
        if (checkPokerCombination(enemyScore)) {
            enemyCombination = "покер"
            enemyHigh = checkPokerHigh(enemyScore)
            enemyCombinationWeight = 8

        } else if (checkQuadCombination(enemyScore)) {
            enemyCombination = "каре"
            enemyHigh = checkQuadHigh(enemyScore)
            enemyCombinationWeight = 7
        } else if (checkStraightCombination(enemyScore)) {
            if (checkStraightHigh(enemyScore).equals(6)) {
                enemyCombination = "большой стрейт"
                enemyHigh = checkStraightHigh(enemyScore)
                enemyCombinationWeight = 6
            } else {
                enemyCombination = "малый стрейт"
                enemyHigh = checkStraightHigh(enemyScore)
                enemyCombinationWeight = 5
            }
        } else if (checkFullHouseCombination(enemyScore)) {
            enemyCombination = "фулхаус"
            enemyHigh = checkFullHouseHigh(enemyScore)
            enemyCombinationWeight = 4
        } else if (checkSetCombination(enemyScore)) {
            enemyCombination = "сет"
            enemyHigh = checkSetHigh(enemyScore)
            enemyCombinationWeight = 3
        } else if (!checkPairCombination(enemyScore).equals(" ")) {
            if (checkPairCombination(enemyScore).equals("две пары")) enemyCombinationWeight = 2
            else enemyCombinationWeight = 1
            enemyCombination = checkPairCombination(enemyScore)
            enemyHigh = checkPairCHigh(enemyScore)

        } else {
            enemyCombination = "ничего"
            enemyHigh = enemyScore[4]
            enemyCombinationWeight = 0
        }

    }

    fun checkResult(message: String) {
        for (i in (0..4)) {
            userScore[i] = message[i + 2].toInt()
        }
        userScore.sort()
        if (checkPokerCombination(userScore)) {
            userCombination = "покер"
            userHigh = checkPokerHigh(userScore)
            userCombinationWeight = 8

        } else if (checkQuadCombination(userScore)) {
            userCombination = "каре"
            userHigh = checkQuadHigh(userScore)
            userCombinationWeight = 7
        } else if (checkStraightCombination(userScore)) {
            if (checkStraightHigh(userScore).equals(6)) {
                userCombination = "большой стрейт"
                userHigh = checkStraightHigh(userScore)
                userCombinationWeight = 6
            } else {
                userCombination = "малый стрейт"
                userHigh = checkStraightHigh(userScore)
                userCombinationWeight = 5
            }
        } else if (checkFullHouseCombination(userScore)) {
            userCombination = "фулхаус"
            userHigh = checkFullHouseHigh(userScore)
            userCombinationWeight = 4
        } else if (checkSetCombination(userScore)) {
            userCombination = "сет"
            userHigh = checkSetHigh(userScore)
            userCombinationWeight = 3
        } else if (!checkPairCombination(userScore).equals(" ")) {
            if (checkPairCombination(userScore).equals("две пары")) userCombinationWeight = 2
            else userCombinationWeight = 1
            userCombination = checkPairCombination(userScore)
            userHigh = checkPairCHigh(userScore)

        } else {
            userCombination = "ничего"
            userHigh = userScore[4]
            userCombinationWeight = 0
        }

    }

    fun checkPokerCombination(score: IntArray): Boolean {
        return score[0].equals(score[1]) && score[0].equals(score[2]) && score[0].equals(score[3]) && score[0].equals(
            score[4]
        )
    }

    fun checkPokerHigh(score: IntArray): Int {
        if (score[0].equals(score[1]) && score[0].equals(score[2]) && score[0].equals(score[3]) && score[0].equals(
                score[4]
            )
        ) {
            return score[0]
        } else return 0
    }

    fun checkPairCombination(score: IntArray): String {
        var type = 0
        for (i in (0..4)) {
            for (j in (i..4)) {
                if (score[i].equals(score[j]) && !i.equals(j)) type++
            }
        }
        when {
            type.equals(2) -> return "две пары"
            type.equals(1) -> return "пара"
            else -> return " "
        }
    }

    fun checkPairCHigh(score: IntArray): Int {
        var type = 0
        for (i in 0..4) {
            for (j in i..4) {
                if (score[i].equals(score[j]) && !i.equals(j)) type = score[i]
            }
        }
        return type
    }

    fun checkSetCombination(score: IntArray): Boolean {
        return (score[0].equals(score[1]) && score[0].equals(score[2])) || (score[1].equals(score[2]) && score[1].equals(
            score[3]
        )) || (score[2].equals(score[3]) && score[2].equals(score[4]))
    }

    fun checkSetHigh(score: IntArray): Int {
        return score[2]
    }

    fun checkFullHouseCombination(score: IntArray): Boolean {
        return ((score[1].equals(score[0]) && score[1].equals(score[2])) && score[3].equals(score[4])) || ((score[3].equals(
            score[2]
        ) && score[3].equals(score[4])) && score[0].equals(score[1]))
    }

    fun checkFullHouseHigh(score: IntArray): Int {
        if (score[0] > score[4]) return score[0]
        else return score[4]
    }

    fun checkStraightCombination(score: IntArray): Boolean {
        return (score[0].equals(score[1]+1)&&score[1].equals(score[2]+1)&&score[2].equals(score[3]+1)&&score[3].equals(score[4]+1))
    }

    fun checkStraightHigh(score: IntArray): Int {
        return score[4]
    }

    fun checkQuadCombination(score: IntArray): Boolean {
        return ((score[0].equals(score[1]) && score[0].equals(score[2])) && score[0].equals(score[3])) || ((score[1].equals(
            score[2]
        ) && score[1].equals(score[3])) && score[1].equals(score[4]))
    }

    fun checkQuadHigh(score: IntArray): Int {
        return score[2]
    }
}


