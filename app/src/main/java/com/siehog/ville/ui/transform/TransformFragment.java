package com.siehog.ville.ui.transform;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.siehog.ville.R;
import com.siehog.ville.databinding.FragmentTransformBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TransformFragment extends Fragment {
    private FragmentTransformBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", MODE_PRIVATE);

        String link = prefs.getString("wallet_id", null);
        int market = prefs.getInt("market_id", 0);

        WebView webView = binding.webviewTransform.findViewById(R.id.webviewTransform);

        if (webView != null) {
            webView.setBackgroundColor(Color.BLACK);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            if ((link != null) && (market > 0)) {
                WebSettings settings = webView.getSettings();

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
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}