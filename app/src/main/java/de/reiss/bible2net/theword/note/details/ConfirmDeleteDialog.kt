package de.reiss.bible2net.theword.note.details

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import de.reiss.bible2net.theword.R

class ConfirmDeleteDialog : DialogFragment() {

    companion object {

        fun createInstance() = ConfirmDeleteDialog()

    }

    interface Listener {

        fun onDeleteConfirmed()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = activity!!.let { activity ->
        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.confirm_delete_dialog_title))
                .setCancelable(true)
                .setPositiveButton(activity.getString(R.string.confirm_delete_dialog_ok)) { _, _ ->
                    (activity as? Listener?)?.onDeleteConfirmed()
                    dismiss()
                }
                .setNegativeButton(activity.getString(R.string.dialog_cancel)) { _, _ ->
                    dismiss()
                }
                .create()
    }

}