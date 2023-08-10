package fr.environnementservices.artemis

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import org.json.JSONTokener
import android.content.Intent

class CnxUser :CnxDB(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cnx_user)


        //créer un écouteur sur le bouton "se connecter" sur l'événement onclick
        val btnConnecter: Button =findViewById(R.id.btnConnexion)

        btnConnecter.setOnClickListener{
/*
        findViewById<EditText>(R.id.edLogin): Cette partie de l'expression utilise la méthode findViewById pour rechercher la vue correspondant à
        l'identifiant R.id.edLogin dans le layout de l'activité. R.id.edLogin fait référence à un élément de l'interface utilisateur défini
         dans le fichier XML de mise en page (layout) de l'activité, et il est associé à un champ de saisie (EditText) dans ce cas.
         La fonction findViewById retourne une référence à la vue trouvée, mais elle est de type générique (generic type) EditText,
         ce qui signifie qu'elle est explicitement castée en tant qu'objet de type EditText.

        .text: Une fois que la vue (EditText) est trouvée, nous accédons à la propriété text, qui contient le texte actuellement saisi par l'utilisateur dans le champ de saisie (EditText).

        .toString(): La propriété text retourne un objet de type Editable, mais si nous voulons obtenir le contenu sous forme de texte (String),
        nous devons appeler la méthode toString() pour le convertir en une chaîne de caractères.

        Ainsi, la combinaison de ces étapes permet d'obtenir le texte entré par l'utilisateur dans le champ de saisie (EditText)
        identifié par R.id.edLogin sous forme de chaîne de caractères. Cela est souvent utilisé pour récupérer les entrées de
        l'utilisateur à partir des éléments de l'interface utilisateur dans une application Android et les traiter ultérieurement.
 */

            val username = findViewById<EditText>(R.id.edLogin).text.toString()
            val password = findViewById<EditText>(R.id.edMdp).text.toString()
            val jsonString="{\"login\":\"$username\",\"mdp\":\"$password\"}"

            toExecuteQuery("https://192.168.8.150/root/2-Interfaces/Connexion/ConnexionAndroid.php", jsonString, object : QueryResult {
                override fun onSuccess(data: Any) {

                    val jsonTokener=JSONTokener(data.toString())
                    val jsonObject=JSONObject(jsonTokener)

                    val message = "Bienvenue ${jsonObject.getString("utilisateur")}"
                    Toast.makeText(this@CnxUser, message, Toast.LENGTH_SHORT).show()


                    val intent= Intent(this@CnxUser,HomeActivity::class.java)

                    intent.putExtra("idPerso",jsonObject.getInt("idPerso"))

                    intent.putExtra("utilisateur",jsonObject.getString("utilisateur"))
                    startActivity(intent)

                }

                override fun onError() {
                    val message: String = "Erreur de connexion"
                    Toast.makeText(this@CnxUser, message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}