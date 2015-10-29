package com.carloz.inputmethod;

import android.inputmethodservice.InputMethodService;
import android.view.ViewGroup.LayoutParams;

import com.carloz.inputmethod.keyboard.KeyboardView;
import com.carloz.inputmethod.utils.JniUtils;

public class CarloZIME extends InputMethodService {
	// Loading the native library eagerly to avoid unexpected
	// UnsatisfiedLinkError at the initial
	// JNI call as much as possible.
	static {
		JniUtils.loadNativeLibrary();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		KeyboardView kv = new KeyboardView(this, null);
		kv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 400));
		setInputView(kv);

	}
}
