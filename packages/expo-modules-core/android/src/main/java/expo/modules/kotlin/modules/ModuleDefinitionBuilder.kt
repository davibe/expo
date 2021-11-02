/**
 * We used a function from the experimental STD API - typeOf (see kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/type-of.html).
 * We shouldn't have any problem with that function, cause it's widely used in other libraries created by JetBrains like kotlinx-serializer.
 * This function is super handy if we want to receive a collection type.
 * For example, it's very hard to obtain the generic parameter type from the list class.
 * In plain Java, it's almost impossible. There is a trick to getting such information using something called TypeToken.
 * For instance, the Gson library uses this workaround. But there still will be a problem with nullability.
 * We didn't find a good solution to distinguish between List<Any?> and List<Any>.
 * Mainly because from the JVM perspective it's the same type.
 * That's why we used typeOf. It solves all problems described above.
 */
@file:OptIn(ExperimentalStdlibApi::class)

package expo.modules.kotlin.modules

import expo.modules.kotlin.Promise
import expo.modules.kotlin.methods.AnyMethod
import expo.modules.kotlin.methods.Method
import expo.modules.kotlin.methods.PromiseMethod
import expo.modules.kotlin.views.ViewManagerDefinition
import expo.modules.kotlin.views.ViewManagerDefinitionBuilder
import kotlin.reflect.typeOf

class ModuleDefinitionBuilder {
  private var name: String? = null
  private var constantsProvider = { emptyMap<String, Any?>() }
  @PublishedApi
  internal var methods = mutableMapOf<String, AnyMethod>()
  private var viewManagerDefinition: ViewManagerDefinition? = null

  fun build(): ModuleDefinition {
    return ModuleDefinition(
      requireNotNull(name),
      constantsProvider,
      methods,
      viewManagerDefinition
    )
  }

  fun name(name: String) {
    this.name = name
  }

  fun constants(constantsProvider: () -> Map<String, Any?>) {
    this.constantsProvider = constantsProvider
  }

  inline fun <reified R : Any?> method(
    name: String,
    crossinline body: () -> R
  ) {
    methods[name] = Method(name, arrayOf()) { body() }
  }

  @JvmName("methodWithPromise")
  inline fun method(
    name: String,
    crossinline body: (p0: Promise) -> Unit
  ) {
    methods[name] = PromiseMethod(name, arrayOf()) { _, promise -> body(promise) }
  }

  inline fun <reified P0, reified R : Any?> method(
    name: String,
    crossinline body: (p0: P0) -> R
  ): AnyMethod {
    val method = Method(name, arrayOf(typeOf<P0>())) { body(it[0] as P0) }
    methods[name] = method
    return method
  }

  @JvmName("methodWithPromise")
  inline fun <reified P0> method(
    name: String,
    crossinline body: (p0: P0, p1: Promise) -> Unit
  ) {
    methods[name] = PromiseMethod(name, arrayOf(typeOf<P0>())) { args, promise -> body(args[0] as P0, promise) }
  }

  inline fun <reified P0, reified P1, reified R : Any?> method(
    name: String,
    crossinline body: (p0: P0, p1: P1) -> R
  ) {
    methods[name] = Method(name, arrayOf(typeOf<P0>(), typeOf<P1>())) { body(it[0] as P0, it[1] as P1) }
  }

  @JvmName("methodWithPromise")
  inline fun <reified P0, reified P1> method(
    name: String,
    crossinline body: (p0: P0, p1: P1, p2: Promise) -> Unit
  ) {
    methods[name] = PromiseMethod(name, arrayOf(typeOf<P0>(), typeOf<P1>())) { args, promise -> body(args[0] as P0, args[1] as P1, promise) }
  }

  fun viewManager(body: ViewManagerDefinitionBuilder.() -> Unit) {
    require(viewManagerDefinition == null) { "The module definition may have exported only one view manager." }

    val viewManagerDefinitionBuilder = ViewManagerDefinitionBuilder()
    body.invoke(viewManagerDefinitionBuilder)
    viewManagerDefinition = viewManagerDefinitionBuilder.build()
  }
}
