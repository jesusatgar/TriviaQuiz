package com.example.chus.aplicaciontrivial.interfaz;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chus.aplicaciontrivial.R;
import com.example.chus.aplicaciontrivial.logica.Puntuacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityFinal extends AppCompatActivity {

    String nickname = "";
    int puntos = 0;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        puntos = getIntent().getIntExtra("puntos",0);

        TextView textoPuntos = (TextView) findViewById(R.id.textViewPuntosFinal);
        textoPuntos.setText(String.valueOf(puntos));
    }

    public void subirPuntuacion(View view){
        EditText edit = (EditText) findViewById(R.id.editTextUser);
        String nombreUsuario = edit.getText().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(!nombreUsuario.equals("")){
            Puntuacion puntuacion = new Puntuacion(nombreUsuario,puntos);
            mDatabase.child("puntuaciones").child("User-" + nombreUsuario).setValue(puntuacion);
            Intent i = new Intent(this,ActivityRanking.class);
            startActivity(i);
        }else if(nombreUsuario.contains(".") || nombreUsuario.contains("#") || nombreUsuario.contains("$") || nombreUsuario.contains("[") || nombreUsuario.contains("]")){
            Snackbar.make(view,"No se permiten símbolos",Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(view,"Introduce un nombre de usuario",Snackbar.LENGTH_SHORT).show();
        }
    }

    public void Compartir(View view)
    {
       /* ACTION_SEND: Deliver some data to someone else.
        createChooser (Intent target, CharSequence title): Here, target- The Intent that the user will be selecting an activity to perform.
            title- Optional title that will be displayed in the chooser.
        Intent.EXTRA_TEXT: A constant CharSequence that is associated with the Intent, used with ACTION_SEND to supply the literal data to be sent.
        */
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"He conseguido una puntuación de " + puntos + " en la app TriviaQuiz!");
        sendIntent.setType("text/plain");
        Intent.createChooser(sendIntent,"Compartir en");
        startActivity(sendIntent);
    }

    public void Reiniciar(View view){
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
    }

    public void NuevaPartida(View view){
        Intent i = new Intent(this, ActivityPregunta.class );
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

}
