package com.carloz.inputmethod.utils;

import com.carloz.inputmethod.flag.DebugFlags;

public class LogUtils {

	public interface Callback {
		public Callback getLogUtilsCallback();

		public String getTag();

		public boolean getDebug();
	}

	public static void d(Callback callback, String msg) {
		if (DebugFlags.DEBUG_ENABLED && callback.getDebug())
			android.util.Log.d(callback.getTag(), msg);
	}

	public static void e(Callback callback, String msg) {
		if (DebugFlags.DEBUG_ENABLED && callback.getDebug())
			android.util.Log.e(callback.getTag(), msg);
	}

	public static void i(Callback callback, String msg) {
		if (DebugFlags.DEBUG_ENABLED && callback.getDebug())
			android.util.Log.i(callback.getTag(), msg);
	}

}
