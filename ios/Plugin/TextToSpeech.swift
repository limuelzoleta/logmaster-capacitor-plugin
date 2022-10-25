import Foundation

@objc public class TextToSpeech: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
