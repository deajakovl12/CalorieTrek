package foi.hr.calorietrek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    Button buttonBack;

    TextView showKg;
    SeekBar seekBarKg;
    
    int min = 0, max = 120, current = 55;

    public void init(){
        buttonBack = (Button) findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        showKg = (TextView) findViewById(R.id.txtKg);
        seekBarKg = (SeekBar) findViewById(R.id.sbKg);

        seekBarKg.setMax(max);
        seekBarKg.setProgress(current - min);
        showKg.setText("" + current);

        seekBarKg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                current = progress + min;
                showKg.setText("" + current);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
