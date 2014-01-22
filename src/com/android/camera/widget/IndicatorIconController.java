/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.camera.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.camera.ButtonManager;
import com.android.camera.app.AppController;
import com.android.camera.module.ModulesInfo;
import com.android.camera.settings.SettingsManager;
import com.android.camera.util.PhotoSphereHelper;

import com.android.camera2.R;

/**
 * IndicatorIconController sets the visibility and icon state of
 * on screen indicators.
 *
 * Indicators are only visible if they are in a non-default state.  The
 * visibility of an indicator is set when an indicator's setting changes.
 */
public class IndicatorIconController
    implements SettingsManager.OnSettingChangedListener,
               ButtonManager.ButtonStatusListener {

    private final static String TAG = "IndicatorIconController";

    private ImageView mFlashIndicator;
    private ImageView mHdrIndicator;
    private ImageView mRefocusIndicator;
    private ImageView mPanoramaIndicator;

    private TypedArray mFlashIndicatorPhotoIcons;
    private TypedArray mFlashIndicatorVideoIcons;
    private TypedArray mHdrPlusIndicatorIcons;
    private TypedArray mHdrIndicatorIcons;
    private TypedArray mRefocusIndicatorIcons;
    private TypedArray mPanoramaIndicatorIcons;

    private AppController mController;

    public IndicatorIconController(AppController controller, View root) {
        mController = controller;
        Context context = controller.getAndroidContext();

        mFlashIndicator = (ImageView) root.findViewById(R.id.flash_indicator);
        mHdrIndicator = (ImageView) root.findViewById(R.id.hdr_indicator);
        mRefocusIndicator = (ImageView) root.findViewById(R.id.refocus_indicator);
        mPanoramaIndicator = (ImageView) root.findViewById(R.id.panorama_indicator);

        mFlashIndicatorPhotoIcons = context.getResources().obtainTypedArray(
            R.array.camera_flashmode_indicator_icons);
        mFlashIndicatorVideoIcons = context.getResources().obtainTypedArray(
            R.array.video_flashmode_indicator_icons);
        mHdrPlusIndicatorIcons = context.getResources().obtainTypedArray(
            R.array.pref_camera_hdr_plus_indicator_icons);
        mHdrIndicatorIcons = context.getResources().obtainTypedArray(
            R.array.pref_camera_hdr_indicator_icons);
        mRefocusIndicatorIcons = context.getResources().obtainTypedArray(
            R.array.refocus_indicator_icons);
        if (PhotoSphereHelper.getPanoramaIndicatorArrayId() > 0) {
            mPanoramaIndicatorIcons = context.getResources().obtainTypedArray(
                PhotoSphereHelper.getPanoramaIndicatorArrayId());
        }
    }

    @Override
    public void onButtonVisibilityChanged(ButtonManager buttonManager, int buttonId) {
        syncIndicatorWithButton(buttonId);
    }

    @Override
    public void onButtonEnabledChanged(ButtonManager buttonManager, int buttonId) {
        syncIndicatorWithButton(buttonId);
    }

    /**
     * Syncs a specific indicator's icon and visibility
     * based on the enabled state and visibility of a button.
     */
    private void syncIndicatorWithButton(int buttonId) {
        switch (buttonId) {
            case ButtonManager.BUTTON_FLASH: {
                syncFlashIndicator();
                break;
            }
            case ButtonManager.BUTTON_TORCH: {
                syncFlashIndicator();
                break;
            }
            case ButtonManager.BUTTON_HDRPLUS: {
                syncHdrIndicator();
                break;
            }
            case ButtonManager.BUTTON_HDR: {
                syncHdrIndicator();
                break;
            }
            case ButtonManager.BUTTON_REFOCUS: {
                syncRefocusIndicator();
                break;
            }
            case ButtonManager.BUTTON_PANO_HORIZONTAL: {
                syncPanoramaIndicator();
                break;
            }
            case ButtonManager.BUTTON_PANO_VERTICAL: {
                syncPanoramaIndicator();
                break;
            }
            default:
                // Do nothing.  The indicator doesn't care
                // about button that don't correspond to indicators.
        }
    }

    /**
     * Sets all indicators to the correct resource and visibility
     * based on the current settings.
     */
    public void syncIndicators() {
        syncFlashIndicator();
        syncHdrIndicator();
        syncRefocusIndicator();
        syncPanoramaIndicator();
    }

    /**
     * Sync the icon and visibility of the flash indicator.
     */
    private void syncFlashIndicator() {
        ButtonManager buttonManager = mController.getButtonManager();
        // If flash isn't an enabled and visible option,
        // do not show the indicator.
        if (buttonManager.isEnabled(ButtonManager.BUTTON_FLASH)
                && buttonManager.isVisible(ButtonManager.BUTTON_FLASH)) {

            int modeIndex = mController.getCurrentModuleIndex();
            if (modeIndex == mController.getAndroidContext().getResources()
                    .getInteger(R.integer.camera_mode_video)) {
                setIndicatorState(mController.getSettingsManager(),
                                  SettingsManager.SETTING_VIDEOCAMERA_FLASH_MODE,
                                  mFlashIndicator, mFlashIndicatorVideoIcons, false);
            } else {
                setIndicatorState(mController.getSettingsManager(),
                                  SettingsManager.SETTING_FLASH_MODE,
                                  mFlashIndicator, mFlashIndicatorPhotoIcons, false);
            }
        } else {
            mFlashIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Sync the icon and the visibility of the hdr/hdrplus indicator.
     */
    private void syncHdrIndicator() {
        ButtonManager buttonManager = mController.getButtonManager();
        // If hdr isn't an enabled and visible option,
        // do not show the indicator.
        if (buttonManager.isEnabled(ButtonManager.BUTTON_HDRPLUS)
                && buttonManager.isVisible(ButtonManager.BUTTON_HDRPLUS)) {
            setIndicatorState(mController.getSettingsManager(),
                              SettingsManager.SETTING_CAMERA_HDR,
                              mHdrIndicator, mHdrPlusIndicatorIcons, false);
        } else if (buttonManager.isEnabled(ButtonManager.BUTTON_HDR)
                && buttonManager.isVisible(ButtonManager.BUTTON_HDR)) {
            setIndicatorState(mController.getSettingsManager(),
                              SettingsManager.SETTING_CAMERA_HDR,
                              mHdrIndicator, mHdrIndicatorIcons, false);
        } else {
            mHdrIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Sync the icon and the visibility of the refocus indicator.
     */
    private void syncRefocusIndicator() {
        ButtonManager buttonManager = mController.getButtonManager();
        // If refocus isn't an enabled and visible option,
        // do not show the indicator.
        if (buttonManager.isEnabled(ButtonManager.BUTTON_REFOCUS)
                && buttonManager.isVisible(ButtonManager.BUTTON_REFOCUS)) {
            setIndicatorState(mController.getSettingsManager(),
                              SettingsManager.SETTING_CAMERA_REFOCUS,
                              mRefocusIndicator, mRefocusIndicatorIcons, false);
        } else {
            mRefocusIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Sync the icon and the visibility of the panorama indicator.
     */
    private void syncPanoramaIndicator() {
        ButtonManager buttonManager = mController.getButtonManager();
        // If refocus isn't an enabled and visible option,
        // do not show the indicator.
        boolean panoEnabled = (buttonManager.isEnabled(ButtonManager.BUTTON_PANO_HORIZONTAL)
            && buttonManager.isEnabled(ButtonManager.BUTTON_PANO_VERTICAL));
        boolean panoVisible = (buttonManager.isVisible(ButtonManager.BUTTON_PANO_HORIZONTAL)
            && buttonManager.isVisible(ButtonManager.BUTTON_PANO_VERTICAL));
        if (panoEnabled && panoVisible && mPanoramaIndicatorIcons != null) {
            setIndicatorState(mController.getSettingsManager(),
                              SettingsManager.SETTING_CAMERA_PANO_ORIENTATION,
                              mPanoramaIndicator, mPanoramaIndicatorIcons, true);
        } else {
            mPanoramaIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Sets the image resource and visibility of the indicator
     * based on the indicator's corresponding setting state.
     */
    private void setIndicatorState(SettingsManager settingsManager, int id,
            ImageView imageView, TypedArray iconArray, boolean showDefault) {

        // Set the correct image src.
        String value = settingsManager.get(id);
        int valueIndex = settingsManager.getStringValueIndex(id);
        if (valueIndex < 0) {
            // This can happen when the setting is camera dependent
            // and the camera is not yet open.  CameraAppUI.onChangeCamera()
            // will call this again when the camera is open.
            Log.w(TAG, "The setting for this indicator is not available.");
            imageView.setVisibility(View.GONE);
            return;
        }
        Drawable drawable = iconArray.getDrawable(valueIndex);
        if (drawable == null) {
            throw new IllegalStateException("Indicator drawable is null.");
        }
        imageView.setImageDrawable(drawable);

        // Set the indicator visible if not in default state.
        if (!showDefault && settingsManager.isDefault(id)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSettingChanged(SettingsManager settingsManager, int id) {
        switch (id) {
            case SettingsManager.SETTING_FLASH_MODE: {
                syncFlashIndicator();
                break;
            }
            case SettingsManager.SETTING_VIDEOCAMERA_FLASH_MODE: {
                syncFlashIndicator();
                break;
            }
            case SettingsManager.SETTING_CAMERA_HDR: {
                syncHdrIndicator();
                break;
            }
            case SettingsManager.SETTING_CAMERA_REFOCUS: {
                syncRefocusIndicator();
                break;
            }
            case SettingsManager.SETTING_CAMERA_PANO_ORIENTATION: {
                syncPanoramaIndicator();
                break;
            }
            default: {
                // Do nothing.
            }
        }
    }
}
