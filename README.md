# logmaster-capacitor-plugin

Logmaster Capacitor plugin

## Install

```bash
npm install logmaster-capacitor-plugin
npx cap sync
```

## API

<docgen-index>

* [`speak(...)`](#speak)
* [`stop()`](#stop)
* [`getSupportedLanguages()`](#getsupportedlanguages)
* [`getSupportedVoices()`](#getsupportedvoices)
* [`getSystemVolume()`](#getsystemvolume)
* [`isLanguageSupported(...)`](#islanguagesupported)
* [`openInstall()`](#openinstall)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### speak(...)

```typescript
speak(options: TTSOptions) => Promise<void>
```

Starts the TTS engine and plays the desired text.

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#ttsoptions">TTSOptions</a></code> |

--------------------


### stop()

```typescript
stop() => Promise<void>
```

Stops the TTS engine.

--------------------


### getSupportedLanguages()

```typescript
getSupportedLanguages() => Promise<{ languages: string[]; }>
```

Returns a list of supported BCP 47 language tags.

**Returns:** <code>Promise&lt;{ languages: string[]; }&gt;</code>

--------------------


### getSupportedVoices()

```typescript
getSupportedVoices() => Promise<{ voices: SpeechSynthesisVoice[]; }>
```

Returns a list of supported voices.

**Returns:** <code>Promise&lt;{ voices: SpeechSynthesisVoice[]; }&gt;</code>

--------------------


### getSystemVolume()

```typescript
getSystemVolume() => Promise<Volume>
```

Returns device volume information between 0 to 100

**Returns:** <code>Promise&lt;<a href="#volume">Volume</a>&gt;</code>

--------------------


### isLanguageSupported(...)

```typescript
isLanguageSupported(options: { lang: string; }) => Promise<{ supported: boolean; }>
```

Checks if a specific BCP 47 language tag is supported.

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ lang: string; }</code> |

**Returns:** <code>Promise&lt;{ supported: boolean; }&gt;</code>

--------------------


### openInstall()

```typescript
openInstall() => Promise<void>
```

Verifies proper installation and availability of resource files on the system.

Only available for Android.

--------------------


### Interfaces


#### TTSOptions

| Prop           | Type                | Description                                                                                                                                                                                        |
| -------------- | ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`text`**     | <code>string</code> | The text that will be synthesised when the utterance is spoken.                                                                                                                                    |
| **`lang`**     | <code>string</code> | The language of the utterance. Possible languages can be queried using `getSupportedLanguages`. Default: `en-US`.                                                                                  |
| **`rate`**     | <code>number</code> | The speed at which the utterance will be spoken at. Default: `1.0`.                                                                                                                                |
| **`pitch`**    | <code>number</code> | The pitch at which the utterance will be spoken at. Default: `1.0`.                                                                                                                                |
| **`volume`**   | <code>number</code> | The volume that the utterance will be spoken at. Default: `1.0`.                                                                                                                                   |
| **`voice`**    | <code>string</code> | The name or voiceURI of the selected voice that will be used to speak the utterance. Possible voices can be queried using `getSupportedVoices`. Only available for Web.                            |
| **`category`** | <code>string</code> | Select the iOS Audio session category. Possible values: `ambient` and `playback`. Use `playback` to play audio even when the app is in the background. Only available for iOS. Default: `ambient`. |


#### SpeechSynthesisVoice

The <a href="#speechsynthesisvoice">SpeechSynthesisVoice</a> interface represents a voice that the system supports.

| Prop               | Type                 | Description                                                                                                                                                  |
| ------------------ | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`default`**      | <code>boolean</code> | Specifies whether the voice is the default voice for the current app (`true`) or not (`false`).                                                              |
| **`lang`**         | <code>string</code>  | BCP 47 language tag indicating the language of the voice. Example: `en-US`.                                                                                  |
| **`localService`** | <code>boolean</code> | Specifies whether the voice is supplied by a local (`true`) or remote (`false`) speech synthesizer service.                                                  |
| **`name`**         | <code>string</code>  | Human-readable name that represents the voice. Example: `Microsoft Zira Desktop - English (United States)`.                                                  |
| **`voiceURI`**     | <code>string</code>  | Type of URI and location of the speech synthesis service for this voice. Example: `urn:moz-tts:sapi:Microsoft Zira Desktop - English (United States)?en-US`. |


#### Volume

| Prop         | Type                |
| ------------ | ------------------- |
| **`volume`** | <code>number</code> |

</docgen-api>
