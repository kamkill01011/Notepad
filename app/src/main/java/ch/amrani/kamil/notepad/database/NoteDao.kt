package ch.amrani.kamil.notepad.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.*

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM note")
    fun getAll(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE note_id = :id")
    fun getById(id: Long): LiveData<NoteEntity>

    @Query("DELETE FROM note")
    suspend fun deleteAll()
}