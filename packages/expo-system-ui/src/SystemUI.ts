import { UnavailabilityError } from 'expo-modules-core';
import { ColorValue, processColor } from 'react-native';

import ExpoSystemUI from './ExpoSystemUI';
import { SystemUIUserInterfaceStyle } from './SystemUI.types';

/**
 * Changes the root view background color.
 *
 * @example
 * ```ts
 * SystemUI.setBackgroundColorAsync("white");
 * ```
 * @param color Any valid [CSS 3 (SVG) color](http://www.w3.org/TR/css3-color/#svg-color).
 */
export async function setBackgroundColorAsync(color: ColorValue): Promise<void> {
  const colorNumber = processColor(color);
  return await ExpoSystemUI.setBackgroundColorAsync(colorNumber);
}

/**
 * Gets the root view background color.
 *
 * @example
 * ```ts
 * const color = await SystemUI.getBackgroundColorAsync();
 * ```
 * @returns Current root view background color in hex format.
 */
export async function getBackgroundColorAsync(): Promise<ColorValue> {
  return await ExpoSystemUI.getBackgroundColorAsync();
}

/**
 * Sets the app-wide user interface style.
 *
 * @example
 * ```ts
 * await SystemUI.setUserInterfaceStyleAsync('automatic');
 * ```
 */
export async function setUserInterfaceStyleAsync(style: SystemUIUserInterfaceStyle): Promise<void> {
  if (!ExpoSystemUI.setUserInterfaceStyleAsync) {
    throw new UnavailabilityError('SystemUI', 'setUserInterfaceStyleAsync');
  }
  return await ExpoSystemUI.setUserInterfaceStyleAsync(style);
}

export * from './SystemUI.types';
