# Loan Foreclosure Planner (India) - Project Documentation

## Project Overview

This is a complete, production-ready Android application built with Kotlin and Jetpack Compose that helps users in India manage and plan their loan foreclosures.

## Build Instructions

### Prerequisites
- Android Studio Giraffe (2022.3.1) or later
- JDK 17 or later
- Android SDK with API Level 34
- Gradle 8.2 or later

### Opening the Project

1. **Extract the ZIP file** to your desired location
2. **Open Android Studio**
3. Click **File > Open**
4. Navigate to the extracted project folder
5. Click **OK**

### First Build

1. Android Studio will automatically sync Gradle files
2. If prompted, install any missing SDK components
3. Wait for Gradle sync to complete
4. Build the project: **Build > Make Project** (or press Ctrl+F9 / Cmd+F9)

### Running the App

1. **Using an Emulator:**
   - Create an Android Virtual Device (AVD) with API Level 24 or higher
   - Click the **Run** button or press Shift+F10 / Ctrl+R
   
2. **Using a Physical Device:**
   - Enable Developer Options and USB Debugging on your device
   - Connect via USB
   - Select your device from the device dropdown
   - Click **Run**

## Project Structure

```
Loan-Foreclosure-Planner-India-/
├── app/
│   ├── build.gradle.kts              # App-level build configuration
│   ├── proguard-rules.pro            # ProGuard rules for release builds
│   └── src/main/
│       ├── AndroidManifest.xml       # App manifest
│       ├── java/com/loanplanner/
│       │   ├── MainActivity.kt       # Main activity with navigation
│       │   ├── LoanPlannerApplication.kt  # Application class
│       │   ├── billing/              # Google Play Billing integration
│       │   │   └── BillingManager.kt
│       │   ├── data/                 # Data layer (Room Database)
│       │   │   ├── AppDatabase.kt
│       │   │   ├── dao/
│       │   │   │   ├── LoanDao.kt
│       │   │   │   └── PurchaseDao.kt
│       │   │   ├── entities/
│       │   │   │   ├── LoanEntity.kt
│       │   │   │   └── PurchaseEntity.kt
│       │   │   └── repository/
│       │   │       ├── LoanRepository.kt
│       │   │       └── PurchaseRepository.kt
│       │   ├── domain/               # Business logic layer
│       │   │   ├── models/
│       │   │   │   └── Models.kt
│       │   │   └── usecases/
│       │   │       ├── LoanCalculator.kt
│       │   │       └── StrategyComparator.kt
│       │   ├── ui/                   # UI layer (Jetpack Compose)
│       │   │   ├── navigation/
│       │   │   │   └── Screen.kt
│       │   │   ├── screens/
│       │   │   │   ├── splash/       # Splash screen
│       │   │   │   ├── home/         # Home dashboard
│       │   │   │   ├── loan/         # Add/Edit/Detail loan screens
│       │   │   │   ├── prepayment/   # Prepayment simulation
│       │   │   │   ├── strategy/     # Strategy comparison
│       │   │   │   ├── export/       # Export functionality
│       │   │   │   ├── paywall/      # Premium unlock screen
│       │   │   │   └── settings/     # Settings screen
│       │   │   └── theme/            # Material 3 theme
│       │   │       ├── Color.kt
│       │   │       ├── Theme.kt
│       │   │       └── Type.kt
│       │   └── utils/
│       │       └── ExportUtils.kt    # CSV/PDF export utilities
│       └── res/                      # Resources
│           ├── values/
│           │   ├── colors.xml
│           │   ├── strings.xml
│           │   └── themes.xml
│           ├── xml/
│           │   └── file_paths.xml
│           └── mipmap-*/             # Launcher icons
├── gradle/
│   ├── libs.versions.toml            # Version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts                  # Root build configuration
├── settings.gradle.kts               # Gradle settings
├── gradle.properties                 # Gradle properties
├── gradlew                           # Gradle wrapper script (Unix)
├── .gitignore                        # Git ignore rules
└── README.md                         # Project README
```

## Features Implemented

### 1. **Splash Screen**
- Displays app branding
- Performs entitlement checks
- Smooth transition to home screen

### 2. **Home Dashboard**
- Summary of all loans (total principal, interest)
- List of current loans
- Add loan button with tier restrictions
- Navigation to settings and strategy comparison

### 3. **Add/Edit Loan**
- Input fields: Lender name, Principal, Interest rate, Tenure
- Automatic EMI calculation
- Form validation
- Save to local database

### 4. **Loan Detail Screen**
- Complete loan information
- Full EMI schedule table with:
  - Month number
  - Principal paid
  - Interest paid
  - Remaining balance
- Navigation to prepayment simulation
- Export option (Pro tier only)

### 5. **Prepayment Simulation**
- Input lump sum payment
- Input extra monthly payment
- Calculate:
  - Interest saved
  - Tenure reduced
  - New EMI schedule

### 6. **Strategy Comparison**
- Compare Snowball method (smallest balance first)
- Compare Avalanche method (highest interest rate first)
- Show:
  - Total interest saved
  - Time saved
  - Payoff order
  - Recommendation

### 7. **Export Functionality** (Pro Tier)
- Export to CSV format
- Export to PDF format
- Share files with other apps
- Uses Android FileProvider for secure sharing

### 8. **Paywall Screen**
- Showcase premium features
- One-time purchase button (₹299)
- Restore purchases functionality
- Error handling for billing issues

### 9. **Settings Screen**
- Display entitlement status (Free/Pro)
- Restore purchases option
- Clear all data option
- Privacy policy link placeholder

## Technical Implementation

### Architecture: MVVM (Model-View-ViewModel)

- **Model**: Room Database entities and repositories
- **View**: Jetpack Compose UI screens
- **ViewModel**: Lifecycle-aware components managing UI state

### Key Technologies

1. **Jetpack Compose**: Modern declarative UI framework
2. **Material 3**: Latest Material Design system
3. **Room Database**: Local data persistence
4. **Google Play Billing v6+**: In-app purchases
5. **Kotlin Coroutines**: Asynchronous programming
6. **Navigation Compose**: Type-safe navigation
7. **StateFlow**: Reactive state management

### Tier System

#### Free Tier
- Maximum 2 loans
- Basic EMI calculation and viewing
- Basic prepayment simulation
- No export functionality

#### Pro Tier (₹299)
- Unlimited loans
- All prepayment features
- CSV and PDF export
- Share functionality
- Strategy comparison

### Billing Integration

The app uses Google Play Billing Library 6.1.0 with:
- Product ID: `planner_pro_unlock`
- Type: INAPP (one-time purchase)
- Price: ₹299
- Features:
  - Purchase acknowledgement
  - Offline entitlement persistence
  - Restore purchases
  - Graceful error handling

### Data Persistence

Uses Room Database with two tables:
1. **loans**: Stores loan information
2. **purchases**: Stores purchase entitlements for offline access

## Testing the App

### Free Tier Flow
1. Launch app
2. Add first loan - should succeed
3. Add second loan - should succeed
4. Try to add third loan - should show paywall

### Pro Tier Flow
1. Navigate to Paywall screen
2. Click "Unlock Now"
3. Complete mock purchase (in test environment)
4. Verify unlimited loan creation
5. Test export functionality
6. Test strategy comparison

### Testing Billing
To test billing in development:
1. Use a test account in Google Play Console
2. Add test license for your test account
3. Publish to Internal Testing track
4. Install from Play Store
5. Test purchase flow

## Build Variants

The project supports standard Android build types:
- **debug**: Development build with debugging enabled
- **release**: Production build with ProGuard/R8 optimization

## Gradle Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device
./gradlew installDebug

# Generate release bundle (AAB)
./gradlew bundleRelease
```

## Known Limitations

1. **Launcher Icons**: Placeholder icons are used. Production app should have custom designed icons.
2. **Privacy Policy**: Link is placeholder. Real app needs actual privacy policy URL.
3. **PDF Export**: Currently exports as formatted text. For true PDF, consider using libraries like iText or Android PdfDocument.
4. **Charts**: Basic chart data structures are in place. For visual charts, integrate a library like MPAndroidChart or Vico.
5. **Billing Testing**: Requires Google Play Console setup and test accounts for full testing.

## Production Checklist

Before releasing to production:

- [ ] Design and add custom launcher icons
- [ ] Create and link actual Privacy Policy
- [ ] Set up Google Play Console
- [ ] Configure signing keys
- [ ] Add real product ID in Play Console
- [ ] Implement advanced PDF generation
- [ ] Add chart visualization library
- [ ] Complete end-to-end billing testing
- [ ] Add analytics (Firebase, etc.)
- [ ] Implement crash reporting
- [ ] Add unit and integration tests
- [ ] Perform security audit
- [ ] Optimize performance
- [ ] Test on multiple devices and screen sizes
- [ ] Add proper error logging

## Dependencies

All dependencies are defined in `gradle/libs.versions.toml`:

- Core: androidx.core:core-ktx:1.12.0
- Compose: BOM 2024.01.00
- Material 3: 1.2.0
- Navigation: 2.7.6
- Room: 2.6.1
- Billing: 6.1.0
- Coroutines: 1.7.3

## License

This project is for demonstration purposes.

## Support

For issues or questions:
1. Check the documentation
2. Review the code comments
3. Check Android Developer documentation
4. Review Material 3 guidelines

## Contributing

This is a complete template project. Feel free to:
- Customize the UI
- Add additional features
- Improve algorithms
- Enhance user experience
- Add testing
