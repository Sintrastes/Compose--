package io.github.sintrastes.composeminusminus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A saner version of Kotlin's [StateFlow], inspired by the Dynamic
 *  type of reflex.
 *
 * Some nice things about this API:
 *
 *   * It isn't a sub-class of [Flow], so we can [map] a [Dynamic] and get out a [Dynamic]
 *    again, rather than just a [Flow].
 *   * The same goes for [combine].
 *   * It can be mapped lazily without having to use [Flow.stateIn] or specify a coroutine context to do so.
 *   * [updated] is literally just for the updates, and won't emit automatically on first collect, so
 *    no having to `.drop(1)` if you don't want the current value on initial collect.
 **/
data class Dynamic<A>(
    val current: () -> A,
    val updated: Flow<A>
) {
    fun <B> map(f: (A) -> B): Dynamic<B> {
        return Dynamic(
            current = { f(current()) },
            updated = updated.map { f(it) }
        )
    }

    /**
     * Convert this [Dynamic] into a [StateFlow], for
     *  interoperability with other APIs.
     **/
    fun asStateFlow(scope: CoroutineScope): StateFlow<A> {
        val result = MutableStateFlow(current())

        scope.launch {
            updated.collect { update ->
                result.emit(update)
            }
        }

        return result
    }
}

val <A> A.const get(): Dynamic<A> {
    val constant = this

    return Dynamic(
        { constant },
        flow { }
    )
}

fun <E, A> Flow<E>.reduce(scope: CoroutineScope, initial: A, update: (E, A) -> A): Dynamic<A> {
    val events = this
    val updated = MutableSharedFlow<A>()

    var state = initial

    val current = { state }

    scope.launch {
        events.collect { event ->
            state = update(event, current())
        }
    }

    return Dynamic(
        current,
        updated
    )
}

fun <A, B, C> combine(xDyn: Dynamic<A>, yDyn: Dynamic<B>, transform: (A, B) -> C): Dynamic<C> {
    return Dynamic(
        current = {
            transform(
                xDyn.current(),
                yDyn.current()
            )
        },
        updated = combine(xDyn.updated, yDyn.updated, transform)
    )
}

fun <A, B, C, D> combine(xDyn: Dynamic<A>, yDyn: Dynamic<B>, zDyn: Dynamic<C>, transform: (A, B, C) -> D): Dynamic<D> {
    return Dynamic(
        current = {
            transform(
                xDyn.current(),
                yDyn.current(),
                zDyn.current()
            )
        },
        updated = combine(xDyn.updated, yDyn.updated, zDyn.updated, transform)
    )
}

fun <A, B, C, D, E> combine(
    dynamic1: Dynamic<A>,
    dynamic2: Dynamic<B>,
    dynamic3: Dynamic<C>,
    dynamic4: Dynamic<D>,
    transform: (A, B, C, D) -> E): Dynamic<E> {
    return Dynamic(
        current = {
            transform(
                dynamic1.current(),
                dynamic2.current(),
                dynamic3.current(),
                dynamic4.current()
            )
        },
        updated = combine(
            dynamic1.updated,
            dynamic2.updated,
            dynamic3.updated,
            dynamic4.updated,
            transform
        )
    )
}

fun <A, B, C, D, E, F> combine(
    dynamic1: Dynamic<A>,
    dynamic2: Dynamic<B>,
    dynamic3: Dynamic<C>,
    dynamic4: Dynamic<D>,
    dynamic5: Dynamic<E>,
    transform: (A, B, C, D, E) -> F): Dynamic<F> {
    return Dynamic(
        current = {
            transform(
                dynamic1.current(),
                dynamic2.current(),
                dynamic3.current(),
                dynamic4.current(),
                dynamic5.current()
            )
        },
        updated = combine(
            dynamic1.updated,
            dynamic2.updated,
            dynamic3.updated,
            dynamic4.updated,
            dynamic5.updated,
            transform
        )
    )
}