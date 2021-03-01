package sk.peterjurkovic.dril.updater;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HttpClient {

    public final static  OkHttpClient INSTANCE = new OkHttpClient.Builder()
                                                                 .readTimeout(3000, TimeUnit.MILLISECONDS)
                                                                 .writeTimeout(3000, TimeUnit.MILLISECONDS)
                                                                 .connectTimeout(3000, TimeUnit.MILLISECONDS)
                                                                 .callTimeout(15000, TimeUnit.MILLISECONDS)
                                                                 .build();
}
