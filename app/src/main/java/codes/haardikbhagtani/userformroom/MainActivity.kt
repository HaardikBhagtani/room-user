package codes.haardikbhagtani.userformroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import codes.haardikbhagtani.userformroom.room.AppDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppDatabase.invoke(this@MainActivity)

        val intent = Intent(this, ListingActivity::class.java)
        startActivity(intent)
        finish()
    }
}