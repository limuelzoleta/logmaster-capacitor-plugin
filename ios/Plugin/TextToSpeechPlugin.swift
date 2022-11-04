import AVFoundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(TextToSpeechPlugin)
public class TextToSpeechPlugin: CAPPlugin {
    private static let errorUnsupportedLanguage = "This language is not supported."

    private let implementation = TextToSpeech()

    @objc public func speak(_ call: CAPPluginCall) {
        let text = call.getString("text") ?? ""
        let lang = call.getString("lang") ?? "en-US"
        let rate = call.getFloat("rate") ?? 1.0
        let pitch = call.getFloat("pitch") ?? 1.0
        let volume = call.getFloat("volume") ?? 1.0
        let category = call.getString("category") ?? "playback"
        let voice = call.getString("voice") ?? ""
        let playOnSilent = call.getBool("playOnSilent") ?? true

        let isLanguageSupported = implementation.isLanguageSupported(lang)
        guard isLanguageSupported else {
            call.reject(TextToSpeechPlugin.errorUnsupportedLanguage)
            return
        }

        do {
            try implementation.speak(text, lang, rate, pitch, category, volume, voice, call)
        } catch {
            call.reject(error.localizedDescription)
        }
    }

    @objc public func stop(_ call: CAPPluginCall) {
        implementation.stop()
        call.resolve()
    }

    @objc public func openInstall(_ call: CAPPluginCall) {
        call.resolve()
    }

    @objc func getSupportedLanguages(_ call: CAPPluginCall) {
        let languages = self.implementation.getSupportedLanguages()
        call.resolve([
            "languages": languages
        ])
    }

    @objc func getSupportedVoices(_ call: CAPPluginCall) {
        let voices: [[String: Any]] = implementation.getSupportedVoices();
        call.resolve([
            "voices": voices
        ])
    }

    @objc func isLanguageSupported(_ call: CAPPluginCall) {
        let lang = call.getString("lang") ?? ""
        let isLanguageSupported = self.implementation.isLanguageSupported(lang)
        call.resolve([
            "supported": isLanguageSupported
        ])
    }
    
    @objc func getSystemVolume(_ call: CAPPluginCall) {
        let volume = implementation.getSystemVolume();
        call.resolve(["volume":volume]);
    }
}
