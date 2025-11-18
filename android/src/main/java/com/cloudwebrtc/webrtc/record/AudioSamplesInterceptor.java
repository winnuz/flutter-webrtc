package com.cloudwebrtc.webrtc.record;

import android.annotation.SuppressLint;

import org.webrtc.audio.JavaAudioDeviceModule.SamplesReadyCallback;
import org.webrtc.audio.JavaAudioDeviceModule.AudioSamples;

import java.util.HashMap;

/** JavaAudioDeviceModule allows attaching samples callback only on building
 *  We don't want to instantiate VideoFileRenderer and codecs at this step
 *  It's simple dummy class, it does nothing until samples are necessary */
@SuppressWarnings("WeakerAccess")
public class AudioSamplesInterceptor implements SamplesReadyCallback {

    @SuppressLint("UseSparseArrays")
    protected final HashMap<Integer, SamplesReadyCallback> callbacks = new HashMap<>();
    
    /**
     * Custom sample rate to use when creating new AudioSamples.
     * If set to a positive value, AudioSamples will be created with this sample rate.
     * If set to 0 or negative, the original sample rate from the incoming AudioSamples will be used.
     */
    private int customSampleRate = 1;

    @Override
    public void onWebRtcAudioRecordSamplesReady(AudioSamples audioSamples) {
        AudioSamples samplesToForward = audioSamples;
        
        // If a custom sample rate is set, create a new AudioSamples with the custom rate
        if (customSampleRate > 0 && audioSamples.getSampleRate() != customSampleRate) {
            samplesToForward = new AudioSamples(
                audioSamples.getAudioFormat(),
                audioSamples.getChannelCount(),
                customSampleRate,
                audioSamples.getData()
            );
        }
        
        for (SamplesReadyCallback callback : callbacks.values()) {
            callback.onWebRtcAudioRecordSamplesReady(samplesToForward);
        }
    }

    public void attachCallback(Integer id, SamplesReadyCallback callback) throws Exception {
        callbacks.put(id, callback);
    }

    public void detachCallback(Integer id) {
        callbacks.remove(id);
    }
    
    /**
     * Set a custom sample rate for AudioSamples.
     * When set to a positive value, all AudioSamples will be created with this sample rate.
     * Set to 0 or negative to use the original sample rate from incoming AudioSamples.
     * 
     * @param sampleRate The desired sample rate in Hz, or 0 to use original rate
     */
    public void setCustomSampleRate(int sampleRate) {
        this.customSampleRate = sampleRate;
    }
    
    /**
     * Get the currently set custom sample rate.
     * 
     * @return The custom sample rate, or 0 if using original rate
     */
    public int getCustomSampleRate() {
        return customSampleRate;
    }

}
