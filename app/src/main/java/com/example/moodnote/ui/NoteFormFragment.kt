package com.example.moodnote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.moodnote.R
import com.example.moodnote.data.Note
import com.example.moodnote.databinding.FragmentNoteFormBinding
import com.example.moodnote.utils.toDateString
import com.example.moodnote.utils.toLongDate
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Collections.addAll

private const val NOTE_ID = "noteId"

@AndroidEntryPoint
class NoteFormFragment : Fragment() {
    private val viewModel: MoodViewModel by viewModels()
    private lateinit var binding: FragmentNoteFormBinding
    private val noEmotionSelectedItem = "Эмоция не выбрана"
    private var noteId: Long? = null
    private var note : Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteId = it.getLong(NOTE_ID)
            if(noteId != -1L){
                viewModel.getNote(noteId!!) {
                    note = it
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinner()
        initButtons()
        note?.let {
            initFileds()
        }
    }

    private fun initSpinner() {
        viewModel.emotions.onEach { emotions ->
            val items = mutableListOf(noEmotionSelectedItem).apply {
                addAll(emotions.map { "${it.getEmojiFromUnicode()} ${it.name}" })
            }
            val adapter = ArrayAdapter(
                requireContext(),
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                items
            )
            adapter.setDropDownViewResource(
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item
            )
            binding.emotionSpinner.adapter = adapter
        }.launchIn(lifecycleScope)
    }

    private fun getEmotionFromSpinner(): Int? {
        val position = binding.emotionSpinner.selectedItemPosition

        if (position == 0) {
            return null
        } else {
            return position - 1
        }
    }

    private fun initFileds(){
        binding.eventEditText.setText(note!!.event)
        binding.emotionSpinner.setSelection(note!!.emotionId + 1)
        binding.reasonEditText.setText(note!!.reason)
        binding.dateButton.text = note!!.date.toDateString()
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
            saveNote()
        }
    }

    private fun saveNote() {
        val date: String = binding.dateButton.text.toString()
        val emotionId: Int? = getEmotionFromSpinner()
        val event: String = binding.eventEditText.text.toString()
        val reason: String = binding.reasonEditText.text.toString()

        if (date == "" || event == "" || reason == "" || emotionId == null) {
            Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_SHORT)
                .show()
        } else {
            val formatedDate = date.toLongDate()
            if(note == null){
                note = Note(0, emotionId, event, reason, formatedDate!!)
            }

            viewModel.addOrEditNewNote(note!!)
            Toast.makeText(requireContext(), "Новая запись создана", Toast.LENGTH_SHORT).show()
            toMainFragment()
        }
    }

    private fun toMainFragment(){
        val direction = NoteFormFragmentDirections.actionNoteFormFragmentToMainFragment()

        val navController = parentFragment?.findNavController()
        navController?.navigate(direction)
    }
}