/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.example.apidemo.preloading;

import static com.google.android.gms.example.apidemo.MainActivity.LOG_TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAdPreloader;
import com.google.android.gms.ads.preload.PreloadCallbackV2;
import com.google.android.gms.ads.preload.PreloadConfiguration;
import com.google.android.gms.example.apidemo.R;
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadItemBinding;

/** A [Fragment] subclass that preloads an app open ad. */
public class AppOpenFragment extends Fragment {

  // Sample app open ad unit ID.
  public static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";

  private FragmentPreloadItemBinding viewBinding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    viewBinding = FragmentPreloadItemBinding.inflate(inflater, container, false);

    // Define a PreloadConfiguration.
    PreloadConfiguration configuration = new PreloadConfiguration.Builder(AD_UNIT_ID).build();

    // [Optional] Define a callback to receive preload events.
    PreloadCallbackV2 callback =
        new PreloadCallbackV2() {
          @Override
          public void onAdPreloaded(
              @NonNull String preloadId, @Nullable ResponseInfo responseInfo) {
            Log.i(LOG_TAG, "Preload ad for " + preloadId + " is available.");
            updateUI();
          }

          @Override
          public void onAdsExhausted(@NonNull String preloadId) {
            Log.i(LOG_TAG, "Preload ad  " + preloadId + " is exhausted.");
          }

          @Override
          public void onAdFailedToPreload(@NonNull String preloadId, @NonNull AdError adError) {
            Log.i(
                LOG_TAG,
                "Preload ad " + preloadId + " had an error : " + adError.getMessage() + ".");
          }
        };

    // Start the preloading with a given preload ID, preload configuration, and callback.
    AppOpenAdPreloader.start(AD_UNIT_ID, configuration, callback);

    // Initialize the UI.
    viewBinding.txtTitle.setText(getText(R.string.preload_app_open));
    viewBinding.btnShow.setOnClickListener(
        view -> {
          pollAndShowAd();
          updateUI();
        });
    updateUI();

    return viewBinding.getRoot();
  }

  private void pollAndShowAd() {
    // pollAd() returns the next available ad and loads another ad in the background.
    AppOpenAd ad = AppOpenAdPreloader.pollAd(AD_UNIT_ID);
    Activity activity = getActivity();

    if (activity != null && ad != null) {
      // [Optional] Interact with the ad object as needed.
      ad.setOnPaidEventListener(
          adValue -> {
            // [Optional] Send the impression-level ad revenue information to your preferred
            // analytics server directly within this callback.
          });

      // Show the ad immediately.
      ad.show(activity);
    }
  }

  private void updateUI() {
    if (AppOpenAdPreloader.isAdAvailable(AD_UNIT_ID)) {
      viewBinding.txtStatus.setText(getString(R.string.preload_available));
      viewBinding.btnShow.setEnabled(true);
    } else {
      viewBinding.txtStatus.setText(getString(R.string.preload_exhausted));
      viewBinding.btnShow.setEnabled(false);
    }
  }
}
