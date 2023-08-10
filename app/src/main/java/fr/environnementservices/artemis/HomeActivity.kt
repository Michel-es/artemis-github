package fr.environnementservices.artemis

import android.os.Bundle
import android.util.Log
import android.widget.TextView



class HomeActivity :CnxDB(){
    // créer data class qui contient la liste des champs à afficher dans le recycler view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val user=intent.getStringExtra("utilisateur")
        val idPerso=intent.getIntExtra("idPerso",0)

        val tvUserConnect: TextView=findViewById(R.id.tvUserConnect)
        tvUserConnect.text=user
        val url="https://192.168.8.150/root/5-%20ModuleAppliMobile/Menu/Menu.php"
        val jsonData = "{\"idPerso\":\"$idPerso\"}"
        toExecuteQuery(url,jsonData,object: QueryResult{
            override fun onSuccess(data: Any) {

                try{


                }catch (e: Exception){
                    Log.d("artemis",e.toString())
                }

            }

            override fun onError() {
                Log.d("artemis","erreur")
            }
        })

    }
}