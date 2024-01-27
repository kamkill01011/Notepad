package ch.amrani.kamil.notepad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import ch.amrani.kamil.notepad.database.NoteEntity
import ch.amrani.kamil.notepad.database.NoteViewModel
import ch.amrani.kamil.notepad.databinding.ActivityMainBinding
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: NoteViewModel
    var lastDeletedNote: NoteEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[NoteViewModel::class.java]
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        (binding.fab).setOnClickListener { editNote() }

        val manager = supportFragmentManager
        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.add(R.id.nav_host_fragment, NotesFragment())
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new_note -> {
                editNote()
                true
            }
            R.id.action_revert -> {
                if (lastDeletedNote != null) viewModel.insert(lastDeletedNote!!)
                lastDeletedNote = null
                true
            }
            R.id.action_export -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "notepad_export.json")
                }
                startActivityForResult(intent, 1)

                true
            }
            R.id.action_import -> {
                startActivityForResult(intent, 1)
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
                startActivityForResult(intent, 2)

                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    if (resultData != null) {
                        resultData.data?.also { uri ->
                            val values = viewModel.notes.value
                            val json = Json.encodeToString(values)
                            val buffer = contentResolver.openOutputStream(uri)?.bufferedWriter()
                            buffer?.write(json)
                            buffer?.flush()
                        }
                    }
                }
                2 -> {
                    if (resultData != null) {
                        resultData.data?.also { uri ->
                            val text = contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                            contentResolver.openInputStream(uri)?.bufferedReader()?.forEachLine { println(it) }
                            val json = Json.decodeFromString<List<NoteEntity>>(text!!)
                            viewModel.deleteAll()
                            json.forEach { viewModel.insert(it) }
                        }
                    }
                }
            }
        }
    }

    fun editNote(note: NoteEntity?=null) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.edit_note_dialog, null)
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        if (note != null) editText.setText(note.text)
        editText.requestFocus()
        builder.setTitle("Edit Note").setView(dialogView)
        builder.setPositiveButton("Save") { _, _ ->
            val text = editText.text.toString()
            if (note != null) viewModel.update(NoteEntity(id=note.id, text=text, lastModify=System.currentTimeMillis()))
            else viewModel.insert(NoteEntity(text=text, lastModify=System.currentTimeMillis()))
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        val dialog = builder.create()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        dialog.setOnDismissListener { imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0) }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = editText.text.toString()
                if (note != null) viewModel.update(NoteEntity(id=note.id, text=text, lastModify=System.currentTimeMillis()))
                else viewModel.insert(NoteEntity(text=text, lastModify=System.currentTimeMillis()))
                dialog.dismiss()
            }
            true
        }

        dialog.show()
    }
}
