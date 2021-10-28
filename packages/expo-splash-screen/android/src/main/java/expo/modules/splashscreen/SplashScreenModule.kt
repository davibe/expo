package expo.modules.splashscreen

import android.content.Context

import expo.modules.core.ExportedModule
import expo.modules.core.ModuleRegistry
import expo.modules.core.Promise
import expo.modules.core.errors.CurrentActivityNotFoundException
import expo.modules.core.interfaces.ActivityProvider
import expo.modules.core.interfaces.ExpoMethod
import expo.modules.splashscreen.helpers.ReflectionExtensions

// Below import must be kept unversioned even in versioned code to provide a redirection from
// versioned code realm to unversioned code realm.
// Without this import any `SplashScreen.anyMethodName(...)` invocation on JS side ends up
// in versioned SplashScreen kotlin object that stores no information about the ExperienceActivity.
import expo.modules.splashscreen.singletons.SplashScreen

class SplashScreenModule(context: Context) : ExportedModule(context) {
  companion object {
    private const val NAME = "ExpoSplashScreen"
    private const val ERROR_TAG = "ERR_SPLASH_SCREEN"
  }

  private lateinit var activityProvider: ActivityProvider

  // TODO: Is this the safest approach?
  private val appearance by lazy {
    (context as ReactContext)
            .getNativeModule(AppearanceModule::class.java)!!
  }

  override fun getName(): String {
    return NAME
  }

  override fun onCreate(moduleRegistry: ModuleRegistry) {
    activityProvider = moduleRegistry.getModule(ActivityProvider::class.java)

    updateUserInterfaceStyle();
  }

  /**
   * Overwrite the Appearance API to lock it based on the 
   * static `expo_splash_screen_user_interface_style` value.
   */
  private fun updateUserInterfaceStyle() {
    val activity = activityProvider.currentActivity
    var style = context.getString(R.string.expo_splash_screen_user_interface_style).toLowerCase()

    // Default to "light" unless set otherwise.
    if (style == "") style = "light"

    // Update the UI mode in case it changed between reloads.
    SplashScreen.setUserInterfaceStyle(activity, style);

    if ((style == "dark" || style == "light")) {
      // TODO: How do we ensure this doesn't break Expo Go, or Dev Client manifest overrides.
      appearance.let { appearanceModule ->
        try {
          appearanceModule::class.java.setProtectedDeclaredField(
                  obj = appearanceModule,
                  filedName = "mOverrideColorScheme",
                  newValue = object : AppearanceModule.OverrideColorScheme {
                    override fun getScheme(): String {
                      return style
                    }
                  },
                  predicate = { currentValue -> currentValue == null }
          )

          appearanceModule::class.java.setProtectedDeclaredField(
                  obj = appearanceModule,
                  filedName = "mColorScheme",
                  newValue = style
          )
        } catch (e: Exception) {
          Log.e("SplashScreen", e)
        }
      }

      // Update Appearance listeners
      appearance.emitAppearanceChanged(style);
    }
  }

  @ExpoMethod
  fun preventAutoHideAsync(promise: Promise) {
    val activity = activityProvider.currentActivity
    if (activity == null) {
      promise.reject(CurrentActivityNotFoundException())
      return
    }
    SplashScreen.preventAutoHide(
      activity,
      { hasEffect -> promise.resolve(hasEffect) },
      { m -> promise.reject(ERROR_TAG, m) }
    )
  }

  @ExpoMethod
  fun hideAsync(promise: Promise) {
    val activity = activityProvider.currentActivity
    if (activity == null) {
      promise.reject(CurrentActivityNotFoundException())
      return
    }
    SplashScreen.hide(
      activity,
      { hasEffect -> promise.resolve(hasEffect) },
      { m -> promise.reject(ERROR_TAG, m) }
    )
  }
}
