# Jardy ColorPicker

A small, dependency-free color picker dialog for Android (Kotlin).

- Saturation / brightness square, hue bar and alpha bar
- Preview swatch and editable `#AARRGGBB` field, kept in sync with the sliders
- Standard Cancel / OK buttons (localized by Android)
- Ships with its own framework-based theme, so it works whatever theme your app uses
  (`Theme.AppCompat`, Material, or plain framework themes). It follows light/dark mode.
- Customizable background, title, hex field and button colors
- No dependency: no AppCompat, no Material

`minSdk 24` · built with `compileSdk 35`

## Installation

The library is published through [JitPack](https://jitpack.io).

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

// build.gradle.kts (module)
dependencies {
    implementation("com.github.NasH5169:jardy_color_picker:1.1.4")
}
```

## Usage

```kotlin
JardyColorPickerDialog.Builder(activity)
    .setTitle("Navigation line")
    .setInitialColor(0x8000FF00.toInt())
    .setOnColorSelected { argb ->
        // called only when the user taps OK
    }
    .show()
```

The selected color is an ARGB `Int`. To format it as `#AARRGGBB`:

```kotlin
val hex = String.format("#%08X", argb)
```

### Options

| Method | Description |
| --- | --- |
| `setTitle(CharSequence?)` / `setTitle(@StringRes Int)` | Dialog title |
| `setInitialColor(Int)` | Color shown when the dialog opens (ARGB) |
| `setAlphaEnabled(Boolean)` | `false` hides the alpha bar; the result is always opaque (default `true`) |
| `setOnColorSelected((Int) -> Unit)` | Called with the chosen color when the user taps OK |
| `setOnCancelled(() -> Unit)` | Called on Cancel, or when the dialog is dismissed otherwise |
| `setBackgroundColor(Int?)` | Dialog background color; the theme's rounded shape is kept |
| `setTextColor(Int?)` | Color of the title and the hex field |
| `setTitleColor(Int?)` | Title color only (overrides `setTextColor`) |
| `setHexTextColor(Int?)` | Hex field color only: text, hint and underline (overrides `setTextColor`) |
| `setButtonTextColor(Int?)` | Text color of the Cancel and OK buttons |
| `setTheme(Int)` | Replaces the default dialog theme (should derive from a framework Material dialog theme) |

Passing `null` to a color option restores the theme default.

`build()` returns the underlying `AlertDialog` without showing it; `show()` builds and shows it.

### Custom colors

The color setters take **resolved ARGB colors**, not resource ids. Resolve resources first:

```kotlin
JardyColorPickerDialog.Builder(context)
    .setTitle("Navigation line")
    .setBackgroundColor(context.getColor(R.color.background))
    .setTitleColor(context.getColor(R.color.title))
    .setHexTextColor(context.getColor(R.color.text))
    .setButtonTextColor(context.getColor(R.color.accent))
    .setOnColorSelected { /* ... */ }
    .show()
```

Passing `R.color.background` directly would be read as a color value and render a wrong, semi-transparent color.

## Notes

- The dialog is a plain `AlertDialog`: it is dismissed when the activity is recreated (for example on rotation), so reopen it if needed.
- If your activity handles orientation changes itself (`android:configChanges`), the open dialog switches between its portrait and landscape layouts on rotation, keeping the current color.
- There is no live callback while the user drags the sliders; the result is delivered on OK only.

## Sample app

The `sample` module is a small app (using a plain `Theme.AppCompat` theme) with two buttons: one for the default look and one with custom colors.

```
./gradlew :sample:installDebug
```
