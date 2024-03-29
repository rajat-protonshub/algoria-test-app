package com.softinator.algolia.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlgoliaApi {

        private static Retrofit retrofit = null;
        private static final String BASE_URL = "https://hn.algolia.com/api/v1/";

        public static Retrofit getClient() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.NONE);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:sss")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();



            return retrofit;
        }

}
