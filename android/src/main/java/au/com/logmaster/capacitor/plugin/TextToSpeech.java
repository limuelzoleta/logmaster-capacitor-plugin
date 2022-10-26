package au.com.logmaster.capacitor.plugin;

import android.media.AudioManager;
import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextToSpeech implements android.speech.tts.TextToSpeech.OnInitListener {

    public static final String LOG_TAG = "TextToSpeech";

    private Context context;
    private final AudioManager audioManager;
    private android.speech.tts.TextToSpeech tts = null;
    private int initializationStatus;
    private JSObject[] supportedVoices = null;

    TextToSpeech(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        try {
            tts = new android.speech.tts.TextToSpeech(context, this);
        } catch (Exception ex) {
            Log.d(LOG_TAG, ex.getLocalizedMessage());
        }
    }

    @Override
    public void onInit(int status) {
        this.initializationStatus = status;
    }

    public void speak(
            String text,
            String lang,
            float rate,
            float pitch,
            float volume,
            int voice,
            String callbackId,
            SpeakResultCallback resultCallback) {
        tts.stop();
        tts.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        resultCallback.onDone();
                    }

                    @Override
                    public void onError(String utteranceId) {
                        resultCallback.onError();
                    }
                });

        Locale locale = Locale.forLanguageTag(lang);
        Set<Voice> supportedVoices = tts.getVoices();
        List<Voice> voiceList = new ArrayList<>(supportedVoices);
        Voice selectedVoice = voiceList.get(voice);
        tts.setVoice(selectedVoice);
        locale = selectedVoice.getLocale();

        if (Build.VERSION.SDK_INT >= 21) {
            Bundle ttsParams = new Bundle();
            ttsParams.putSerializable(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackId);
            ttsParams.putSerializable(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);

            tts.setLanguage(locale);
            tts.setSpeechRate(rate);
            tts.setPitch(pitch);
            tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, ttsParams, callbackId);
        } else {
            HashMap<String, String> ttsParams = new HashMap<>();
            ttsParams.put(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackId);
            ttsParams.put(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME, Float.toString(volume));

            tts.setLanguage(locale);
            tts.setSpeechRate(rate);
            tts.setPitch(pitch);
            tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, ttsParams);
        }
    }

    public void stop() {
        tts.stop();
    }

    public JSArray getSupportedLanguages() {
        ArrayList<String> languages = new ArrayList<>();
        Set<Locale> supportedLocales = tts.getAvailableLanguages();
        for (Locale supportedLocale : supportedLocales) {
            String tag = supportedLocale.toLanguageTag();
            languages.add(tag);
        }
        return JSArray.from(languages.toArray());
    }

    public JSArray getSupportedVoices() {
        ArrayList<JSObject> voices = new ArrayList<>();
        Set<Voice> supportedVoices = tts.getVoices();
        for (Voice supportedVoice : supportedVoices) {
            JSObject obj = this.convertVoiceToJSObject(supportedVoice);
            voices.add(obj);
        }
        return JSArray.from(voices.toArray());
    }

    public void openInstall() {
        PackageManager packageManager = context.getPackageManager();
        Intent installIntent = new Intent();
        installIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        ResolveInfo resolveInfo = packageManager.resolveActivity(installIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null) {
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(installIntent);
        }
    }

    public boolean isAvailable() {
        if (tts != null && initializationStatus == android.speech.tts.TextToSpeech.SUCCESS) {
            return true;
        }
        return false;
    }

    public boolean isLanguageSupported(String lang) {
        Locale locale = Locale.forLanguageTag(lang);
        int result = tts.isLanguageAvailable(locale);
        return result == tts.LANG_AVAILABLE || result == tts.LANG_COUNTRY_AVAILABLE
                || result == tts.LANG_COUNTRY_VAR_AVAILABLE;
    }

    public int getSystemVolume() {
        int volume = this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return mapVolumeValue(volume, 0, 100);
    }

    public void onDestroy() {
        if (tts == null) {
            return;
        }
        tts.stop();
        tts.shutdown();
    }

    private JSObject convertVoiceToJSObject(Voice voice) {
        Locale locale = voice.getLocale();
        JSObject obj = new JSObject();
        obj.put("voiceURI", voice.getName());
        obj.put("name", locale.getDisplayLanguage() + " " + locale.getDisplayCountry());
        obj.put("lang", locale.toLanguageTag());
        obj.put("localService", !voice.isNetworkConnectionRequired());
        obj.put("default", false);
        return obj;
    }

    private int mapVolumeValue(int volume, double targetMin, double targetMax) {
        double maxVol = (double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        double minVol = 0;

        double scale = (targetMax - targetMin) / (maxVol - minVol);
        double output = scale * (volume - minVol);
        return (int) Math.round(output);
    }
}
