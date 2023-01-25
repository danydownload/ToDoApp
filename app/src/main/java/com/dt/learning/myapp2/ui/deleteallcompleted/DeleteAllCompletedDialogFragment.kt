package com.dt.learning.myapp2.ui.deleteallcompleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // va messo su qualsiasi cosa abbia una sua viewmodel
class DeleteAllCompletedDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you really want to delete all completed tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                // passiamo il listener. Vogliamo chiamare il viewModel per fare la delete
                viewModel.onConfirmClick()
            }
            .create()
}