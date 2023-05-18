package codes.haardikbhagtani.userformroom.adapter

import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import codes.haardikbhagtani.userformroom.CellClickListener
import codes.haardikbhagtani.userformroom.ListingActivity
import codes.haardikbhagtani.userformroom.R
import codes.haardikbhagtani.userformroom.databinding.UserBinding
import codes.haardikbhagtani.userformroom.room.User
import java.util.*


class UserAdapter(
    private val activity: ListingActivity,
    private var list: ArrayList<User>,
    private val cellClickListener: CellClickListener

) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(var bind: UserBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val listItemContactsBinding: UserBinding
        val view = LayoutInflater.from(parent.context)
        listItemContactsBinding = DataBindingUtil.inflate(
            view,
            R.layout.user, parent, false
        )
        return ViewHolder(listItemContactsBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind.userName.text = list[position].name
        holder.bind.userDesignation.text = list[position].designation
        holder.bind.userDOB.text = list[position].dob
        val decodedString: ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(list[position].profilePicture)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        holder.bind.userImage.setImageBitmap(decodedByte)
        holder.bind.ivMore.setOnClickListener{
            cellClickListener.onCellClickListener(holder.bind.ivMore, position, list[position])
        }
    }
}