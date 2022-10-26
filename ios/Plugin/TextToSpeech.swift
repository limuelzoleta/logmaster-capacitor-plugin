import AVFoundation
import Capacitor

@objc public class TextToSpeech: NSObject, AVSpeechSynthesizerDelegate {
    let synthesizer = AVSpeechSynthesizer()
    let audioSession = AVAudioSession.sharedInstance()
    var calls: [CAPPluginCall] = []

    override init() {
        super.init()
        self.synthesizer.delegate = self
    }

    public func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didCancel utterance: AVSpeechUtterance) {
        self.resolveCurrentCall()
    }

    public func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didFinish utterance: AVSpeechUtterance) {
        self.resolveCurrentCall()
    }

    @objc public func speak(_ text: String, _ lang: String, _ rate: Float, _ pitch: Float, _ category: String, _ volume: Float, _ voice: Int, _ call: CAPPluginCall) throws {
        self.synthesizer.stopSpeaking(at: .immediate)

        var avAudioSessionCategory = AVAudioSession.Category.ambient
        if category != "ambient" {
            avAudioSessionCategory = AVAudioSession.Category.playback
        }

        try AVAudioSession.sharedInstance().setCategory(avAudioSessionCategory, mode: .default, options: AVAudioSession.CategoryOptions.duckOthers)
        try AVAudioSession.sharedInstance().setActive(true)

        self.calls.append(call)

        let utterance = AVSpeechUtterance(string: text)
        utterance.voice = AVSpeechSynthesisVoice(language: lang)
        utterance.rate = adjustRate(rate)
        utterance.pitchMultiplier = pitch
        utterance.volume = volume
        synthesizer.speak(utterance)
    }

    @objc public func stop() {
        synthesizer.stopSpeaking(at: .immediate)
    }

    @objc public func getSupportedLanguages() -> [String] {
        return Array(AVSpeechSynthesisVoice.speechVoices().map {
            return $0.language
        })
    }
    
    @objc public func getSupportedVoices() -> [[String: Any]]{
        let allVoices = AVSpeechSynthesisVoice.speechVoices()
        var res: [[String: Any]] = []

        for voice in allVoices {
            let lang = [
                "default": false,
                "lang": voice.language,
                "localService": true,
                "name": voice.name,
                "voiceURI": voice.identifier
            ] as [String : Any]
            res.append(lang)
        }
        return res;
    }

    @objc public func isLanguageSupported(_ lang: String) -> Bool {
        let voice = AVSpeechSynthesisVoice(language: lang)
        return voice != nil
    }

    // Adjust rate for a closer match to other platform.
    @objc private func adjustRate(_ rate: Float) -> Float {
        let baseRate = AVSpeechUtteranceDefaultSpeechRate
        if rate == 1 {
            return baseRate
        }
        if rate > baseRate {
            return baseRate + (rate * 0.025)
        }
        return rate / 2
    }

    @objc private func resolveCurrentCall() {
        do {
            try AVAudioSession.sharedInstance().setActive(false)
        } catch {
            CAPLog.print(error.localizedDescription)
        }
        guard let call = calls.first else {
            return
        }
        call.resolve()
        calls.removeFirst()
    }
    
    @objc public func getSystemVolume() -> Int {
        do{
            try audioSession.setActive(true);
            let volume = audioSession.outputVolume
            return Int(volume * 100)
        } catch {
            return -1
        }
    }
}
