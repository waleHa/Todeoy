package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    //executed the first time
    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,//lazy
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            //db operations

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Pay for Hisham", completed = true))
                dao.insert(Task("Go to the dentist", important = true))
                dao.insert(Task("Call Rogers"))
                dao.insert(Task("Book a Flight",important = true))
            }


        }
    }
}