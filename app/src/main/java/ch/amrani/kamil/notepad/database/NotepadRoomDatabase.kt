package ch.amrani.kamil.notepad.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NotepadRoomDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NotepadRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotepadRoomDatabase {
            if (INSTANCE != null) {
                return INSTANCE!!
            } else {
                synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext, NotepadRoomDatabase::class.java, "notepad_database").build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
}