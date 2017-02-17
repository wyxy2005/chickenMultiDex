package com.koodroid.chicken;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class ARHelper {
    public static final String TAG = "chicken";

    // getPackageManager
    private static final String METHOD_NAME_1 = "Z2V0UGFja2FnZU1hbmFnZXI=";

    // getPackageName
    private static final String METHOD_NAME_2 = "Z2V0UGFja2FnZU5hbWU=";

    // getPackageInfo
    private static final String METHOD_NAME_3 = "Z2V0UGFja2FnZUluZm8=";

    // toByteArray
    private static final String METHOD_NAME_4 = "dG9CeXRlQXJyYXk=";

    // signatures
    private static final String FIELD_NAME_1 = "c2lnbmF0dXJlcw==";

    // android.app.AlertDialog$Builder
    private static final String CLASS_NAME_1 = "YW5kcm9pZC5hcHAuQWxlcnREaWFsb2ckQnVpbGRlcg==";

    // setTitle
    private static final String METHOD_NAME_5 = "c2V0VGl0bGU=";

    // setMessage
    private static final String METHOD_NAME_6 = "c2V0TWVzc2FnZQ==";

    // setPositiveButton
    private static final String METHOD_NAME_7 = "c2V0UG9zaXRpdmVCdXR0b24=";

    // create
    private static final String METHOD_NAME_8 = "Y3JlYXRl";

    // getWindow
    private static final String METHOD_NAME_9 = "Z2V0V2luZG93";

    // setBackgroundDrawableResource
    private static final String METHOD_NAME_10 = "c2V0QmFja2dyb3VuZERyYXdhYmxlUmVzb3VyY2U=";

    // show
    private static final String METHOD_NAME_11 = "c2hvdw==";

    // void setOnDismissListener
    private static final String METHOD_NAME_12 = "c2V0T25EaXNtaXNzTGlzdGVuZXI=";

    public static void checkPackageValidaty(final Context context) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                return checkSignInfo(context);
            };

            protected void onPostExecute(Boolean result) {
                if (!result) {
                    showDlg(context);
                }
            };
        }.execute();
    }

    private static void showDlg(final Context context) {
        // 以下代码相当于：
        // AlertDialog.Builder ab = new AlertDialog.Builder(context,
        // android.R.style.Theme_DeviceDefault_Light_Dialog);
        // ab.setTitle("警告");
        // ab.setMessage("检测到您所使用的软件不是官方正版软件，有可能被植入了恶意代码，侵害您的设备或者隐私");
        // ab.setPositiveButton();
        // ab.setOnDismissListener();
        // AlertDialog ad = ab.create();
        // ad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // ad.show();

        Object obj = getInstance(decodeBase64String(CLASS_NAME_1), new Class[] {
                Context.class, int.class
        }, new Object[] {
                context, android.R.style.Theme_DeviceDefault_Light_Dialog
        });

        invokeMethod(obj, decodeBase64String(METHOD_NAME_5), android.R.string.dialog_alert_title,
                int.class);
        invokeMethod(
                obj,
                decodeBase64String(METHOD_NAME_6),
                getErrorMessage(context),
                CharSequence.class);

        invokeMethod(obj, decodeBase64String(METHOD_NAME_7), android.R.string.ok, int.class, null,
                OnClickListener.class);

        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        };

        invokeMethod(obj, decodeBase64String(METHOD_NAME_12), dismissListener,
                OnDismissListener.class);

        obj = invokeMethod(obj, decodeBase64String(METHOD_NAME_8));
        Object obj2 = invokeMethod(obj, decodeBase64String(METHOD_NAME_9));
        invokeMethod(obj2, decodeBase64String(METHOD_NAME_10), android.R.color.transparent,
                int.class);
        invokeMethod(obj, decodeBase64String(METHOD_NAME_11));
    }

    private static boolean checkSignInfo(Context context) {

    	return true;
//        try {
//            // 以下代码等价于：
//            // PackageInfo pi =
//            // context.getPackageManager().getPackageInfo(context.getPackageName(),
//            // PackageManager.GET_SIGNATURES);
//            // Signature[] signs = pi.signatures;
//            // Signature sign = signs[0];
//            // String strSign = Utils.getMD5(sign.toByteArray());
//
//            // Package Manager
//            Object obj = invokeMethod(context, decodeBase64String(METHOD_NAME_1));
//
//            // Package Name
//            String pn = (String) invokeMethod(context, decodeBase64String(METHOD_NAME_2));
//
//            // PackageInfo
//            obj = invokeMethod(obj, decodeBase64String(METHOD_NAME_3), pn, String.class,
//                    PackageManager.GET_SIGNATURES, int.class);
//
//            // signatures
//            Object[] objs = (Object[]) getField(obj, decodeBase64String(FIELD_NAME_1));
//
//            // signature
//            obj = objs[0];
//
//            // signature byte array
//            byte[] bytes = (byte[]) invokeMethod(obj, decodeBase64String(METHOD_NAME_4));
//
//            // signature string
//            String strSign = getMD5(bytes);
//            
//            //Log.d("daniel", strSign);
//
//            // 正式签名：6e4d19080670928a3b408642353d74d9
//            // 测试签名：e7369c25f8f301357b0f17f65a4eb38c
//            return strSign.equals(decodeBase64String("NmU0ZDE5MDgwNjcwOTI4YTNiNDA4NjQyMzUzZDc0ZDk="))
//                    || strSign.equals(decodeBase64String("ZTczNjljMjVmOGYzMDEzNTdiMGYxN2Y2NWE0ZWIzOGM="))
//                    || strSign.equals(decodeBase64String("NTA5YzMxMGI5N2RlMWYyZGUwNGJlNGZjMDk0ZjM4NzE="));
//
//        } catch (Exception e) {
//            return true;
//        }
    }

    private static final String decodeBase64String(String string) {
        return new String(Base64.decode(string, Base64.DEFAULT));
    }

    private static String getErrorMessage(Context context) {
        Locale loc = Locale.getDefault();
        if (loc.getLanguage().equalsIgnoreCase("zh")) {
            /**
             * 检测到您所使用的软件不是官方正版软件，有可能被植入了恶意代码，侵害您的设备或者隐私，
             */
            return decodeBase64String("5qOA5rWL5Yiw5oKo5omA5L2/55So55qE5b+r5LmQ5bCP6bih5LiN5piv55qE5a6Y5pa55q2j54mI6L2v5Lu277yM5pyJ5Y+v6IO96KKr5qSN5YWl5LqG5oG25oSP5Luj56CB77yM5L615a6z5oKo55qE6K6+5aSH5oiW6ICF6ZqQ56eB77yM5bu66K6u5oKo5Y675bqU55So5a6d5LiL6L295q2j54mI6L2v5Lu244CC");
        } else {
            /**
             * We've detected that the software you are using is not a genuine
             * */
            return decodeBase64String("V2UmIzM5O3ZlIGRldGVjdGVkIHRoYXQgdGhlIHNvZnR3YXJlIHlvdSBhcmUgdXNpbmcgaXMgbm90IGEgZ2VudWluZSwgd2hpY2ggbWF5IGNhdXNlIGRpc2Nsb3N1cmUgb2YgeW91ciBwcml2YWN5IGFuZCBoYXJtZnVsIHRvIHlvdXIgZGV2aWNlLiBXZSBzdHJvbmdseSBzdWdnZXN0IHlvdSB0byBkb3dubG9hZCBhIGdlbnVpbmUgdmVyc2lvbiBvZiB0aGlzIHByb2R1Y3Qu");
        }
    }

    private static Object invokeMethod(final Object obj, final String methodName) {
        final Class<? extends Object> cls = obj.getClass();
        try {
            final Method method = cls.getMethod(methodName);
            Object ret = method.invoke(obj);
            return ret;
        } catch (final Exception e) {
                Log.e(TAG, "Error invoking method:", e);
            
        }

        return null;
    }

    private static Object invokeMethod(final Object obj, final String methodName,
            final Object param, final Class<? extends Object> paramCls) {
        final Class<? extends Object> cls = obj.getClass();
        try {
            final Method method = cls.getMethod(methodName, paramCls);
            Object ret = method.invoke(obj, param);
            return ret;
        } catch (final Exception e) {
                Log.e(TAG, "Error invoking method:", e);
            
        }

        return null;
    }

    private static Object invokeMethod(final Object obj, final String methodName,
            final Object param1, final Class<? extends Object> paramCls1, final Object param2,
            final Class<? extends Object> paramCls2) {
        final Class<? extends Object> cls = obj.getClass();
        try {
            final Method method = cls.getMethod(methodName, paramCls1, paramCls2);
            Object ret = method.invoke(obj, param1, param2);
            return ret;
        } catch (final Exception e) {
                Log.e(TAG, "Error invoking method:", e);
            
        }

        return null;
    }

    private static Object getField(final Object obj, final String fieldName) {
        try {
            Field f = obj.getClass().getField(fieldName);
            return f.get(obj);
        } catch (final Exception e) {
                Log.e(TAG, "Error getting field:", e);
        }
        return null;
    }

    private static Object getInstance(String name, Class<?> classParas[], Object paras[]) {
        Object o = null;
        try {
            Class<?> c = Class.forName(name);
            Constructor<?> con = c.getConstructor(classParas);
            o = con.newInstance(paras);
        } catch (Exception ex) {
        }

        return o;
    }

    private static String getMD5(byte[] input) {
        return bytesToHexString(MD5(input));
    }

    private static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        final String table = "0123456789abcdef";
        final StringBuilder ret = new StringBuilder(2 * bytes.length);

        for (final byte c : bytes) {
            int b;
            b = 0x0f & (c >> 4);
            ret.append(table.charAt(b));
            b = 0x0f & c;
            ret.append(table.charAt(b));
        }

        return ret.toString();
    }

    private static byte[] MD5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException e) {
                Log.e(TAG, "", e);
            
        }
        if (md != null) {
            md.update(input);
            return md.digest();
        } else {
            return null;
        }
    }
}