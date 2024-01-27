package ch.amrani.kamil.notepad.database

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {
    val notes : LiveData<List<NoteEntity>> = noteDao.getAll()

    suspend fun insert(note: NoteEntity) {
        noteDao.insert(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.delete(note)
    }

    suspend fun deleteAll() {
        noteDao.deleteAll()
    }
}