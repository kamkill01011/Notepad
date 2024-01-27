package ch.amrani.kamil.notepad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.amrani.kamil.notepad.database.NoteEntity

class NoteListAdapter internal constructor(context: Context) : RecyclerView.Adapter<NoteListAdapter.RecipeViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<NoteEntity>()
    private val main = (context as MainActivity)

    override fun getItemCount() = notes.size

    internal fun setNotes(notes: List<NoteEntity>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    internal fun getNote(position: Int) : NoteEntity {
        return this.notes[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = inflater.inflate(R.layout.note_item, parent, false)
        return RecipeViewHolder(itemView)
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteItemView: TextView = itemView.findViewById(R.id.noteTextView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val current = notes[position]
        holder.noteItemView.text = current.text
        holder.itemView.setOnClickListener { main.editNote(current) }
    }
}