package codes.haardikbhagtani.userformroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import codes.haardikbhagtani.userformroom.adapter.UserAdapter
import codes.haardikbhagtani.userformroom.databinding.ActivityListingBinding
import codes.haardikbhagtani.userformroom.room.AppDatabase
import codes.haardikbhagtani.userformroom.room.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

interface CellClickListener {
    fun onCellClickListener(button: View, index: Int, user: User)
}

class ListingActivity : AppCompatActivity(), CellClickListener {
    private lateinit var binding: ActivityListingBinding
    private lateinit var adapter: UserAdapter
    private lateinit var users: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_listing)

        binding.title.text = "Users"

        binding.add.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }
        val db = AppDatabase(this)
        val userDao = db.userDao()

        GlobalScope.launch {
            users = userDao.getAll() as ArrayList<User>
            Log.i("MAIN", users.size.toString())
            adapter = UserAdapter(this@ListingActivity, users, this@ListingActivity)

            binding.rvUserList.layoutManager = LinearLayoutManager(this@ListingActivity, LinearLayoutManager.VERTICAL, false)

            binding.rvUserList.adapter = adapter

        }
    }

    override fun onCellClickListener(button: View, index: Int, user: User) {
        val popupMenu = PopupMenu(this, button)
        popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    val intent = Intent(this, FormActivity::class.java).apply {
                        putExtra("user", user)
                    }
                    startActivity(intent)
                    Toast.makeText(
                        this@ListingActivity,
                        "You Clicked : " + item.title,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.delete -> {
                    val db = AppDatabase(this)
                    val userDao = db.userDao()
                    GlobalScope.launch {
                        userDao.delete(user)
                    }
                    MainScope().launch {
                        users.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                }
            }
            true
        }
        popupMenu.show()
    }
}