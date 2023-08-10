package fr.environnementservices.artemis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Créez une instance de l'Intent pour lancer l'activité Cnxuser
        val intent = Intent(this, CnxUser::class.java)

        // Lancez l'activité en utilisant l'Intent
        startActivity(intent)

        // Facultatif : vous pouvez appeler finish() pour terminer l'activité MainActivity après avoir lancé Cnxuser,
        // cela dépend de vos besoins.
        finish()
    }
}