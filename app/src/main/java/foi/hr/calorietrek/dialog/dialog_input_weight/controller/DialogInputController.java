package foi.hr.calorietrek.dialog.dialog_input_weight.controller;

import android.content.Context;

import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;

public class DialogInputController implements IDialogInputControler{

    public DbHelper Instance;

    public DialogInputController(Context context){
        Instance = DbHelper.getInstance(context);
    }

    public boolean changedWeight(String weight){
        boolean isChanged = Instance.updateWeight(CurrentUser.personName, weight);
        return isChanged;
    }
}
