package com.awrtechnologies.androidvibratorservice.api.caller;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.awrtechnologies.androidvibratorservice.api.callback.ApiCallBack;
import com.awrtechnologies.androidvibratorservice.api.enums.ApiType;
import com.awrtechnologies.androidvibratorservice.api.params.ApiParams;

import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dalveersinghdaiya on 19/10/16.
 */

public abstract class BaseCaller {

    protected Retrofit retrofit;
    protected int V_INTERNAL_ERROR_FOR_LOGOUT = 500;
    protected int V_INTERNAL_ERROR_FOR_NO_RECORDFOUND = 1;
    protected int V_INTERNAL_ERROR = 501;
    protected int loadingCount = 0;

    protected String BASEURL = "https://tracker.awrtechnologies.com/" + "api/v1/";

    private ProgressDialog mProgressDialog;
//    private SpotsDialog dialog;

    protected String API_KEY = "7b2632622e75774e685f7b3f216175414b2c284f37343b6f72614e252e";
    protected String DEVICE_SECRET = "2c5b4c5839394c7a2b7936245a3b2c285f3e2b682b4b6e787e61524c4e";
//    protected String SECRET_KEY_BASE = "5778ef619329ed9faef2c1a002796c3bc6d9dd1eb88aa5f2261366e5fc5fb50036a6c04bdc7c030811d4cc7daf974bb03fcf6b0a7ebcb0d88b156d33043df851";

    //    protected String AES_IV = "2c5b4c5839394c7a2b7936245a3b2c285f3e2b682b4b6e787e61524c4e";
    protected String AES_IV = "F27D5C9927726BCEFE7510B1BDD3D137";
    protected String AES_DEFAULT_KEY = "1afae42d5e20de88486ff740cb5d467e669f8a4176a36ca4ad978c7d1efce0f9";
    protected String AES_SALT = "E0A353A4647B99F22482736C523927F571DA67E47DA5E2298B65432654E2AD7C1A0D4222A5DA0A456E382240976E01F5";

//    protected String DEVICE_SECRET = "7e56ff8c59002732af7b7527150e96d13da84eb7e4a90595349c33bedcbe00cd81bc9288a8ec64ca8aef8cbfb88682b5ee9d86bcb70c01d172bd0c3970757cac";

    public BaseCaller() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.readTimeout(0, TimeUnit.SECONDS);
        okHttpClientBuilder.connectTimeout(0, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(0, TimeUnit.HOURS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();
    }


    abstract public void get(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType);

    abstract public void post(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType);

    abstract public void put(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType);

    abstract public void delete(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType);


//    protected void showLoading(Context context, boolean showProgress) {
//        try {
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//
//            } else {
////                dialog = new SpotsDialog(context, R.style.Custom);
//                mProgressDialog = new ProgressDialog (context);
//                mProgressDialog.setCancelable(false);
////                Log.d("Meg", "Show progress");
//
//                if (showProgress) {
//
//                    mProgressDialog.show();
//                }
//
//            }
//            loadingCount++;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    protected void hideLoading() {
//        loadingCount--;
////        Log.d("Meg", "loding true" + loadingCount);
//        if (loadingCount <= 0) {
////            Log.d("Meg", "loading true");
//            try {
//                mProgressDialog.dismiss();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
//            System.out.println("encrypted string: "
//                    + Base64.encodeBase64String(encrypted));

            Log.d("Meg", "Encryption==" + Base64.encodeToString(encrypted, Base64.NO_WRAP));
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

        protected void showLoading(Context context, boolean showProgress) {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {

        } else {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading..");
            mProgressDialog.setCancelable(false);
            if (showProgress) {
                mProgressDialog.show();
            }

        }
        loadingCount++;
    }

    protected void hideLoading() {
        Log.d("MYEXAMGUIDE", "Load count--" + loadingCount);
        loadingCount--;
        if (loadingCount <= 0) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
