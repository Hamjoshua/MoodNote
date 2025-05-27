package com.example.moodnote.ui

import android.os.Bundle
import android.security.ConfirmationAlreadyPresentingException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodnote.R
import com.example.moodnote.data.Note
import com.example.moodnote.databinding.FragmentMainBinding
import com.example.moodnote.utils.ConfirmationDialog
import com.example.moodnote.utils.NoteAdapter
import com.example.moodnote.utils.OnNoteElementClick
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(), OnNoteElementClick {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MoodViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun initRView() {
        val noteAdapter: NoteAdapter = NoteAdapter(this)

        binding.noteList.layoutManager = LinearLayoutManager(requireContext())
        binding.noteList.adapter = noteAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notes.collect {
                    noteAdapter.submitList(it)
                    Log.d("NotesFlow", "Updated ${it.size} notes")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRView()
        initButtons()
    }

    private fun initButtons() {
        binding.toNoteForm.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.noteFormFragment)
        }
    }

    override fun onClick(note: Note) {
        val navController = parentFragment?.findNavController()
        navController?.navigate(R.id.noteFormFragment, bundleOf("noteId" to note.id))
    }

    override fun onRemoveClick(note: Note) {
        ConfirmationDialog("Вы уверены, что хотите удалить эту запись?") {
            viewModel.removeNote(note)
        }.show(parentFragmentManager, "CONFIRMATION_DIALOG")
    }
}