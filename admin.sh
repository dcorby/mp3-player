echo "rm ~/.android/avd/Pixel_XL_API_33.avd/*.lock"
echo "mksdcard -l mySdCard 1024M mySdCardFile.img"
echo "export PATH=/home/dmc7z/Android/Sdk/emulator:\$PATH"
echo "adb shell"
echo "sudo adb push 52096f49 /sdcard/Android/data/com.example.media/"
echo "emulator -avd Pixel_XL_API_33"

