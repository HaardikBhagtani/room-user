package codes.haardikbhagtani.userformroom.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "designation") val designation: String?,
    @ColumnInfo(name = "dob") val dob: String?,
    @ColumnInfo(name = "profile_picture") val profilePicture: String
) : java.io.Serializable