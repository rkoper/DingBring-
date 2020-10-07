package com.so.dingbring

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.so.dingbring.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding= DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.terms-> {
                Toast.makeText(this@MainActivity,"agreenment", Toast.LENGTH_SHORT).show()
            }
            R.id.privacy-> {
                Toast.makeText(this@MainActivity,"agreenment", Toast.LENGTH_SHORT).show()

            }

            R.id.share->{
                val intent= Intent()
                intent.apply {
                    action= Intent.ACTION_SEND
                    type="text/plain"
                    putExtra(Intent.EXTRA_TEXT,"download this app through")
                    `package`="com.whatsapp"
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
