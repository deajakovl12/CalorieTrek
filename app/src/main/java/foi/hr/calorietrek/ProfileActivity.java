package foi.hr.calorietrek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txtKg) TextView showKg;
    @BindView(R.id.sbKg) SeekBar seekBarKg;
    
    int min = 0, max = 120, current = 55;

    //Toolbar
    public void initToolbar(){
        ButterKnife.bind(this);
        toolbar.setTitle("Profile");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.iconback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Toast.makeText(ProfileActivity.this, "Click on toolbar!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Prikaz tezine u kg
    public void showWeight(){
        ButterKnife.bind(this);

        seekBarKg.setMax(max);
        seekBarKg.setProgress(current - min);
        showKg.setText("" + current + " kg");

        seekBarKg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                current = progress + min;
                showKg.setText("" + current + " kg");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        showWeight();
    }
}
