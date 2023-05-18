package codes.haardikbhagtani.userformroom

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import codes.haardikbhagtani.userformroom.databinding.ActivityFormBinding
import codes.haardikbhagtani.userformroom.room.AppDatabase
import codes.haardikbhagtani.userformroom.room.User
import java.util.*
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

const val TAG = "MAIN"

class FormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormBinding
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    binding.ivProfilePicture.visibility = View.GONE
                    binding.ivSelectedProfilePicture.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    binding.ivSelectedProfilePicture.setImageURI(fileUri)
                    //binding.tvProfilePicture.visibility = View.GONE
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_form)

        binding.title.text = "Add User Form"

        binding.back.setOnClickListener{
            finish()
        }

        binding.lName.hint = "Name"

        //binding.etDesignation.text = "Select Designation"

        val designations = resources.getStringArray(R.array.designations)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, designations)
        // get reference to the autocomplete text view
        val autocompleteTV = binding.etDesignation
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)

        binding.tvProfilePicture.text = "Profile Picture"

        binding.ivProfilePicture.setOnClickListener{
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.etDate.hint = "DOB"
        binding.etDate.setOnClickListener {
            Log.i(TAG, "Date")
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our text view.
                    binding.etDate.setText((dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year))

                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            // at last we are calling show
            // to display our date picker dialog.
            datePickerDialog.show()
        }

        binding.bAddUser.text = "Save User"

        binding.bAddUser.setOnClickListener {
            if(binding.ivSelectedProfilePicture.visibility == View.GONE){
                Toast.makeText(
                    this@FormActivity,
                    "Profile Picture required ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if(binding.etName.text.isEmpty()){
                Toast.makeText(
                    this@FormActivity,
                    "Name required ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if(binding.etDesignation.text.toString() == "Select Designation"){
                Toast.makeText(
                    this@FormActivity,
                    "Designation required ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if(binding.etDate.text.isEmpty()){
                Toast.makeText(
                    this@FormActivity,
                    "DOB required ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val db = AppDatabase(this)
            val userDao = db.userDao()
            val baos = ByteArrayOutputStream()
            binding.ivSelectedProfilePicture.drawToBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            val image = Base64.getEncoder().encodeToString(b)

            if(this.intent.getSerializableExtra("user") != null) {
                val user  = this.intent.getSerializableExtra("user") as User
                GlobalScope.launch {
                    userDao.updateUser(
                        User(
                            id = user.id,
                            name = binding.etName.text.toString(),
                            designation = binding.etDesignation.text.toString(),
                            dob = binding.etDate.text.toString(),
                            profilePicture = image
                        )
                    )
                    setResult(RESULT_OK)
                    finish()
                    startActivity(Intent(this@FormActivity, ListingActivity::class.java))
                }
            } else{
                GlobalScope.launch {
                    userDao.insertUser(
                        User(
                            id = 0,
                            name = binding.etName.text.toString(),
                            designation = binding.etDesignation.text.toString(),
                            dob = binding.etDate.text.toString(),
                            profilePicture = image
                        )
                    )
                    setResult(RESULT_OK)
                    finish()
                    startActivity(Intent(this@FormActivity, ListingActivity::class.java))
                }
            }
        }

        val user = this.intent.getSerializableExtra("user")

        if(user != null){
            val person = user as User
            binding.ivProfilePicture.visibility = View.GONE
            binding.ivSelectedProfilePicture.visibility = View.VISIBLE
            binding.view2.visibility = View.VISIBLE
            val decodedString: ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getDecoder().decode(person.profilePicture)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.ivSelectedProfilePicture.setImageBitmap(decodedByte)
            binding.etName.setText(person.name)
            binding.etDate.setText(person.dob)
            binding.etDesignation.setText(person.designation)
            val designations = resources.getStringArray(R.array.designations)
            // create an array adapter and pass the required parameter
            // in our case pass the context, drop down layout , and array.
            val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, designations)
            // get reference to the autocomplete text view
            val autocompleteTV = binding.etDesignation
            // set adapter to the autocomplete tv to the arrayAdapter
            autocompleteTV.setAdapter(arrayAdapter)
        }

    }
}