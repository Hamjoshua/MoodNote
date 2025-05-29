package com.example.moodnote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.moodnote.data.Note
import com.example.moodnote.databinding.FragmentNoteFormBinding
import com.example.moodnote.utils.ConfirmationDialog
import com.example.moodnote.utils.toDateString
import com.example.moodnote.utils.toLongDate
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Calendar

private const val NOTE_ID = "noteId"

@AndroidEntryPoint
class NoteFormFragment : Fragment() {
    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var binding: FragmentNoteFormBinding
    private val noEmotionSelectedItem = "Эмоция не выбрана"
    private var noteId: Long? = null
    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteId = it.getLong(NOTE_ID)
            if (noteId != -1L) {
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
            return position
        }
    }

    private fun initFileds() {
        binding.eventEditText.setText(note!!.event)
        binding.reasonEditText.setText(note!!.reason)
        binding.dateButton.text = note!!.date.toDateString()
        // Загрузка эмоции после того, как она применилась
        binding.emotionSpinner.post {
            binding.emotionSpinner.setSelection(note!!.emotionId)
        }
    }

    private fun initButtons() {
        val cal: Calendar = Calendar.getInstance()
        binding.dateButton.text = "${cal.get(Calendar.YEAR)}" +
                "/${(cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}" +
                "/${cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}" +
                " ${cal.get(Calendar.HOUR).toString().padStart(2, '0')}:" +
                "${cal.get(Calendar.MINUTE).toString().padStart(2, '0')}"

        binding.dateButton.setOnClickListener {
            DateTimePickerHelper(requireContext()) {
                binding.dateButton.text = it
            }.show()
        }

        binding.saveButton.setOnClickListener {
            saveNote()
        }

        if (note == null) {
            binding.removeNoteElementButton.isVisible = false
        } else {
            binding.removeNoteElementButton.setOnClickListener {
                removeNote()
            }
        }
    }

    private fun saveNote() {
        val date: String = binding.dateButton.text.toString()
        val emotionId: Int? = getEmotionFromSpinner()
        val event: String = binding.eventEditText.text.toString()
        val reason: String = binding.reasonEditText.text.toString()
        var completeToastText = "Новая запись создана"

        if (date == "" || event == "" || reason == "" || emotionId == null) {
            Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_SHORT)
                .show()
        } else {
            val formatedDate = date.toLongDate()
            if (note == null) {
                note = Note(0, emotionId, event, reason, formatedDate!!)
            } else {
                completeToastText = "Запись успешно редактирована"
                note!!.emotionId = emotionId
                note!!.event = event
                note!!.date = formatedDate!!
                note!!.reason = reason
            }

            viewModel.addOrEditNewNote(note!!)
            Toast.makeText(requireContext(), completeToastText, Toast.LENGTH_SHORT).show()
            toMainFragment()
        }
    }

    private fun removeNote() {
        ConfirmationDialog("Вы уверены, что хотите удалить эту запись?") {
            viewModel.removeNote(note!!)
            toMainFragment()
        }.show(parentFragmentManager, "CONFIRMATION_DIALOG")
    }

    private fun toMainFragment() {
        val navController = parentFragment?.findNavController()
        navController?.popBackStack()

    }
}