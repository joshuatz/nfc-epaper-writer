# NFC EInk Writer
> An Android app to generate images for, and flash to, WaveShare Passive NFC Powered (aka *parasitic*) EInk displays, like [this one](https://www.waveshare.com/2.9inch-nfc-powered-e-paper.htm):

![WaveShare 2.9" Passive NFC-Powered EInk Display, showing the text "Hello World" with a waving hand emoji at the end](https://user-images.githubusercontent.com/17817563/118736344-32156480-b7f7-11eb-9a03-7d5b7c878c30.jpg)

## Features
With this app you can flash images, custom text, or even free-form graphics to a WaveShare NFC EInk device:

- ***Text***: Use the built-in generator tool to create a bitmap from any text you want. Even supports Emoji
- ***Images***: Select a picture saved on your phone to crop and flash, or capture a new one with your camera.
- ***Graphics***: Draw something on your phone and have it ready to flash in seconds. Uses [JSPaint](https://jspaint.app/) as the WYSIWYG editor.

> This app should support all screen sizes supported by [the WaveShare SDK](https://www.waveshare.com/wiki/Android_SDK_for_NFC-Powered_e-Paper), but has only been tested with the 2.9" so far. The app will remember the last used size on startup.

## Demos
Flashing a local image file:

![Animated GIF showing this application letting a user select a local image file from their gallery, cropping it, and then flashing it to the EInk NFC display](https://user-images.githubusercontent.com/17817563/118732297-e2329f80-b7ee-11eb-9f5c-16b2872d6bf6.gif)


Flashing Text:

![Animated GIF showing the user being able to input custom text, having the text captured as an image, and then flashing the resulting image to the EInk NFC Display](https://user-images.githubusercontent.com/17817563/118735056-7eab7080-b7f4-11eb-9d11-60d2aa58efe4.gif)

Creating and flashing with a WYSIWYG editor:

![Animated GIF showing user creating a custom image via WYSIWYG paint editor, having the image captured to bitmap, and then flashing the generated bitmap to the EInk NFC Display](https://user-images.githubusercontent.com/17817563/118734322-ff696d00-b7f2-11eb-947d-dc844c259518.gif)

## Known Issues
NFC can be a little finnicky, and especially with these EInk displays. Depending on the power and capabilities of your phone, it may take time perfecting the exact distance and position to hold your phone in proximity to the EInk display in order to get successful flashes.

On certain Android phones, you might also see a high rate of your NFC radio / chipset randomly *"dying"*. This happens at a lower level of system APIs, so it is really hard for my application to detect or attempt to recover from.

> When detected by the lower-level APIs, Android will throw this as a `android.os.DeadObjectException`, with the entry: `NFC service dead - attempting to recover`. You can see the internal recovery efforts [here](https://github.com/aosp-mirror/platform_frameworks_base/blob/9635abafa0053c65e04b93da16c72da8af371454/core/java/android/nfc/NfcAdapter.java#L831-L865).

Additionally, sometimes you might see corrupted writes, where something goes wrong during the transceiving process and the display ends up with random noise:

![Animated GIF showing a failed flash, with random noise appearing over the previously flashed image](https://user-images.githubusercontent.com/17817563/118723223-fde37900-b7e1-11eb-8b0c-c12ba4387d27.gif)

## Technical Details
Building this project was my first time touching Kotlin, Java, or Android APIs, of which this project uses all three. I opted to go this route (native Android dev) instead of React Native or Flutter, because I knew I was going to need access to a lot of lower level APIs, and saw it as an opportunity to learn some new skills.

This project uses a bunch of different technologies, and takes some interesting "shortcuts":

- For the custom image generation options - both the text editor or WYSIWYG editor - I used WebView so that I could use HTML + JS + Web Canvas, and pass back the bitmap data to Android
	- The WYSIWYG editor is actually just [JSPaint](https://jspaint.app/), but with injected JavaScript for capturing the bitmap data from the app's canvas
	- The text editor is a custom tiny webpage I put together that renders the text to a Canvas element, and then captures the raw bitmap data
- The local image option uses [CanHub/Android-Image-Cropper](https://github.com/CanHub/Android-Image-Cropper) for cropping and resizing
- By using scoped storage and the right APIs, no special permissions (other than NFC and Internet) are required from the User.
- For actually sending the bitmap data over the NFC protocol, this uses the [WaveShare Android SDK](https://www.waveshare.com/wiki/Android_SDK_for_NFC-Powered_e-Paper), and the JAR file that they provided.
- Kotlin coroutines are used throughout, as there are a lot of operations that are blocking in nature (the main transceive operation is basically one long blocking sequence).

> If you are interested in the WaveShare module itself, I will likely have a blog post coming soon with some more details.

## Where to Get It
Currently, this is not published to the App Store. If I have time, I might work on that avenue. For now, I'm side-loading it after building it, which anyone else is free to as well.

## Backlog

- Mirror tag write exceptions to UI (right now just in console)
- Look for better recovery methods for NFC adapter dying
- When saving generated image, prefix or suffix with resolution, and then only allow cached image for re-flashing if saved resolution matches
- App Icon
- Publish and/or provide sideload instructions

## About Me
More About Me (Joshua Tzucker):

 - 🔗<a href="https://joshuatz.com/" rel="noopener" target="_blank">joshuatz.com</a>
 - 💬<a href="https://twitter.com/1joshuatz" rel="noopener" target="_blank">@1joshuatz</a>
 - 💾<a href="https://github.com/joshuatz" rel="noopener" target="_blank">github.com/joshuatz</a>