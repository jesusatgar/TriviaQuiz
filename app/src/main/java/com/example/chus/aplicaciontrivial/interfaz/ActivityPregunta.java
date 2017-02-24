package com.example.chus.aplicaciontrivial.interfaz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chus.aplicaciontrivial.R;
import com.example.chus.aplicaciontrivial.logica.Pregunta;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ActivityPregunta extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int NUMERO_PREGUNTAS_GENERALES = 10;
    private static final int NUMERO_PREGUNTAS_UBICACION = 5;
    ArrayList<Pregunta> listaPreguntas = new ArrayList<>();
    ArrayList<Pregunta> listaPreguntasGenerales = new ArrayList<>();
    ArrayList<Pregunta> listaPreguntasUbicacion = new ArrayList<>();
    private int preguntasRealizadas = 0; //Counter
    Pregunta pregunta = null;
    MediaPlayer mediaPlayer;
    GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private LocationRequest mLocationRequest;
    protected Location estoyAqui;
    private int puntos;
    private View mVista;
    //Sonidos del SoundPool
    private SoundPool sp;
    public int correcto=0;
    public int incorrecto=0;
    Geocoder geocoder = null;
    private int numeroPreguntasGenerales = 0;
    private int numeroPreguntasUbicacion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        cargarSonidos();
        mVista = findViewById(R.id.activity_pregunta);

        //Geolocalizacion
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Boton para el microfono
        FloatingActionButton microfono = (FloatingActionButton) findViewById(R.id.floatingActionButtonVoz);

        microfono.setOnClickListener(new View.OnClickListener(){public void onClick(View v){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hable ahora");
            startActivityForResult(intent,0);
        }
        });



        pedirPermisos();
        crearLocationRequest();
        addPreguntas();
        cambiarPregunta();
    }

    private void addPreguntas() {
        addPreguntasGenerales();
        for (int i = 0; i < NUMERO_PREGUNTAS_GENERALES;i++){
            Random rand = new Random();
            int r = rand.nextInt(listaPreguntasGenerales.size());
            listaPreguntas.add(listaPreguntasGenerales.get(r));
            // Borrar de la lista para que no salga otra vez
            listaPreguntasGenerales.remove(r);
            numeroPreguntasGenerales++;
        }
    }

    private void cargarSonidos() {
        //El volumen
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //Sonido de respuesta correcta e incorrecta
        sp = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        correcto= sp.load(this, R.raw.correcto,1);
        incorrecto= sp.load(this,R.raw.incorrecto,1);
    }

    private void addPreguntasGenerales() {

        listaPreguntasGenerales.add(new Pregunta("¿Cómo se pronuncia pepino en inglés?","cucumber",false,true,new String[]{"cucumber","Cucumber","wqeqb","csadcc"},null,false,null));
        listaPreguntasGenerales.add(new Pregunta("¿Cómo se pronuncia pulpo en inglés?","octopus",false,true,new String[]{"octopus","Octopus","wqeqb","csadcc"},null,false,null));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál es la traducción de 'octopus'?","pulpo",false,true,new String[]{"pulpo","Pulpo","wqeqb","csadcc"},null,false,null));
        listaPreguntasGenerales.add(new Pregunta("¿Qué significa 'hummingbird' en español?","colibrí",false,true,new String[]{"colibrí","Colibrí","wqeqb","csadcc"},null,false,null));
        listaPreguntasGenerales.add(new Pregunta("¿Cómo se pronuncia perro en inglés?","dog",false,true,new String[]{"dog","aasdhknj","wqeqb","csadcc"},null,false,null));

        listaPreguntasGenerales.add(new Pregunta("¿Cuándo escuchamos este sonido?", "Al apagar windows xp",true,false, new String[]{"Al apagar windows xp", "Al apagar ubuntu", "Error en mac os", "Llamada de skype"},"windows",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál es esta canción?", "Careless Whisper",true,false, new String[]{"The Saxo Guy", "Smooth", "Jazz for your soul", "Careless Whisper"},"carelesswhisper",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál es esta canción?", "September",true,false, new String[]{"September", "November", "Friday", "Celebration"},"september",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién es el productor de esta conocida base de rap?", "Dr Dre",true,false, new String[]{"Snoop Dogg", "Tupac", "Ice Cube", "Dr Dre"},"still",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿De qué álbum de Michael Jackson es esta canción?", "Thriller",true,false, new String[]{"Off The Wall", "Thriller", "Billie Jean", "Bad"},"billie",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Cómo se llama esta canción?", "Baby one more time",true,false, new String[]{"Baby one more time", "Oops, i did it again", "Toxic", "Umbrella"},"britney",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál es el título de esta canción de Beyoncé?", "Crazy In Love",true,false, new String[]{"Crazy In Love", "Trumpets", "Move your body", "Halo"},"beyonce",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿De qué país son los creadores de esta canción?", "Gran Bretaña",true,false, new String[]{"Estados Unidos", "Gran Bretaña", "España", "Alemania"},"blur",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál es esta canción?", "Sweet child o' mine",true,false, new String[]{"Back in Black", "Thunderstruck", "Highway to Hell", "Sweet child o' mine"},"guns",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién colabora junto a Daft Punk en esta canción?", "Pharrell Williams",true,false, new String[]{"Michael Jackson", "David Bowie", "Tina Turner", "Pharrell Williams"},"lucky",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál es esta canción?", "Sultans of Swing",true,false, new String[]{"Brothers in Arms", "Sultans of Swing", "Romeo and Juliet", "Money for nothing"},"straits",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién es el autor de esta pieza?", "Beethoven",true,false, new String[]{"Mozart", "Beethoven", "Bach", "Haydn"},"beethoven",false,"altavoz"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién es el autor de esta pieza?", "Satie",true,false, new String[]{"Satie", "Chopin", "Wagner", "Debussy"},"satie",false,"altavoz"));

        listaPreguntasGenerales.add(new Pregunta("¿Quién fue presidente de EEUU de 2001 a 2009?", "George W. Bush",false,false, new String[]{"Ronald Reagan", "Donald Trump", "Franklin D. Roosevelt", "George W. Bush"},null,false,"rushmore"));
        listaPreguntasGenerales.add(new Pregunta("¿En qué año se promulgó la Constitución Española?", "1978",false,false, new String[]{"1977", "1978", "1936", "1950"},null,false,"espana"));
        listaPreguntasGenerales.add(new Pregunta("¿Cómo se dice 'caballo' en inglés?", "Horse",false,false, new String[]{"Cockatoo", "Hole", "Dog", "Horse"},null,false,"caballo"));
        listaPreguntasGenerales.add(new Pregunta("¿Cuál de estos objetos utilizó Michael Jackson?", "Un jetpack",false,false, new String[]{"Un jetpack", "Un taladro", "Una piscina hinchable", "Una nariz de payaso"},null,false,"michael"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién es Voldemort?", "El malo de harry potter",false,false, new String[]{"El malo de star wars", "El malo de star trek", "El malo de harry potter", "El malo de el señor de los anillos"},null,false,"voldemort"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién es el futbolista mejor pagado del mundo?", "Leo Messi",false,false, new String[]{"Cristiano Ronaldo", "Leo Messi", "Cárlos Tévez", "Néstor 'Colibrí' Coratella"},null,false,"futbolista"));
        listaPreguntasGenerales.add(new Pregunta("¿En qué año murió el actor de Mr.Bean?", "Ninguna de las anteriores",false,false, new String[]{"1998", "2003", "2004", "Ninguna de las anteriores"},null,false,"mrbean"));
        listaPreguntasGenerales.add(new Pregunta("¿Qué es un ewok?","Una criatura de 'Star Wars'",false,false,new String[]{"Una criatura de 'Star Wars'","Un libro digital","Un jefe indio","Un tipo de teléfono"},null,false,"interrogacion"));
        listaPreguntasGenerales.add(new Pregunta("¿Qué máxima es propia del racionalismo?","Cogito ergo sum",false,false,new String[]{"Errare humanum est","Veni vidi vici","Pienso que existo","Cogito ergo sum"},null,false,"racionalismo"));
        listaPreguntasGenerales.add(new Pregunta("Banksy es un famoso...","Grafitero anónimo",false,false,new String[]{"Actor de cine","Pintor polaco","Grafitero anónimo","Banco online"},null,false,"interrogacion"));
        listaPreguntasGenerales.add(new Pregunta("¿Qué es el Texas Hold'em?","Una variante del póker",false,false,new String[]{"Una variante del póker","Un campo petrolífero","Un legendario wéstern","Cadena de restaurantes"},null,false,"texas"));
        listaPreguntasGenerales.add(new Pregunta("China es un régimen...","Comunista",false,false,new String[]{"Capitalista","Maoísta","Comunista","Anarquista"},null,false,"china"));
        listaPreguntasGenerales.add(new Pregunta("¿Quién fue el Atahualpa Yupanqui?","Un poeta argentino",false,false,new String[]{"Un poeta argentino","El último rey inca","El sobrenombre inca de Pizarro","Un cantante peruano"},null,false,"interrogacion"));
    }

    private void addPreguntasCiudad(String ciudad){
        if(ciudad.equals("Gijón")){
            listaPreguntasUbicacion.add(new Pregunta("¿Se encuentra la Universidad Laboral en tu ciudad actual?","Gijón",false,false,new String[]{"Sí","No","Es de una película","No existe"},null,true,"laboral"));
            listaPreguntasUbicacion.add(new Pregunta("¿Estás cerca de la Facultad de Informática?","Oviedo",false,false,new String[]{"No","Sí","Mejor no acercarse","Todas son correctas"},null,true,"infor"));
            listaPreguntasUbicacion.add(new Pregunta("Antes de ser el ayuntamiento, ¿qué era este edificio?","La rula",false,false,new String[]{"Una biblioteca","La rula","Casa de un noble","Siempre fue el ayuntamiento"},null,false,"ayuntamiento"));
            listaPreguntasUbicacion.add(new Pregunta("¿En qué año nació Jovellanos?","1744",false,false,new String[]{"1744","1840","1780","1821"},null,false,"jovellanos"));
            listaPreguntasUbicacion.add(new Pregunta("¿En qué división está el Sporting de Gijón?","Primera",false,false,new String[]{"Primera","Segunda","Segunda B","Tercera"},null,false,"sporting"));
            listaPreguntasUbicacion.add(new Pregunta("¿Qué representa este monumento?","A una madre",false,false,new String[]{"A una madre","A una loca","A una hija","A un monstruo"},null,false,"lloca"));
            listaPreguntasUbicacion.add(new Pregunta("¿Qué era el edificio que ahora es Coporación Dermoestética?","Un teatro",false,false,new String[]{"Un cine","Un teatro","Una biblioteca","Un hospital infantil"},null,false,"albeniz"));
            listaPreguntasUbicacion.add(new Pregunta("¿Qué conocida cadena ocupa el edificio del antiguo Cine Robledo?","Pull And Bear",false,false,new String[]{"McDonald's","Primark","Burger King","Pull And Bear"},null,false,"cine"));
            listaPreguntasUbicacion.add(new Pregunta("¿Cómo se llama este parque?","Isabel la Católica",false,false,new String[]{"Begoña","Isabel la Católica","Los Pericones","Parque Fluvial"},null,false,"isabel"));
            listaPreguntasUbicacion.add(new Pregunta("¿Cómo se llama este monumento?","Elogio del horizonte",false,false,new String[]{"Váter de King Kong","Elogio del horizonte","Monumento al viento","La dignidad"},null,false,"elogio"));
        }
        if(ciudad.equals("Oviedo")){
            listaPreguntasUbicacion.add(new Pregunta("¿Se encuentra la Universidad Laboral en tu ciudad actual?","Gijón",false,false,new String[]{"Sí","No","Es de una película","No existe"},null,true,"laboral"));
            listaPreguntasUbicacion.add(new Pregunta("¿Estás cerca de la Facultad de Informática?","Oviedo",false,false,new String[]{"No","Sí","Mejor no acercarse","Todas son correctas"},null,true,"infor"));
            listaPreguntasUbicacion.add(new Pregunta("¿Qué famoso director ha visitado Oviedo?","Woody Allen",false,false,new String[]{"Cristopher Nolan","JJ Abrams","David Fincher","Woody Allen"},null,false,"oviedo"));
            listaPreguntasUbicacion.add(new Pregunta("¿En qué año se fundó la Universidad de Oviedo?","1608",false,false,new String[]{"1608","1721","1750","1921"},null,false,"uniovi"));
            listaPreguntasUbicacion.add(new Pregunta("¿Por qué nombre se conoce a Oviedo en La Regenta?","Vetusta",false,false,new String[]{"Ciudad Verde","Vetusta","Carbayona","Regenta"},null,false,"regenta"));
            listaPreguntasUbicacion.add(new Pregunta("¿Cada cuánto suena el himno de Asturias en la Escandalera?","Cada hora",false,false,new String[]{"Cada día","Cada hora","Cada semana","Cada 15 minutos"},null,false,"escandalera"));
            listaPreguntasUbicacion.add(new Pregunta("¿Quién diseñó el centro comercial Modoo?","Santiago Calatrava",false,false,new String[]{"Santiago Calatrava","Óscar Niemeyer","Eduardo Chillida","Pablo Picasso"},null,false,"modoo"));
            listaPreguntasUbicacion.add(new Pregunta("¿Cómo se llama este parque?","Parque de Invierno",false,false,new String[]{"Parque de Invierno","Parque San Francisco","Parque del Oeste","Parque Juan Mata"},null,false,"invierno"));
            listaPreguntasUbicacion.add(new Pregunta("¿Cuál es el nombre por el que se conoce a los ovetenses?","Carbayones",false,false,new String[]{"Pixuetos","Carbayones","Moscones","Troyeros"},null,false,"gente"));
            listaPreguntasUbicacion.add(new Pregunta("¿Quién es el entrenador del Real Oviedo?","Fernando Hierro",false,false,new String[]{"Fernando Hierro","Míchel","El mono burgos","Quique Sánchez Flores"},null,false,"realoviedo"));
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.connect();
    }



    public void compruebaRespuesta(View v) {
        switch (v.getId()) {
            case (R.id.BotonRespuesta1):
                Button boton1 = (Button) findViewById(R.id.BotonRespuesta1);
                String texto1 = boton1.getText().toString();

                if(pregunta.isLocalizacion() && (texto1.equals("Sí"))) {
                    texto1 = comprobarLocalizacion();

                }else if (pregunta.isLocalizacion() && (texto1.equals("No"))){
                    texto1 = comprobarLocalizacion();
                    //Aquí hacemos lo contrario si la respuesta es "no"
                    //Si dice que no pero es falso, la pregunta sale como mal y al revés
                    if (!pregunta.esRespuestaValida(texto1)) {
                        puntos +=10;
                        terminarPregunta();
                        sp.play(correcto, 1, 1, 0, 0, 1);
                    }else{
                        puntos -=5;
                        terminarPregunta();
                        sp.play(incorrecto, 1, 1, 0, 0, 1);
                    }
                    break;
            }

                if (pregunta.esRespuestaValida(texto1)) {
                    puntos +=10;
                    terminarPregunta();
                    sp.play(correcto, 1, 1, 0, 0, 1);
                }else{
                    puntos -=5;
                    terminarPregunta();
                    sp.play(incorrecto, 1, 1, 0, 0, 1);
                }
                break;

            case (R.id.BotonRespuesta2):
                Button boton2 = (Button) findViewById(R.id.BotonRespuesta2);
                String texto2 = boton2.getText().toString();

                if(pregunta.isLocalizacion() && (texto2.equals("Sí"))) {
                    texto2 = comprobarLocalizacion();

                }else if (pregunta.isLocalizacion() && (texto2.equals("No"))){
                    texto2 = comprobarLocalizacion();
                    if (!pregunta.esRespuestaValida(texto2)) {
                        puntos +=10;
                        terminarPregunta();
                        sp.play(correcto, 1, 1, 0, 0, 1);
                    }else{
                        puntos -=5;
                        terminarPregunta();
                        sp.play(incorrecto, 1, 1, 0, 0, 1);
                    }
                    break;
                }

                if (pregunta.esRespuestaValida(texto2)) {
                    puntos +=10;
                    terminarPregunta();
                    sp.play(correcto, 1, 1, 0, 0, 1);
                } else{
                    puntos -=5;
                    terminarPregunta();
                    sp.play(incorrecto, 1, 1, 0, 0, 1);
                }
                break;

            case (R.id.BotonRespuesta3):
                Button boton3 = (Button) findViewById(R.id.BotonRespuesta3);
                String texto3 = boton3.getText().toString();

                if(pregunta.isLocalizacion() && (texto3.equals("Sí"))) {
                    texto3 = comprobarLocalizacion();

                }else if (pregunta.isLocalizacion() && (texto3.equals("No"))){
                    texto3 = comprobarLocalizacion();
                    if (!pregunta.esRespuestaValida(texto3)) {
                        puntos +=10;
                        terminarPregunta();
                        sp.play(correcto, 1, 1, 0, 0, 1);
                    }else{
                        puntos -=5;
                        terminarPregunta();
                        sp.play(incorrecto, 1, 1, 0, 0, 1);
                    }
                    break;
                }

                if (pregunta.esRespuestaValida(texto3)) {
                    puntos +=10;
                    terminarPregunta();
                    sp.play(correcto, 1, 1, 0, 0, 1);
                }else{
                    puntos -=5;
                    terminarPregunta();
                    sp.play(incorrecto, 1, 1, 0, 0, 1);
                }
                break;

            case (R.id.BotonRespuesta4):
                Button boton4 = (Button) findViewById(R.id.BotonRespuesta4);
                String texto4 = boton4.getText().toString();

                if(pregunta.isLocalizacion() && (texto4.equals("Sí"))) {
                    texto4 = comprobarLocalizacion();

                }else if (pregunta.isLocalizacion() && (texto4.equals("No"))){
                    texto4 = comprobarLocalizacion();
                    if (!pregunta.esRespuestaValida(texto4)) {
                        puntos +=10;
                        terminarPregunta();
                        sp.play(correcto, 1, 1, 0, 0, 1);
                    }else{
                        puntos -=5;
                        terminarPregunta();
                        sp.play(incorrecto, 1, 1, 0, 0, 1);
                    }
                    break;
                }

                if (pregunta.esRespuestaValida(texto4)) {
                    puntos +=10;
                    terminarPregunta();
                    sp.play(correcto, 1, 1, 0, 0, 1);
                }else{
                    puntos -=5;
                    terminarPregunta();
                    sp.play(incorrecto, 1, 1, 0, 0, 1);
                }
                break;
        }
        actualizarPuntos();
    }

    private String comprobarLocalizacion() {
        String texto1;
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(estoyAqui.getLatitude(), estoyAqui.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        texto1 = addresses.get(0).getLocality(); //ciudad
        return texto1;
    }

    private void actualizarPuntos() {
        TextView textoPuntos = (TextView) findViewById(R.id.textViewPuntos);
        textoPuntos.setText(String.valueOf(puntos));
    }

    private void terminarPregunta() {
        if (preguntasRealizadas < (numeroPreguntasGenerales + numeroPreguntasUbicacion)) {
            pararMusica();
            cambiarPregunta();
        }
        else {
            pararMusica();
            Intent i = new Intent(this, ActivityFinal.class );
            i.putExtra("puntos",puntos);
            startActivity(i);
        }
    }

    private void pararMusica() {
        if(pregunta.reproducible == true){
            mediaPlayer.stop();
            if (mediaPlayer.isLooping()){
                mediaPlayer.setLooping(false);
            }
        }
    }

    private void cambiarPregunta() {
        preguntasRealizadas++;
        //Elegir una pregunta al azar
            Random rand = new Random();
            int i = rand.nextInt(listaPreguntas.size());
            pregunta = listaPreguntas.get(i);
            rellenarActividad();
        // Borrar de la lista para que no salga otra vez
            listaPreguntas.remove(i);
    }

    private void rellenarActividad() {
            Button boton1 = (Button) findViewById(R.id.BotonRespuesta1);
            boton1.setText(pregunta.getTodasRespuestas()[0]);
            boton1.setVisibility(View.VISIBLE);
            Button boton2 = (Button) findViewById(R.id.BotonRespuesta2);
            boton2.setText(pregunta.getTodasRespuestas()[1]);
            boton2.setVisibility(View.VISIBLE);
            Button boton3 = (Button) findViewById(R.id.BotonRespuesta3);
            boton3.setText(pregunta.getTodasRespuestas()[2]);
            boton3.setVisibility(View.VISIBLE);
            Button boton4 = (Button) findViewById(R.id.BotonRespuesta4);
            boton4.setText(pregunta.getTodasRespuestas()[3]);
            boton4.setVisibility(View.VISIBLE);
            FloatingActionButton botonMicro = (FloatingActionButton) findViewById(R.id.floatingActionButtonVoz);
            botonMicro.setVisibility(View.GONE);

            TextView texto = (TextView) findViewById(R.id.textoPregunta);
            texto.setText(pregunta.getPregunta());
            if (pregunta.getReproducible()) {
                Uri audio = Uri.parse("android.resource://" + getPackageName() + "/raw/" + pregunta.getUriAudio());
                mediaPlayer = MediaPlayer.create(this, audio);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                Snackbar.make(mVista,"¡Sube el volumen! Esta pregunta tiene sonido",Snackbar.LENGTH_SHORT).show();
            } else if (pregunta.isReconVoz()) {
                boton1.setVisibility(View.GONE);
                boton2.setVisibility(View.GONE);
                boton3.setVisibility(View.GONE);
                boton4.setVisibility(View.GONE);
                botonMicro.setVisibility(View.VISIBLE);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, pregunta.getPregunta());
                startActivityForResult(intent, 0);
            }
            if(pregunta.getUriImagen() != null){
                Uri imagen = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + pregunta.getUriImagen());
                Drawable yourDrawable;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imagen);
                    yourDrawable = Drawable.createFromStream(inputStream, imagen.toString() );
                } catch (FileNotFoundException e) {
                    yourDrawable = null;
                }

                ImageView image = (ImageView) findViewById(R.id.imageView);
                image.setBackground(yourDrawable);
            }else{
                ImageView image = (ImageView) findViewById(R.id.imageView);
                image.setBackgroundColor(0x00000000);
            }

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        for (String resultadoVoz : matches){
            if (resultadoVoz.equals(pregunta.getRespuesta())){
                puntos += 10;
                cambiarPregunta();
                actualizarPuntos();
                return;
            }
        }
            puntos -= 5;
            cambiarPregunta();actualizarPuntos();
    }
    }

    protected void crearLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);

            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        estoyAqui = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationManager locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        if (estoyAqui !=null){
            geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(estoyAqui.getLatitude(), estoyAqui.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String cityName = addresses.get(0).getLocality(); //ciudad
                // String countryName = addresses.get(0).getCountryName();
                addPreguntasCiudad(cityName);
                for (int i = 0; i < NUMERO_PREGUNTAS_UBICACION;i++){
                    Random rand = new Random();
                    int r = rand.nextInt(listaPreguntasUbicacion.size());
                    listaPreguntas.add(listaPreguntasUbicacion.get(r));
                    // Borrar de la lista para que no salga otra vez
                    listaPreguntasUbicacion.remove(r);
                    numeroPreguntasUbicacion++;
                }
            }catch(Exception e){
                new AlertDialog.Builder(this).setTitle("Error de localización").setMessage("Para disfrutar de todas " +
                        "las funcionalidades del juego, activa la geolocalización en tu dispositivo.").setIcon(android.R.drawable.ic_dialog_alert).setCancelable(true).show();

            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        new AlertDialog.Builder(this).setTitle("Error de localización").setMessage("Para disfrutar de todas " +
                "las funcionalidades del juego, activa la geolocalización en tu dispositivo.").setIcon(android.R.drawable.ic_dialog_alert).setCancelable(true).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new AlertDialog.Builder(this).setTitle("Error de localización").setMessage("Para disfrutar de todas " +
                "las funcionalidades del juego, activa la geolocalización en tu dispositivo.").setIcon(android.R.drawable.ic_dialog_alert).setCancelable(true).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        estoyAqui = location;

    }

    @Override
    public void onBackPressed(){
        pararMusica();
        super.onBackPressed();
    }
}
