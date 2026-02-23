package com.siehog.ville.ui.webview;

import static android.content.Context.MODE_PRIVATE;

import static com.siehog.ville.httpclient.KeyHelper.getPublicKeyBase64;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import com.siehog.ville.R;

import com.siehog.ville.databinding.FragmentWebviewBinding;

import java.util.HashMap;
import java.util.Map;

public class WebviewFragment extends Fragment {

    private static final String ARG_LINK = "link";
    private String link;
    private FragmentWebviewBinding binding;

    public static WebviewFragment newInstance(String link) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            link = getArguments().getString(ARG_LINK);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        WebviewViewModel webviewModel =
                new ViewModelProvider(this).get(WebviewViewModel.class);

        binding = FragmentWebviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        WebView webView = binding.webview.findViewById(R.id.webview);

        if (webView != null) {
            WebSettings settings = webView.getSettings();

            settings.setUseWideViewPort(true);
            settings.setDatabaseEnabled(true);
            settings.setAllowFileAccess(false);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowContentAccess(true);
            settings.setLoadWithOverviewMode(true);

            // settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            // settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            // settings.setUserAgentString("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 Ville");

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.e("WEBVIEW", error.toString());
                }
            });

            try {
                webView.loadUrl(link);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}