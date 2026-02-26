package com.siehog.ville.ui.webview;

import static android.content.Context.MODE_PRIVATE;
import static com.siehog.ville.httpclient.ClientFactory.JSON;
import static com.siehog.ville.httpclient.KeyHelper.getPublicKeyBase64;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.snackbar.Snackbar;
import com.siehog.ville.R;

import com.siehog.ville.databinding.FragmentWebviewBinding;
import com.siehog.ville.httpclient.ClientFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WebviewFragment extends Fragment {

    private static final String ARG_LINK = "link";
    private String link;
    private FragmentWebviewBinding binding;

    private View blackWebviewPlaceholder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", MODE_PRIVATE);

        String id = prefs.getString("device_id", null);

        int market = prefs.getInt("market_id", 0);

        String operatorUrl = prefs.getString("operator_url", null);

        try {
            String publicKey = getPublicKeyBase64();

            if ((publicKey != null) && (id != null) && (market > 0) && operatorUrl != null) {
                grabUserWallet(publicKey, id, market, operatorUrl);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentWebviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        blackWebviewPlaceholder = root.findViewById(R.id.blackWebviewPlaceholder);

        blackWebviewPlaceholder.setVisibility(View.VISIBLE);

        if (getArguments() != null) {
            link = getArguments().getString(ARG_LINK);
        }

        WebView webView = binding.webviewMarket.findViewById(R.id.webviewMarket);

        if (webView != null) {
            webView.setVisibility(View.INVISIBLE);

            webView.setBackgroundColor(Color.BLACK);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            WebSettings settings = webView.getSettings();

            settings.setDefaultTextEncodingName("utf-8");

            settings.setUseWideViewPort(true);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowContentAccess(true);
            settings.setLoadWithOverviewMode(true);
            // settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            // settings.setUserAgentString("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 TokenVille");
            settings.setUserAgentString("TokenVille (Android; 14)");
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    blackWebviewPlaceholder.setVisibility(View.GONE);

                    webView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.e("WEBVIEW", error.toString());
                }
            });

            Locale currentLocale = Resources.getSystem().getConfiguration().getLocales().get(0);

            String language = currentLocale.getLanguage();

            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("X-User-Locale", language);

            try {
                webView.loadUrl(link, extraHeaders);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void grabUserWallet(String publicKey, String id, int market, String operatorUrl) {

        OkHttpClient client = ClientFactory.getClient();

        if (client != null) {
            JSONObject payload = new JSONObject();

            try {
                payload.put("public_id", publicKey);
                payload.put("market_id", market);
                payload.put("device_id", id);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            RequestBody body = RequestBody.create(payload.toString(), JSON);

            Request request = new Request.Builder()
                    .url(operatorUrl + "/api/deposit/" + market)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                    if (response.code() == 200) {

                        try (ResponseBody responseBody = response.body()) {
                            JSONObject responseJson = new JSONObject(responseBody.string());

                            SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", MODE_PRIVATE);

                            String link = responseJson.getString("access");

                            prefs.edit().putString("wallet_id", link).apply();

                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (response.code() == 401) {

                        try (ResponseBody responseBody = response.body()) {
                            JSONObject responseJson = new JSONObject(responseBody.string());

                            Snackbar.make(requireView(), responseJson.getString("message"), Snackbar.LENGTH_LONG).show();

                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    response.body().close();
                }
            });
        }
    }

}