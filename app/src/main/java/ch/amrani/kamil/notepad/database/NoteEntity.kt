package ch.amrani.kamil.notepad.database

import androidx.room.*
import kotlinx.serialization.Serializable

@Entity(tableName = "note")
@Serializable
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val id: Int = 0,
    @ColumnInfo(name="note_text")
    val text : String,
    @ColumnInfo(name="last_modify")
    val lastModify : Long
) {}