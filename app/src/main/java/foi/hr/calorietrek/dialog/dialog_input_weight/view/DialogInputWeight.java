package foi.hr.calorietrek.dialog.dialog_input_weight.view;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.dialog.dialog_input_weight.controller.DialogInputController;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.ui.login.view.LoginActivity;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;

public class DialogInputWeight extends AppCompatDialogFragment implements IDialogInputWeight {

    public EditText inputWeight;
    public SeekBar selectedCargo;
    public TextView showCargoWeight;

    private DialogInputWeightListener listener;

    DialogInputController dialogInputController;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_weight_layout, null);

        dialogInputController = new DialogInputController(getContext());


        inputWeight = view.findViewById(R.id.inputWeight);
        selectedCargo = view.findViewById(R.id.seekbarCargo);
        showCargoWeight = view.findViewById(R.id.txtCargoProgress);

        seekbarCargoChanged();

        mBuilder.setView(view)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        dismiss();
                    }
                })
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        if (inputWeight.getText().toString().isEmpty()){
                            Toast.makeText(getContext(), "Input weight is required!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int value = selectedCargo.getProgress();
                            listener.applyCargo(value);

                            String weight = inputWeight.getText().toString();
                            changeWeight(weight);
                        }
                    }
                });

        return mBuilder.create();
    }

    public void changeWeight(String weight){
        boolean isChanged = dialogInputController.changedWeight(weight);

        if (isChanged) {
            Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), R.string.not_saved, Toast.LENGTH_SHORT).show();
        }
    }

    public void seekbarCargoChanged(){
        selectedCargo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                showCargoWeight.setText(String.format("%2d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogInputWeightListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
            "Must implement DialogInputWeightListener");
        }
    }

    public interface DialogInputWeightListener{
        void applyCargo(int cargo);
    }
}
