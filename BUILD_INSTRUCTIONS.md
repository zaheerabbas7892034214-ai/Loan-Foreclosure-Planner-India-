# Build Instructions for Loan Foreclosure Planner (India)

## Quick Start Guide

### Step 1: Prerequisites

Ensure you have the following installed:

1. **Android Studio Giraffe (2022.3.1) or later**
   - Download from: https://developer.android.com/studio
   
2. **Java Development Kit (JDK) 17 or later**
   - Android Studio includes OpenJDK by default
   - Or download from: https://adoptium.net/

3. **Android SDK**
   - Minimum API Level: 24 (Android 7.0)
   - Target API Level: 34 (Android 14)
   - Install via Android Studio SDK Manager

### Step 2: Extract and Open Project

1. **Extract the ZIP file:**
   ```
   unzip LoanForeclosurePlannerIndia.zip -d LoanForeclosurePlanner
   cd LoanForeclosurePlanner
   ```

2. **Configure Android SDK path:**
   - Copy `local.properties.template` to `local.properties`
   - Edit `local.properties` and set your Android SDK path:
     ```properties
     sdk.dir=/path/to/your/Android/Sdk
     ```
   - On macOS: Usually `/Users/<username>/Library/Android/sdk`
   - On Linux: Usually `/home/<username>/Android/Sdk`
   - On Windows: Usually `C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk`

3. **Open in Android Studio:**
   - Launch Android Studio
   - Click "Open" (not "New Project")
   - Navigate to the extracted folder
   - Select the root folder and click "OK"

### Step 3: Gradle Sync

1. Android Studio will automatically start syncing Gradle
2. If prompted to install missing SDK components, click "Install"
3. Wait for sync to complete (check bottom status bar)
4. If sync fails:
   - Check your internet connection
   - Try: File > Invalidate Caches > Invalidate and Restart
   - Try: File > Sync Project with Gradle Files

### Step 4: Build the Project

#### Using Android Studio UI:

1. **Build > Make Project** (or Ctrl+F9 / Cmd+F9)
2. Wait for build to complete
3. Check the Build tab at the bottom for any errors

#### Using Command Line:

```bash
# On macOS/Linux:
./gradlew build

# On Windows:
gradlew.bat build
```

### Step 5: Run the App

#### Option A: Using an Emulator

1. **Create AVD (if not already created):**
   - Tools > Device Manager
   - Click "Create Device"
   - Select a device (e.g., Pixel 5)
   - Select system image (API 24+, recommend API 34)
   - Click "Finish"

2. **Run the app:**
   - Select the emulator from device dropdown
   - Click Run button (green triangle) or Shift+F10 / Ctrl+R
   - Wait for emulator to boot and app to install

#### Option B: Using a Physical Device

1. **Enable Developer Options on your Android device:**
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings > Developer Options
   - Enable "USB Debugging"

2. **Connect device:**
   - Connect via USB cable
   - Accept USB debugging prompt on device
   - Select device from dropdown in Android Studio
   - Click Run button

### Step 6: Testing the App

#### Test Free Tier:
1. Launch app (you'll see splash screen)
2. Add first loan:
   - Click + button
   - Fill in: Lender name, Principal (e.g., 100000), Interest rate (e.g., 10), Tenure (e.g., 12)
   - Click "Save Loan"
3. Add second loan (similar process)
4. Try to add third loan → Should show Paywall

#### Test Navigation:
1. Click on a loan to view details
2. Click "Prepayment" to simulate prepayments
3. From home, click "Compare Strategies" (if 2+ loans)
4. Click Settings icon to view settings

## Troubleshooting

### Build Errors

**Error: "SDK location not found"**
```
Solution: Create local.properties file with SDK path
```

**Error: "Gradle sync failed"**
```
Solution:
1. Check internet connection
2. File > Invalidate Caches > Restart
3. Delete .gradle folder and sync again
```

**Error: "Unsupported class file major version"**
```
Solution: Update to JDK 17 or later
File > Project Structure > SDK Location > JDK location
```

### Runtime Errors

**Error: "App keeps crashing on startup"**
```
Solution:
1. Clean and rebuild: Build > Clean Project, then Build > Rebuild Project
2. Check Logcat for error messages
3. Ensure target API 34 SDK is installed
```

**Error: "Billing not working"**
```
Solution: Billing requires:
1. Google Play Console setup (production only)
2. Test license configuration
3. Published to testing track
4. Cannot test billing in debug builds without proper setup
```

## Building Release APK

### For Testing (Unsigned):

```bash
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/release/app-release-unsigned.apk`

### For Production (Signed):

1. **Generate signing key:**
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
   ```

2. **Configure signing in `app/build.gradle.kts`:**
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("path/to/my-release-key.jks")
               storePassword = "your-store-password"
               keyAlias = "my-alias"
               keyPassword = "your-key-password"
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               // ... other configs
           }
       }
   }
   ```

3. **Build signed APK:**
   ```bash
   ./gradlew assembleRelease
   ```

### Generate Android App Bundle (AAB) for Play Store:

```bash
./gradlew bundleRelease
```

AAB location: `app/build/outputs/bundle/release/app-release.aab`

## Project Structure Verification

Verify the project structure is correct:

```
✓ settings.gradle.kts
✓ build.gradle.kts
✓ gradle.properties
✓ app/build.gradle.kts
✓ app/src/main/AndroidManifest.xml
✓ app/src/main/java/com/loanplanner/MainActivity.kt
✓ gradle/libs.versions.toml
```

## Gradle Tasks

Common Gradle tasks:

```bash
# List all tasks
./gradlew tasks

# Clean build artifacts
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug APK on connected device
./gradlew installDebug

# Run lint checks
./gradlew lint

# Generate dependency report
./gradlew app:dependencies
```

## IDE Configuration

### Recommended Android Studio Settings:

1. **Enable Gradle build cache:**
   - File > Settings > Build, Execution, Deployment > Gradle
   - Check "Enable Gradle build cache"

2. **Configure code style:**
   - File > Settings > Editor > Code Style > Kotlin
   - Set from: Kotlin style guide

3. **Enable Compose preview:**
   - Ensure Compose UI Check Mode is enabled in toolbar

## Next Steps

After successful build:

1. Review PROJECT_DOCUMENTATION.md for detailed feature information
2. Explore the codebase starting from MainActivity.kt
3. Test all screens and features
4. Customize UI and branding as needed
5. Set up Google Play Console for billing (if needed)
6. Add unit tests
7. Prepare for release

## Support Resources

- **Android Documentation**: https://developer.android.com
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Material 3**: https://m3.material.io
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Google Play Billing**: https://developer.android.com/google/play/billing

## Common Questions

**Q: Can I build without Android Studio?**
A: Yes, using Gradle wrapper from command line, but Android Studio is recommended.

**Q: Why can't I test billing?**
A: Billing requires Google Play Console setup and publishing to testing track.

**Q: Can I change the package name?**
A: Yes, use Android Studio's Refactor > Rename Package feature.

**Q: How do I add more features?**
A: Follow the MVVM pattern: Add entity → DAO → Repository → ViewModel → Screen

**Q: The app shows errors in IDE but builds fine?**
A: Try File > Invalidate Caches > Invalidate and Restart

---

**Need Help?**
Check the console output, Logcat, and Build tabs in Android Studio for detailed error messages.
