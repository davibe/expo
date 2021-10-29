import { ColorValue } from 'react-native';
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
export declare function setBackgroundColorAsync(color: ColorValue): Promise<void>;
/**
 * Gets the root view background color.
 *
 * @example
 * ```ts
 * const color = await SystemUI.getBackgroundColorAsync();
 * ```
 * @returns Current root view background color in hex format.
 */
export declare function getBackgroundColorAsync(): Promise<ColorValue>;
/**
 * Sets the app-wide user interface style.
 *
 * @example
 * ```ts
 * await SystemUI.setUserInterfaceStyleAsync('automatic');
 * ```
 */
export declare function setUserInterfaceStyleAsync(style: SystemUIUserInterfaceStyle): Promise<void>;
export * from './SystemUI.types';
