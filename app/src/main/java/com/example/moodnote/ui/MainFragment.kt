package com.example.moodnote.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodnote.R
import com.example.moodnote.data.Note
import com.example.moodnote.databinding.FragmentMainBinding
import com.example.moodnote.utils.NoteAdapter
import com.example.moodnote.utils.OnNoteElementClick
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(), OnNoteElementClick {
    private lateinit var binding : FragmentMainBinding
    private val viewModel: MoodViewModel by viewModels()

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
        val noteAdapter : NoteAdapter = NoteAdapter(this)

        binding.noteList.layoutManager = LinearLayoutManager(requireContext())
        binding.noteList.adapter = noteAdapter

        lifecycleScope.launch {
            viewModel.notes.collectLatest {
                noteAdapter.submitList(it)
                Log.d("NotesFlow", "Updated ${it.size} notes")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRView()
    }

    override fun onClick(note: Note) {
        val direction = MainFragmentDirections.actionMainFragmentToNoteFormFragment(
            noteId = note.id
        )

        val navController = parentFragment?.findNavController()
        navController?.navigate(direction)
    }
}