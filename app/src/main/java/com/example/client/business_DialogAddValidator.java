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

public class business_DialogAddValidator extends AppCompatDialogFragment {
    // Used when Business Client creates area - and adds validators for the area (Opens Dialog)

    private EditText etScannerPhone;         // Edit Text Scanner - Phone
    private EditText etScannerName;          // Edit Text Scanner - name
    private DialogAddValidatorListener listener;  // Listener for the dialog

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Dialog creation
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.business_dialog_add_validator, null);
        builder.setView(view)
                .setTitle("Add validator")
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = etScannerPhone.getText().toString();
                        String password = etScannerName.getText().toString();
                        listener.applyTexts(username, password);
                    }
                });
        etScannerPhone = view.findViewById(R.id.etDescription);
        etScannerName = view.findViewById(R.id.etScannerName);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        // Attaching the dialog to the context
        super.onAttach(context);
        try {
            listener = (DialogAddValidatorListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogAddValidatorListener");
        }
    }

    public interface DialogAddValidatorListener {
        // When dialog finish
        void applyTexts(String username, String password);
    }
}