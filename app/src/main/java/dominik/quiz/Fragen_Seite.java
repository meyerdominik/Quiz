package dominik.quiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Fragen_Seite extends Activity implements View.OnClickListener{

    private TextView time_left;
    private TextView FraTextView;
    private Button A_1;
    private Button A_2;
    private Button A_3;
    private Button A_4;
    private int time_left_int = 30;
    private String richtige_Antwort_fuer_countdown = "";

    private Frage f ;
    private CountDownTimer cdt;

    private boolean b = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragen__seite);

        time_left = (TextView) findViewById(R.id.Time_Left);
        FraTextView = (TextView) findViewById(R.id.Frage);
        A_1 = (Button) findViewById(R.id.Antwort_A);
        A_2 = (Button) findViewById(R.id.Antwort_B);
        A_3 = (Button) findViewById(R.id.Antwort_C);
        A_4 = (Button) findViewById(R.id.Antwort_D);
        A_1.setOnClickListener(this);
        A_2.setOnClickListener(this);
        A_3.setOnClickListener(this);
        A_4.setOnClickListener(this);

        f = dominik.quiz.Frage.nextFrage(this);

        setButton();

        cdt = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                time_left_int--;
                time_left.setText("Übrige Zeit: " + time_left_int + " sec");
            }

            @Override
            public void onFinish() {
                time_left_int = 0;
                AlertDialog alt = new AlertDialog.Builder(Fragen_Seite.this).create();
                alt.setTitle("Zeit ist um!");
                alt.setMessage("Richtige Antwort: " + richtige_Antwort_fuer_countdown);
                alt.setIcon(R.drawable.ic_launcher);
                alt.setCanceledOnTouchOutside(false);
                alt.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                alt.show();
            }
        }.start();


        richtige_Antwort_fuer_countdown = f.getRichtigeAntwort();

        FraTextView.setText(f.getFrage());


    }

    @Override
    public void onPause() {
        super.onPause();

        if (cdt != null) {
            cdt.cancel();
        }
        if(b){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            int i = prefs.getInt("user_" + prefs.getString("loggedinuser", "") + "_falsch", 0);
            i++;
            editor.putInt("user_" + prefs.getString("loggedinuser", "") + "_falsch", i);
            editor.commit();
            editor.apply();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragen__seite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Es gibt keine Einstellungen!", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        cdt.cancel();
        List<Frage.Antwort> antworten = f.getAntworten();
        Integer idx = (Integer) v.getTag();
        Frage.Antwort antwort = antworten.get(idx);
        if (antwort.richtige) {
            richtig();
            v.setBackgroundColor(Color.GREEN);
        }else {
            falsch();
            v.setBackgroundColor(Color.RED);
        }
        }

    public void richtig(){
        b=false;
        AlertDialog alt = new AlertDialog.Builder(Fragen_Seite.this).create();
        alt.setTitle("Richtig!");
        alt.setCanceledOnTouchOutside(false);
        alt.setCancelable(false);
        alt.setIcon(R.drawable.richtig_haken);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        int i = prefs.getInt("user_" + prefs.getString("loggedinuser", "")  + "_richtig" , 0);
        i++;
        editor.putInt("user_" + prefs.getString("loggedinuser", "") + "_richtig", i);
        editor.commit();
        editor.apply();
        alt.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                b=false;
                finish();
            }
        });
        alt.show();
    }
    public void falsch(){

        AlertDialog alt = new AlertDialog.Builder(Fragen_Seite.this).create();
        alt.setTitle("Falsch!");
        alt.setCancelable(false);
        alt.setMessage("'"+richtige_Antwort_fuer_countdown+"' währe richtig gewesen!");
        alt.setCanceledOnTouchOutside(false);
        alt.setIcon(R.drawable.falsch_kreuz);
        alt.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alt.show();
    }
    public void setButton(){

/*
        String AF1 = f.getFalscheAntworten().get(0);
        String AF2 = f.getFalscheAntworten().get(1);
        String AF3 = f.getFalscheAntworten().get(2);
        String AR = f.getRichtigeAntwort();
*/

        List<Frage.Antwort> antworten = f.getAntworten();

        A_1.setText(antworten.get(0).text);
        A_1.setTag(new Integer(0));
        A_2.setText(antworten.get(1).text);
        A_2.setTag(new Integer(1));
        A_3.setText(antworten.get(2).text);
        A_3.setTag(new Integer(2));
        A_4.setText(antworten.get(3).text);
        A_4.setTag(new Integer(3));

    }
    public void onResume(){
        super.onResume();
        A_1.setBackgroundColor(Color.GRAY);
        A_2.setBackgroundColor(Color.GRAY);
        A_3.setBackgroundColor(Color.GRAY);
        A_4.setBackgroundColor(Color.GRAY);
    }
}
