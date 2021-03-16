# TestMInterviewProject
This is a demonstration SDK for scanning a QR code, downloading and playing audio files. The SDK is using the following 3rd party libreries

Retrofit - Network communication

Gson - JSON parsing

Hilt - Dependency Injection

ZXing - Google library for scanning barcodes



## SDK Structure
The SDK is built with the MVVM architecture, supported by LiveData and Coroutines. It invovles 2 sebsequent process:

1. Barcode scanning - The SDK will open the device's camera upon launch and ask the user to scan a QR code

2. After a successful scan, another screen will open for displaying the downloaded audio files. User may now press a file and play its contents
