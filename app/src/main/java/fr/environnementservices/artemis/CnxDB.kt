package fr.environnementservices.artemis

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


open class CnxDB: AppCompatActivity()
{
    /*
    private: C'est un modificateur d'accès qui indique que la variable jsonData n'est accessible que depuis l'intérieur de la classe où elle est déclarée.
    Les autres classes en dehors de cette classe ne peuvent pas accéder directement à cette propriété.

l   ateinit: C'est un modificateur qui indique que la variable jsonData sera initialisée plus tard, après la création de l'objet de la classe.
    Cela signifie que vous pouvez déclarer une variable non nullable (String dans ce cas) sans lui donner une valeur immédiatement. Cependant, vous devez vous assurer de l'initialiser avant d'y accéder pour éviter les erreurs NullPointerException.
    */

    private  var jsonData: String=""
    private  var urlString: String=""

    interface QueryResult {
        fun onSuccess(data: Any)
        fun onError()
    }

    suspend fun doInBackGround(): String {
        //permet d'accepter tous les certificats, passe outre les certificats SSL
        /*
        TrustManager est une interface du package javax.net.ssl qui définit les méthodes pour vérifier la validité des certificats lors de l'établissement d'une connexion SSL/TLS.
        X509TrustManager est une sous-interface de TrustManager qui traite spécifiquement des certificats au format X.509, utilisés dans SSL/TLS.
        L'expression object : X509TrustManager { ... } crée une instance anonyme de X509TrustManager en définissant les méthodes requises
        (checkClientTrusted, checkServerTrusted et getAcceptedIssuers) à l'intérieur des accolades. Dans ce cas, les méthodes sont vides, ce qui signifie qu'aucune vérification des certificats n'est effectuée.
        arrayOf(...) est une fonction qui permet de créer un tableau contenant les éléments spécifiés entre parenthèses. Dans ce cas, il ne contient qu'un seul élément, qui est l'instance anonyme de X509TrustManager
        Enfin, la variable trustAllCertificates est déclarée en utilisant le mot-clé val pour indiquer qu'il s'agit d'une valeur immuable (constante). Elle est donc de type Array<TrustManager>, c'est-à-dire un tableau d'objets TrustManager, et contient l'instance anonyme de X509TrustManager.
         */
        val trustAllCertificates: Array<TrustManager> = arrayOf(
            /*L'expression object : X509TrustManager { ... } est une déclaration d'une instance anonyme (objet anonyme) d'une classe qui implémente l'interface X509TrustManager.
        En Kotlin, vous pouvez créer une instance anonyme d'une interface en utilisant le mot-clé object suivi de : et du nom de l'interface que vous souhaitez implémenter.
        Ensuite, vous pouvez définir les méthodes requises par cette interface entre les accolades { ... }.
        Dans le cas spécifique de X509TrustManager, c'est une interface du package javax.net.ssl qui fait partie de l'API Java pour la gestion des connexions SSL/TLS sécurisées.
        Cette interface définit les méthodes qui permettent de gérer les certificats de sécurité lors de l'établissement d'une connexion SSL/TLS.
        Lorsque vous utilisez object : X509TrustManager { ... }, vous créez essentiellement une instance d'une classe qui implémente toutes les méthodes de X509TrustManager.
        Dans le corps des accolades, vous pouvez définir le comportement souhaité pour chacune de ces méthodes.
        Dans l'exemple que vous avez fourni, les méthodes checkClientTrusted et checkServerTrusted sont vides, ce qui signifie qu'aucune vérification des certificats n'est effectuée (tous les certificats sont acceptés).
        La méthode getAcceptedIssuers retourne simplement un tableau vide d'émetteurs de certificats de confiance.
        C'est une façon de créer une implémentation personnalisée de X509TrustManager sans avoir besoin de définir une classe séparée pour cela. Cette approche est couramment utilisée
        lorsqu'on a besoin d'une implémentation spécifique pour un cas d'utilisation particulier sans avoir à créer une classe distincte.
*/
            object : X509TrustManager {
                /*
                p0: Array<out X509Certificate>?: Cela indique que le premier paramètre (p0) est de type Array<out X509Certificate>?, c'est-à-dire un tableau (Array) d'objets de type X509Certificate.
                Le symbole out indique que le tableau est utilisé en position de sortie (covariant) et permet de fournir des objets X509Certificate à partir du tableau, mais pas d'y ajouter de nouveaux éléments.

                p1: String?: Cela indique que le deuxième paramètre (p1) est de type String?,
                c'est-à-dire une chaîne de caractères (String) qui peut être nullable (pouvant être nulle). Le symbole ? indique que le paramètre peut être nul.

                En résumé, cette signature de méthode indique qu'elle prend deux paramètres : un tableau d'objets X509Certificate et une chaîne de caractères,
                où le tableau est utilisé pour fournir des certificats de sécurité (X509Certificate) et la chaîne de caractères est utilisée pour fournir le type d'authentification (authType).
                Ces deux paramètres peuvent être nuls car ils sont suivis du symbole ?.
                 */
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    //Ne rien faire (accepter tous les certificats cote client)
                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    //Ne rien faire (accepter tous les certificats cote serveur)
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return emptyArray()
                }
            }
        )

        val sslContext:SSLContext=SSLContext.getInstance("TLS")
        /*
        sslContext: L'objet SSLContext est responsable de la gestion des protocoles SSL/TLS.

        init(): C'est une méthode de l'objet SSLContext utilisée pour initialiser le contexte SSL avec les paramètres requis.

        null: Le premier paramètre de init() spécifie un tableau de KeyManager.
        En utilisant null ici, nous indiquons que nous n'avons pas besoin de spécifier de clés de certificat pour le client (clés de certification client).
         Cela signifie que le contexte SSL sera configuré pour accepter tous les certificats côté client.

        trustAllCertificates: Le deuxième paramètre de init() spécifie un tableau de TrustManager.
        Ici, nous utilisons le tableau trustAllCertificates, qui a été défini précédemment dans le code pour accepter tous les certificats côté serveur.
        Cela signifie que le contexte SSL sera configuré pour faire confiance à tous les certificats côté serveur sans effectuer de vérification.

        java.security.SecureRandom(): Le troisième paramètre de init() est un objet SecureRandom, utilisé pour générer des nombres aléatoires sécurisés utilisés dans le processus de négociation SSL/TLS.
         En utilisant java.security.SecureRandom(), nous créons un nouvel objet SecureRandom avec les paramètres par défaut.

        En résumé, avec sslContext.init(null, trustAllCertificates, java.security.SecureRandom()),
        nous configurons le contexte SSL pour accepter tous les certificats côté client et côté serveur, ce qui permet d'effectuer des connexions SSL/TLS sans vérification de certificat.
         */
        sslContext.init(null,trustAllCertificates,java.security.SecureRandom())
/*
        val okHttpClient: OkHttpClient: C'est une variable immuable (val) qui représente l'objet OkHttpClient que vous allez créer.

        OkHttpClient.Builder(): C'est le constructeur pour créer une instance de OkHttpClient.Builder, qui permet de configurer les paramètres de l'OkHttpClient.

        .sslSocketFactory(sslContext.socketFactory, trustAllCertificates[0] as X509TrustManager):
        Cela configure le socketFactory SSL personnalisé pour le client HTTPS. Ici, sslContext est un objet qui représente le contexte SSL configuré précédemment pour accepter tous les certificats côté client et côté serveur (trustAllCertificates).

        .hostnameVerifier { _, _ -> true }: Cela définit un vérificateur d'hôte personnalisé qui accepte tous les noms d'hôtes (hostnames) sans vérification supplémentaire.

        .build(): Cela finalise la configuration de OkHttpClient et renvoie une instance prête à l'emploi.
 */
        val okHttpClient: OkHttpClient=OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory,trustAllCertificates[0] as X509TrustManager)
            .hostnameVerifier{ _,_ -> true }
            .build()

        val postData="donnees=${jsonData}" //Créer la chaine de données à envoyer

        val mediaType="application/x-www-form-urlencoded".toMediaType()
        val body=postData.toRequestBody(mediaType)

        val request=Request.Builder()
            .url(urlString)
            .post(body)
            .build()
        /*
        okHttpClient.newCall(request): Cette ligne crée un nouvel objet Call en utilisant okHttpClient pour exécuter une requête HTTP avec la demande (request) fournie.
        La demande doit contenir l'URL, la méthode (GET, POST, etc.) et éventuellement les paramètres de requête.

        .execute(): C'est une méthode synchrone qui exécute effectivement la requête HTTP en envoyant la demande au serveur et en attendant la réponse.

        .use { response -> ... }: Cette syntaxe Kotlin utilise la ressource response retournée par execute() et assure que la ressource est fermée après utilisation, même en cas d'exception.

        if (response.isSuccessful) { ... } else { ... }: Ici, on vérifie si la réponse du serveur est réussie (code de statut 2xx).
        Si la réponse est réussie, on lit le corps de la réponse à l'aide de response.body?.string(), qui renvoie le contenu de la réponse sous forme de chaîne de caractères. Si le corps de la réponse est vide, on retourne une chaîne vide ("") à la place.

        throw IOException("Erreur dans le code${response.code}"): Si la réponse du serveur n'est pas réussie (code de statut autre que 2xx),
        on lance une exception de type IOException avec un message contenant le code de statut de la réponse. Cela indique qu'il y a eu une erreur lors de la requête HTTP et fournit le code de statut pour diagnostiquer la nature de l'erreur.
         */
        okHttpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful){
                /*response.body: response est un objet de type Response renvoyé par la requête HTTP. La propriété body représente le corps de la réponse HTTP.
                Cette propriété peut être nulle si, par exemple, la requête n'a pas abouti ou si le corps de la réponse est vide.

            ?.: C'est un opérateur de null safety en Kotlin. Il permet d'appeler une propriété ou une méthode sur un objet qui peut être nul. Si l'objet est nul,
             l'expression entière retourne nulle sans générer d'erreur NullPointerException.

            string(): C'est une méthode de l'objet ResponseBody, qui est renvoyé par la propriété body. La méthode string() convertit le corps de la réponse HTTP en une chaîne de caractères.
             Cette méthode peut également retourner nulle si le corps de la réponse est vide ou si la conversion échoue.

            ?:: C'est l'opérateur elvis en Kotlin. Il est utilisé pour fournir une valeur par défaut lorsque l'expression avant l'opérateur est nulle. Si l'expression avant l'opérateur est nulle,
             l'expression entière retourne la valeur après l'opérateur. Dans ce cas, la valeur par défaut est une chaîne vide "".

            Ainsi, l'expression response.body?.string() ?: "" signifie que si response.body est non nul et que response.body.string() renvoie une valeur non nulle, alors cette valeur sera renvoyée.
            Sinon, si response.body est nul ou que response.body.string() renvoie une valeur nulle, alors une chaîne vide "" sera renvoyée. Cela permet de gérer les cas où la réponse est vide ou nulle de manière sécurisée, sans générer d'erreur.
 */
                return response.body?.string() ?:""
            }else{
                throw IOException("Erreur dans la requête${response.code}")
            }
        }
    }

    public fun toExecuteQuery(urlString:String, jsonData:String, action:QueryResult){
        this.urlString=urlString
        this.jsonData=jsonData

        /*
        CoroutineScope: Les coroutines en Kotlin sont gérées par une instance de CoroutineScope,
        qui est responsable de la gestion du cycle de vie des coroutines et de la gestion des exceptions éventuelles.
        CoroutineScope est utilisé pour créer et lancer des coroutines.

        (Dispatchers.Main): Le Dispatchers.Main est un objet qui représente le Dispatcher pour le thread principal (Main) dans Android.
         Un Dispatcher est responsable de l'exécution des coroutines sur un thread particulier.
         Dans ce cas, nous utilisons Dispatchers.Main pour indiquer que la coroutine doit s'exécuter sur le thread principal,
         qui est le thread sur lequel les opérations d'interface utilisateur doivent être effectuées dans une application Android.

        .launch: Une fois que nous avons créé le CoroutineScope avec le Dispatcher spécifié, nous utilisons la fonction launch pour lancer une nouvelle coroutine.
        La fonction launch crée une coroutine légère, qui est un moyen efficace d'exécuter des tâches de manière asynchrone sans bloquer le thread principal.

        En résumé, CoroutineScope(Dispatchers.Main).launch est utilisé pour lancer une nouvelle coroutine sur le thread principal dans une application Android.
        Cela permet d'exécuter des tâches asynchrones tout en assurant que les opérations d'interface utilisateur peuvent être mises à jour correctement sur le thread principal
         sans provoquer de blocage (freeze) de l'application. Les coroutines sont un moyen puissant de gérer la concurrence et les opérations asynchrones de manière élégante et sécurisée dans Kotlin.
         */
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    doInBackGround()
                }

                try {

                    action.onSuccess(JSONObject(result))
                } catch (e: JSONException) {
                    action.onSuccess(JSONArray(result))
                }
            } catch (e: Exception) {
                Log.d("artemis","catcrh dans cnxdb${e.toString()}")
                action.onError()
            }
        }
    }





}