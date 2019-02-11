package pramonow.com.android_graphql

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    //setting up http client and apollo client here
    private fun setupApollo(): ApolloClient {

        //generate git authentication token from your github (Developer option)
        val gitToken = "REPLACE_WITH_TOKEN"

        //generate http client with git token
        val okHttp = OkHttpClient.Builder().addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder().method(original.method(), original.body())
                    builder.addHeader("Authorization", "Bearer " + gitToken)

                    chain.proceed(builder.build())
                }.build()

        return ApolloClient.builder().serverUrl("https://api.github.com/graphql").okHttpClient(okHttp).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var client = setupApollo()

        //generate queries
        val findRepoQuery = FindQuery.builder().name("androidbasic").owner("pramonow").build()
        val findUserQuery = FindUser.builder().login("pramonow").build()

        //simple basic query
        client.query(findRepoQuery).enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
            override fun onResponse(response: Response<FindQuery.Data>) {
                Log.d("GQLTAG", "Response: " + response.data().toString())
            }
            override fun onFailure(e: ApolloException) {
                Log.d("GQLTAG", "Error: " + e.message)
            }
        })

        //this query is example for using edges to get data that is connected to another table
        client.query(findUserQuery).enqueue(object : ApolloCall.Callback<FindUser.Data>() {
            override fun onResponse(response: Response<FindUser.Data>) {
                Log.d("GQLTAG", "Response: " + response.data().toString())
            }
            override fun onFailure(e: ApolloException) {
                Log.d("GQLTAG", "Error: "+ e.message)
            }
        })
    }
}
