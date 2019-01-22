package pramonow.com.android_graphql

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.apollographql.apollo.ApolloClient
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
                            , "Bearer " + BuildConfig.AUTH_TOKEN)
                    chain.proceed(builder.build())
                })
                .build()
        return ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttp)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var client = setupApollo()
        client.query(FindQuery    //From the auto generated class
                .builder()
                .name(repo_name_edittext.text.toString()) //Passing required arguments
                .owner(owner_name_edittext.text.toString()) //Passing required arguments
                .build())
                .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.info(e.message.toString())
                    }
                    override fun onResponse(response: Response<FindQuery.Data>) {
                        Log.info(" " + response.data()?.repository())
                        runOnUiThread({
                            progress_bar.visibility = View.GONE
                            name_text_view.text = String.format(getString(R.string.name_text),
                                    response.data()?.repository()?.name())
                            description_text_view.text = String.format(getString(R.string.description_text),
                                    response.data()?.repository()?.description())
                            forks_text_view.text = String.format(getString(R.string.fork_count_text),
                                    response.data()?.repository()?.forkCount().toString())
                            url_text_view.text = String.format(getString(R.string.url_count_text),
                                    response.data()?.repository()?.url().toString())
                        })
                    }
                })
    }
}
