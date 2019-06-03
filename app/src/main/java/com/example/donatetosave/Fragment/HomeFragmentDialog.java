package com.example.donatetosave.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.donatetosave.R;

public class HomeFragmentDialog extends AppCompatDialogFragment {
    private EditText text;
    private HomeFragmentDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater= getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_home_dialog,null);
        final String type = getArguments().getString("type");


        builder.setView(view).
                setTitle(Html.fromHtml("Edit your new "+"<b>"+type+"</b>")).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result= text.getText().toString();
                listener.applyText(result,type);
            }
        }).setCancelable(false);
        text = view.findViewById(R.id.fragment_home_edit);
        text.setHint(Html.fromHtml("Enter your new "+"<b>"+type+"</b>"));
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (HomeFragmentDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " extend Listener");
        }
    }

    public interface HomeFragmentDialogListener{
        void applyText(String result,String type);
    }
}
