package com.siehog.ville.httpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class ClientFactory {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final MediaType MEDIA_TYPE_JPG = MediaType.get("image/jpg");

    private static OkHttpClient okHttpClient = null;

    private ClientFactory() {
    }

    public static OkHttpClient getClient() {

        if (okHttpClient == null) {
            List<Protocol> protocols = new ArrayList<>();

            protocols.add(Protocol.HTTP_2);
            protocols.add(Protocol.HTTP_1_1);

            try {
                okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .protocols(protocols)
                        .build();

            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        return okHttpClient;
    }

}
