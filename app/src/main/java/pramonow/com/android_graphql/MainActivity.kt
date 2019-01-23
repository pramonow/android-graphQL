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

    private fun setupApollo(): ApolloClient {
        val okHttp = OkHttpClient
                .Builder()
                .addInterceptor({ chain ->
                    val original = chain.request()
                    val builder = original.newBuilder().method(original.method(),
                            original.body())
                    builder.addHeader("Authorization"
                            , "Bearer " + "92139db05538f5c3c2702490c4c9a8f3a028e6c1")
                    chain.proceed(builder.build())
                })
                .build()
        return ApolloClient.builder()
                .serverUrl("https://api.github.com/graphql")
                .okHttpClient(okHttp)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var client = setupApollo()
        client.query(FindQuery    //From the auto generated class
                .builder()
                .name("androidbasic") //Passing required arguments
                .owner("pramonow") //Passing required arguments
                .build())
                .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.d("baniman", "" + e.message)
                    }
                    override fun onResponse(response: Response<FindQuery.Data>) {
                        Log.d("baniman", "" + response.data().toString())
                    }
                })
    }
}
