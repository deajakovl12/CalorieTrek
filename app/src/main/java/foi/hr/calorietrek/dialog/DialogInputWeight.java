package foi.hr.calorietrek.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.ui.login.view.LoginActivity;

public class DialogInputWeight extends AppCompatDialogFragment {

    public EditText inputWeight;

    DbHelper instance;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_weight_layout, null);

        instance = DbHelper.getInstance(getContext());

        inputWeight = view.findViewById(R.id.inputWeight);

        mBuilder.setView(view)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){

                    }
                })
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        String weight = inputWeight.getText().toString();

                        changeWeight(weight);
                    }
                });

        return mBuilder.create();
    }

    public void changeWeight(String weight){
        boolean isChanged = instance.updateWeight(CurrentUser.personName, weight);

        if (isChanged) {
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
