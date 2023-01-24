package com.dt.learning.myapp2.data

import android.os.Parcelable
import android.os.SystemClock
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val description: String = "Default Description",
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id : Int = 0
) : Parcelable {

    val createDateFormatted : String
        get() = DateFormat.getDateTimeInstance().format(created)
}