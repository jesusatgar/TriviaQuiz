package com.example.chus.aplicaciontrivial.interfaz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.chus.aplicaciontrivial.R;
import com.example.chus.aplicaciontrivial.logica.Puntuacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRanking extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView mListView;
    ArrayList<Puntuacion> puntuacionesLista;

    // Read from the database
    ValueEventListener listenerPuntuaciones = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<Puntuacion> list = new ArrayList<Puntuacion>();
            for (DataSnapshot child: dataSnapshot.child("puntuaciones").getChildren()) {
                list.add(child.getValue(Puntuacion.class));
            }
            puntuacionesLista = list;


            mListView = (ListView) findViewById (R.id.puntuaciones_list_view);
            String[] listItems = new String[puntuacionesLista.size()];
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            Collections.sort(puntuacionesLista);

            for(int i = 0; i < puntuacionesLista.size(); i++){
                Puntuacion p = puntuacionesLista.get(i);
                Map<String, String> dato = new HashMap<String, String>(2);
                dato.put("Linea1", p.getNombreUsuario());
                dato.put("Linea2",String.valueOf(p.getPuntos()));
                data.add(dato);
            }

            SimpleAdapter adapter = new SimpleAdapter(ActivityRanking.this, data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"Linea1", "Linea2" },
                    new int[] {android.R.id.text1, android.R.id.text2 });

            mListView.setAdapter(adapter);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Toast.makeText(ActivityRanking.this,"error",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(listenerPuntuaciones);
        mensajeConexion();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void mensajeConexion(){
        if (!isOnline()){
            new AlertDialog.Builder(this).setTitle("Sin conexión").setMessage("Tu dispositivo no tiene conexión a internet." +
                    " Las puntuaciones serán actualizadas en cuanto restablezcas la conexión.").setIcon(android.R.drawable.ic_dialog_alert).setCancelable(true).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
