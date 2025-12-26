# AAC Speech

An Android application for Augmentative and Alternative Communication (AAC) that allows users to create a customizable grid of words and sentences that can be spoken using Text-to-Speech.

## Features

- **Grid View Widget**: Display words and sentences in a customizable grid layout
- **Homescreen Widget**: Add AAC Speech grid widgets to your homescreen with configurable column count
- **Add Items**: Add new words or sentences with custom background colors
- **Text-to-Speech**: Tap any grid item to hear it spoken aloud
- **Color Customization**: Choose from 8 different colors for each grid item
- **Delete Items**: Remove items you no longer need with a confirmation dialog
- **Data Persistence**: All items are saved automatically and persist between app sessions

## How to Use

1. **Adding Items**:
   - Tap the floating action button (+) at the bottom right
   - Enter your text (word or sentence)
   - Select a background color by tapping one of the color buttons
   - Tap "Save" to add the item to the grid

2. **Speaking Text**:
   - Tap any grid item to hear the text spoken using Text-to-Speech

3. **Deleting Items**:
   - Tap the delete icon (trash can) in the top-right corner of any grid item
   - Confirm the deletion in the dialog that appears

4. **Adding Homescreen Widget**:
   - Long-press on your Android homescreen
   - Select "Widgets" from the menu
   - Find "AAC Speech" widget and drag it to your homescreen
   - Configure the number of columns (1-5) for the widget grid
   - Tap "Save" to add the widget
   - The widget will display all items from the app and automatically update when items are added or removed

## Building the App

This is a standard Android application built with:
- Kotlin
- Android SDK 33
- Minimum SDK 21 (Android 5.0 Lollipop)

To build the app:
1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on an emulator or physical device

## Requirements

- Android Studio Arctic Fox or later
- Android SDK 33
- Gradle 8.1.0 or compatible version