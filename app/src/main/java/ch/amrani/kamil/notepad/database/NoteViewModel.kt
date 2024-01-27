package ch.amrani.kamil.notepad.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(application : Application) : AndroidViewModel(application) {

    private val repository : NoteRepository
    val notes: LiveData<List<NoteEntity>>

    init {
        val dao = NotepadRoomDatabase.getDatabase(application, viewModelScope).noteDao()
        repository = NoteRepository(dao)
        notes = repository.notes
    }

    fun insert(note: NoteEntity) = viewModelScope.launch {
        repository.insert(note)
    }

    fun update(note: NoteEntity) = viewModelScope.launch {
        repository.update(note)
    }

    fun delete(note: NoteEntity) = viewModelScope.launch {
        repository.delete(note)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}