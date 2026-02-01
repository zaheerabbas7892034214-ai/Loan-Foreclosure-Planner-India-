# Loan Foreclosure Planner (India)

A comprehensive Android application to help users manage and plan their loan foreclosures in India.

## Features

### Free Tier
- Add up to 2 loans
- Basic EMI preview functionality
- View loan details and EMI schedules
- Basic prepayment simulation

### Premium Tier (₹299 one-time purchase)
- Unlimited loans
- Advanced foreclosure simulation tools
- Export functionality (CSV and PDF)
- Share financial reports
- Strategy comparison (Snowball vs Avalanche method)

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database for local persistence
- **Billing**: Google Play Billing Library v6+
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/loanplanner/
│   │   ├── billing/           # Billing integration
│   │   ├── data/              # Data layer (Room)
│   │   │   ├── dao/           # Data Access Objects
│   │   │   ├── entities/      # Database entities
│   │   │   └── repository/    # Repository pattern
│   │   ├── domain/            # Business logic
│   │   │   ├── models/        # Domain models
│   │   │   └── usecases/      # Use cases
│   │   ├── ui/                # UI layer
│   │   │   ├── screens/       # Compose screens
│   │   │   ├── theme/         # Material 3 theme
│   │   │   └── navigation/    # Navigation
│   │   └── utils/             # Utility classes
│   ├── res/                   # Resources
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## Building the Project

1. Open the project in Android Studio Giraffe or later
2. Sync Gradle files
3. Build and run on an emulator or physical device

```bash
./gradlew build
```

## Key Features Implementation

### Loan Management
- Add/edit/delete loans with principal, interest rate, tenure, EMI
- View detailed EMI schedules
- Automatic EMI calculation

### Prepayment Simulation
- Simulate lump sum payments
- Simulate extra monthly payments
- Calculate interest savings and tenure reduction

### Strategy Comparison
- Compare Snowball method (smallest balance first)
- Compare Avalanche method (highest interest rate first)
- Visual comparison of payoff strategies

### Export & Share
- Export loan schedules to CSV
- Export financial plans to PDF format
- Share files with other apps

### Monetization
- Google Play In-App Purchase integration
- Offline entitlement persistence
- Restore purchases functionality
- Graceful free tier limitations

## Screens

1. **Splash Screen**: Initial loading with entitlement checks
2. **Home Dashboard**: Loan summary and list
3. **Add/Edit Loan**: Form to manage loan details
4. **Loan Detail**: Detailed view with EMI schedule
5. **Prepayment Simulation**: Calculate prepayment scenarios
6. **Strategy Comparison**: Compare payoff strategies
7. **Export**: Export and share loan data (Pro only)
8. **Paywall**: Premium feature showcase
9. **Settings**: App settings and restore purchases

## License

This project is created for demonstration purposes.

## Privacy Policy

[Privacy Policy Placeholder - Add your privacy policy URL]
