/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.carloz.inputmethod.comm;

import static com.carloz.inputmethod.comm.Constants.Subtype.ExtraValue.REQ_NETWORK_CONNECTIVITY;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.carloz.inputmethod.R;
import com.carloz.inputmethod.annotations.UsedForTesting;
import com.carloz.inputmethod.compat.InputMethodSubtypeCompatUtils;
import com.carloz.inputmethod.flag.DebugFlags;
import com.carloz.inputmethod.keyboard.internal.LanguageOnSpacebarHelper;
import com.carloz.inputmethod.utils.LocaleUtils;
import com.carloz.inputmethod.utils.SubtypeLocaleUtils;

public final class SubtypeSwitcher {
    private static boolean DBG = DebugFlags.DEBUG_ENABLED;
    private static final String TAG = SubtypeSwitcher.class.getSimpleName();

    private static final SubtypeSwitcher sInstance = new SubtypeSwitcher();

    private /* final */ Resources mResources;

    private final LanguageOnSpacebarHelper mLanguageOnSpacebarHelper =
            new LanguageOnSpacebarHelper();
    private InputMethodInfo mShortcutInputMethodInfo;
    private InputMethodSubtype mShortcutSubtype;
    private InputMethodSubtype mNoLanguageSubtype;
    private InputMethodSubtype mEmojiSubtype;
    private boolean mIsNetworkConnected;

    private static final String KEYBOARD_MODE = "keyboard";
    // Dummy no language QWERTY subtype. See {@link R.xml.method}.
    private static final int SUBTYPE_ID_OF_DUMMY_NO_LANGUAGE_SUBTYPE = 0xdde0bfd3;
    private static final String EXTRA_VALUE_OF_DUMMY_NO_LANGUAGE_SUBTYPE =
            "KeyboardLayoutSet=" + SubtypeLocaleUtils.QWERTY
            + "," + Constants.Subtype.ExtraValue.ASCII_CAPABLE
            + "," + Constants.Subtype.ExtraValue.ENABLED_WHEN_DEFAULT_IS_NOT_ASCII_CAPABLE
            + "," + Constants.Subtype.ExtraValue.EMOJI_CAPABLE;

    // Caveat: We probably should remove this when we add an Emoji subtype in {@link R.xml.method}.
    // Dummy Emoji subtype. See {@link R.xml.method}.
    private static final int SUBTYPE_ID_OF_DUMMY_EMOJI_SUBTYPE = 0xd78b2ed0;
    private static final String EXTRA_VALUE_OF_DUMMY_EMOJI_SUBTYPE =
            "KeyboardLayoutSet=" + SubtypeLocaleUtils.EMOJI
            + "," + Constants.Subtype.ExtraValue.EMOJI_CAPABLE;

    public static SubtypeSwitcher getInstance() {
        return sInstance;
    }

    public static void init(final Context context) {
        SubtypeLocaleUtils.init(context);
        sInstance.initialize(context);
    }

    private SubtypeSwitcher() {
        // Intentional empty constructor for singleton.
    }

    private void initialize(final Context context) {
        if (mResources != null) {
            return;
        }
        mResources = context.getResources();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        mIsNetworkConnected = (info != null && info.isConnected());

        updateParametersOnStartInputView();
    }

    /**
     * Update parameters which are changed outside LatinIME. This parameters affect UI so that they
     * should be updated every time onStartInputView is called.
     */
    public void updateParametersOnStartInputView() {
    }

    // Update the current subtype. LatinIME.onCurrentInputMethodSubtypeChanged calls this function.
    public void onSubtypeChanged(final InputMethodSubtype newSubtype) {
        if (DBG) {
            Log.w(TAG, "onSubtypeChanged: "
                    + SubtypeLocaleUtils.getSubtypeNameForLogging(newSubtype));
        }

        final Locale newLocale = SubtypeLocaleUtils.getSubtypeLocale(newSubtype);
        final Locale systemLocale = mResources.getConfiguration().locale;
        final boolean sameLocale = systemLocale.equals(newLocale);
        final boolean sameLanguage = systemLocale.getLanguage().equals(newLocale.getLanguage());
    }


    public int getLanguageOnSpacebarFormatType(final InputMethodSubtype subtype) {
        return mLanguageOnSpacebarHelper.getLanguageOnSpacebarFormatType(subtype);
    }

    private static InputMethodSubtype sForcedSubtypeForTesting = null;
    @UsedForTesting
    void forceSubtype(final InputMethodSubtype subtype) {
        sForcedSubtypeForTesting = subtype;
    }

    public InputMethodSubtype getNoLanguageSubtype() {
        Log.w(TAG, "Can't find any language with QWERTY subtype");
        Log.w(TAG, "No input method subtype found; returning dummy subtype: ");
        return mNoLanguageSubtype;
    }

}
