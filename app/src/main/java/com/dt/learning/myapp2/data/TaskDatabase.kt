package com.dt.learning.myapp2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dt.learning.myapp2.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao() : TaskDao


    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //db operations
            val dao = database.get().taskDao()

            applicationScope.launch {

                dao.insert(Task("Buy Groceries", "Pick up milk, bread, eggs, and vegetables at the store"))
                dao.insert(Task("Do Laundry", "Wash and fold all dirty clothes"))
                dao.insert(Task("Pay Rent", "Transfer rent payment to landlord via online banking"))
                dao.insert(Task("Book Doctor's Appointment", "Schedule a check-up with primary care physician"))
                dao.insert(Task("Send Birthday Gift", "Purchase and send a gift to friend's birthday"))
                dao.insert(Task("Return Library Books", "Drop off overdue books at the library"))
                dao.insert(Task("File Taxes", "Complete and submit federal and state tax forms"))
                dao.insert(Task("Renew Driver's License", "Make an appointment at the DMV to renew driver's license"))
                dao.insert(Task("Plan Vacation", "Research and book flights, hotels, and activities for upcoming trip"))
                dao.insert(Task("Mow Lawn", "Cut the grass in the front and back yards"))

            }

        }
    }

}
