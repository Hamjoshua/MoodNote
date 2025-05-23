package com.example.moodnote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.moodnote.R
import com.example.moodnote.data.Note
import com.example.moodnote.databinding.FragmentNoteFormBinding
import com.example.moodnote.utils.toLongDate
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

private const val NOTE_ID = "noteId"


@AndroidEntryPoint
class NoteFormFragment : Fragment() {
    private val viewModel : MoodViewModel by viewModels()
    private lateinit var binding: FragmentNoteFormBinding
    private var noteId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteId = it.getLong(NOTE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinner()
        initButtons()
    }

    private fun initSpinner() {
        lifecycleScope.launch {
            viewModel.emotions.collect {
                binding.emotionSpinner.

            }
        }
    }

    private fun initButtons() {
        val cal: Calendar = Calendar.getInstance()
        binding.dateButton.text = "${cal.get(Calendar.YEAR)}" +
                "/${cal.get(Calendar.MONTH)}" +
                "/${cal.get(Calendar.DAY_OF_MONTH)}" +
                " ${cal.get(Calendar.HOUR)}:" +
                "${cal.get(Calendar.MINUTE)}"

        binding.dateButton.setOnClickListener {
            DateTimePickerHelper(requireContext()) {
                binding.dateButton.text = it
            }.show()
        }

        binding.saveButton.setOnClickListener {
            val date: String = binding.dateButton.text.toString()
            // TODO как брать значения из спиннера
            val emotionId: Int? = null
            val event: String = binding.eventEditText.text.toString()
            val reason: String = binding.reasonEditText.text.toString()

            if (date == "" || event == "" || reason == "" || emotionId == null) {
                Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val formatedDate = date.toLongDate()
                val note: Note = Note(0, emotionId, event, reason, formatedDate)
                viewModel.addOrEditNewNote(note)
            }
        }
    }
}