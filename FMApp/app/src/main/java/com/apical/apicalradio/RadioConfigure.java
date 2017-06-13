package com.apical.apicalradio;

//import android.app.DvdControl;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class RadioConfigure
{
    private static final String TAG = "qulinglingRadio DvdConfigurator***";

    public static final byte LANGUAGE_CODE_SETTINGS = 0x01;
    public static final byte VIDEO_SETTINGS = 0x02;
    public static final byte AUDIO_SETTINGS = 0x04;
    public static final byte ALL_SETTINGS = 0x07;

    public static final short LANGUAGE_CODE_OFF = 0;
    public static final short LANGUAGE_CODE_AUTO = 1;
    public static final short LANGUAGE_CODE_ENGLISH = 25966;
    public static final short LANGUAGE_CODE_FRENCH = 26226;

    public static final byte TV_SHAPE_43 = 0;
    public static final byte TV_SHAPE_169 = 1;

    public static final byte VIEW_MODE_FILL = 0;
    public static final byte VIEW_MODE_ORIGINAL = 1;
    public static final byte VIEW_MODE_HEIGHT_FIT = 2;
    public static final byte VIEW_MODE_WIDTH_FIT = 3;
    public static final byte VIEW_MODE_AUTO_FIT = 4;

    public static final byte UC_3D_EFFECT_OFF = 0;
    public static final byte UC_3D_EFFECT_ON = 1;

    public static final byte UC_DRC_CTRL_OFF = 0;
    public static final byte UC_DRC_CTRL_ON = 1;

    public static final String KEY_DVD_MENU_LANGUAGE = "dvd_menu_language";
    public static final String KEY_AUDIO_LANGUAGE = "audio_language";
    public static final String KEY_SUBTITLE_LANGUAGE = "subtitle_language";
    public static final String KEY_OSD_MENU_LANGUAGE = "osd_menu_language";

    public static final String KEY_TV_SHAPE = "tv_shape";
    public static final String KEY_VIEW_MODE = "view_mode";

    public static final String KEY_UC_3D_EFFECT = "uc_3d_effect";
    public static final String KEY_UC_DRC_CTRL = "uc_drc_ctrl";

    private SharedPreferences mPreferences;
    private RadioController mRadioController;

    public RadioConfigure(Context context)
    {
        Log.d(TAG, "DvdConfigurator(" + context + ")");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mRadioController = new RadioController(context);
    }

    public RadioController getRadioController()
    {
        Log.d(TAG, "getDvdController()");
        return mRadioController;
    }

    public void onEvent_SYS_SETUP(Bundle data)
    {
        Log.d(TAG, "onEvent_SYS_SETUP(" + data + ")");
        byte cmd = data.getByte("cmd");
//        if (cmd != DvdControl.NATIVE_CMD_RET_DVDSETTING)
//        {
//            return;
//        }
        Editor editor = mPreferences.edit();
        byte type = data.getByte("setting_type");
        Log.d(TAG, "setting_type=" + type);
        if ((type & LANGUAGE_CODE_SETTINGS) != 0)
        {
            String dvd_menu_language = String.valueOf(data.getShort(KEY_DVD_MENU_LANGUAGE));
            Log.d(TAG, "dvd_menu_language=" + dvd_menu_language);
            String audio_language = String.valueOf(data.getShort(KEY_AUDIO_LANGUAGE));
            Log.d(TAG, "audio_language=" + audio_language);
            String subtitle_language = String.valueOf(data.getShort(KEY_SUBTITLE_LANGUAGE));
            Log.d(TAG, "subtitle_language=" + subtitle_language);
            String osd_menu_language = String.valueOf(data.getShort(KEY_OSD_MENU_LANGUAGE));
            Log.d(TAG, "osd_menu_language=" + osd_menu_language);
            editor.putString(KEY_DVD_MENU_LANGUAGE, dvd_menu_language).putString(
                    KEY_AUDIO_LANGUAGE, audio_language).putString(KEY_SUBTITLE_LANGUAGE,
                    subtitle_language).putString(KEY_OSD_MENU_LANGUAGE, osd_menu_language);
        }
        if ((type & VIDEO_SETTINGS) != 0)
        {
            String tv_shape = String.valueOf(data.getByte(KEY_TV_SHAPE));
            Log.d(TAG, "tv_shape=" + tv_shape);
            String view_mode = String.valueOf(data.getByte(KEY_VIEW_MODE));
            Log.d(TAG, "view_mode=" + view_mode);
            editor.putString(KEY_TV_SHAPE, tv_shape).putString(KEY_VIEW_MODE, view_mode);
        }
        if ((type & AUDIO_SETTINGS) != 0)
        {
            boolean uc_3d_effect = (data.getByte(KEY_UC_3D_EFFECT) == 1);
            Log.d(TAG, "uc_3d_effect=" + uc_3d_effect);
            boolean uc_drc_ctrl = (data.getByte(KEY_UC_DRC_CTRL) == 1);
            Log.d(TAG, "uc_drc_ctrl=" + uc_drc_ctrl);
            editor.putBoolean(KEY_UC_3D_EFFECT, uc_3d_effect).putBoolean(KEY_UC_DRC_CTRL,
                    uc_drc_ctrl);
        }
        editor.commit();
    }

    public void LoadDvdSettings(byte type)
    {
        Log.d(TAG, "LoadDvdSettings(" + type + ")");
        mRadioController.getSystemSetup(type);
    }

    public void SaveDvdSettings(byte type)
    {
        Log.d(TAG, "SaveDvdSettings(" + type + ")");
        Bundle param = new Bundle();
        param.putByte("type", type);
        if ((type & LANGUAGE_CODE_SETTINGS) != 0)
        {
            short dvd_menu_language = Short.parseShort(mPreferences.getString(
                    KEY_DVD_MENU_LANGUAGE, String.valueOf(LANGUAGE_CODE_ENGLISH)));
            param.putShort(KEY_DVD_MENU_LANGUAGE, dvd_menu_language);
            short audio_language = Short.parseShort(mPreferences.getString(
                    KEY_AUDIO_LANGUAGE, String.valueOf(LANGUAGE_CODE_ENGLISH)));
            param.putShort(KEY_AUDIO_LANGUAGE, audio_language);
            short subtitle_language = Short.parseShort(mPreferences.getString(
                    KEY_SUBTITLE_LANGUAGE, String.valueOf(LANGUAGE_CODE_ENGLISH)));
            param.putShort(KEY_SUBTITLE_LANGUAGE, subtitle_language);
            short osd_menu_language = Short.parseShort(mPreferences.getString(
                    KEY_OSD_MENU_LANGUAGE, String.valueOf(LANGUAGE_CODE_ENGLISH)));
            param.putShort(KEY_OSD_MENU_LANGUAGE, osd_menu_language);
        }
        if ((type & VIDEO_SETTINGS) != 0)
        {
            byte tv_shape = Byte.parseByte(mPreferences.getString(KEY_TV_SHAPE,
                    String.valueOf(TV_SHAPE_43)));
            param.putByte(KEY_TV_SHAPE, tv_shape);
            byte view_mode = Byte.parseByte(mPreferences.getString(KEY_VIEW_MODE,
                    String.valueOf(VIEW_MODE_AUTO_FIT)));
            param.putByte(KEY_VIEW_MODE, view_mode);
        }
        if ((type & AUDIO_SETTINGS) != 0)
        {
            byte uc_3d_effect = (byte)(mPreferences.getBoolean(KEY_UC_3D_EFFECT, false) == false
                    ? UC_3D_EFFECT_OFF : UC_3D_EFFECT_ON);
            param.putByte(KEY_UC_3D_EFFECT, uc_3d_effect);
            byte uc_drc_ctrl = (byte)((mPreferences.getBoolean(KEY_UC_DRC_CTRL, false) == false)
                    ? UC_DRC_CTRL_OFF : UC_DRC_CTRL_ON);
            param.putByte(KEY_UC_DRC_CTRL, uc_drc_ctrl);
        }
        mRadioController.setSystemSetup(param);
    }

    public void RestoreFactorySettings()
    {
        Log.d(TAG, "RestoreFactorySettings()");
        Editor editor = mPreferences.edit();
        Bundle param = new Bundle();
        param.putByte("type", ALL_SETTINGS);

        short dvd_menu_language = LANGUAGE_CODE_ENGLISH;
        param.putShort(KEY_DVD_MENU_LANGUAGE, dvd_menu_language);
        short audio_language = LANGUAGE_CODE_ENGLISH;
        param.putShort(KEY_AUDIO_LANGUAGE, audio_language);
        short subtitle_language = LANGUAGE_CODE_ENGLISH;
        param.putShort(KEY_SUBTITLE_LANGUAGE, subtitle_language);
        short osd_menu_language = LANGUAGE_CODE_ENGLISH;
        param.putShort(KEY_OSD_MENU_LANGUAGE, osd_menu_language);
        editor.putString(KEY_DVD_MENU_LANGUAGE, String.valueOf(dvd_menu_language)).putString(
                KEY_AUDIO_LANGUAGE, String.valueOf(audio_language)).putString(
                KEY_SUBTITLE_LANGUAGE, String.valueOf(subtitle_language)).putString(
                KEY_OSD_MENU_LANGUAGE, String.valueOf(osd_menu_language));

        byte tv_shape = TV_SHAPE_43;
        param.putByte(KEY_TV_SHAPE, tv_shape);
        byte view_mode = VIEW_MODE_AUTO_FIT;
        param.putByte(KEY_VIEW_MODE, view_mode);
        editor.putString(KEY_TV_SHAPE, String.valueOf(tv_shape)).putString(KEY_VIEW_MODE,
                String.valueOf(view_mode));

        byte uc_3d_effect = UC_3D_EFFECT_OFF;
        param.putByte(KEY_UC_3D_EFFECT, uc_3d_effect);
        byte uc_drc_ctrl = UC_DRC_CTRL_OFF;
        param.putByte(KEY_UC_DRC_CTRL, uc_drc_ctrl);
        editor.putBoolean(KEY_UC_3D_EFFECT, false).putBoolean(KEY_UC_DRC_CTRL, false);

        editor.commit();
        mRadioController.setSystemSetup(param);
    }

    public static String getEntryFromValue(String[] entryArray, String[] valueArray,
            String value)
    {
        for (int i = 0; i < valueArray.length; i++)
        {
            if (valueArray[i].equals(value))
            {
                return entryArray[i];
            }
        }
        return "";
    }
}
