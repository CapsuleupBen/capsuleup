package com.example.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class business_DialogGetDescription extends AppCompatDialogFragment {
    // Used when Business Client edits area's description (Opens Dialog)

    private EditText etDescription;         // Description Edit text
    private ExampleDialogListener listener; // Example dialog listener

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Dialog creationg
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.business_dialog_get_description, null);
        builder.setView(view)
                .setTitle("Add Description")
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String description = etDescription.getText().toString();
                        listener.applyTexts(description);
                    }
                });

        etDescription = view.findViewById(R.id.etDescription);
        etDescription.setText(business_area_profile.tvAreaDescription.getText().toString());
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        // Attaching the dialog to the current context
        super.onAttach(context);
        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        // Handle result of the dialog
        void applyTexts(String description);
    }
}