package rsklabs.com.ads;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;



/**
 * Created by Senthilkumar on 11/2/2016.
 */
public class InterstitialAds {

    public static final String TAG = InterstitialAds.class.getSimpleName();
    public static final String ADMOB_ID = "ca-app-pub-3940256099942544/1033173712";
    private static ScheduledFuture loaderHandler;

    public static void loadInterstitialAd(final Activity activity){
        final Runnable loader = new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final InterstitialAd interstitial = new InterstitialAd(activity);
                        interstitial.setAdUnitId(ADMOB_ID);
                        AdRequest adRequest = new AdRequest.Builder().build();
                        interstitial.loadAd(adRequest);
                        interstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                showInterstitial(interstitial);
                            }
                        });
                    }
                });
            }
        };

        ScheduledExecutorService  scheduler = Executors.newScheduledThreadPool(1);
        loaderHandler = scheduler.scheduleWithFixedDelay(loader,30,30, TimeUnit.SECONDS);
    }

    private static void showInterstitial(final InterstitialAd interstitial ) {
        if(interstitial.isLoaded()){
            interstitial.show();
        }
    }


}
