package ch.amrani.kamil.notepad

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class NotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_notes, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview_notes)
        val adapter = NoteListAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val viewModel = (activity as MainActivity).viewModel
        viewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            notes?.let { adapter.setNotes(it.sortedBy { n -> -n.lastModify }) }
        })

        val leftCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val selected = adapter.getNote(viewHolder.adapterPosition)
                (activity as MainActivity).lastDeletedNote = selected
                viewModel.delete(selected)
            }
        }
        val leftHelper = ItemTouchHelper(leftCallback)
        leftHelper.attachToRecyclerView(recyclerView)

        return rootView
    }
}
