package com.example.moodnote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moodnote.R
import com.example.moodnote.databinding.FragmentNoteFormBinding
import java.util.Calendar

private const val NOTE_ID = "noteId"

class NoteFormFragment : Fragment() {
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

        initButtons()
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
    }
}