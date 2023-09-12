package io.github.sintrastes.composeminusminus

import kotlinx.coroutines.flow.*

sealed class UI {
    abstract fun <Target> resolve(resolver: Resolver<Target>): Target?

    data class Widget<A, B>(
        val type: String,
        val attributes: Map<String, String>,
        val bindsInput: Class<A>,
        val bindsOutput: Class<B>,
        val bindInput: Flow<A>,
        val bindOutput: (B) -> Unit
    ) : UI() {
        override fun <Target> resolve(resolver: Resolver<Target>): Target? {
            return resolver.bindWidget(
                type,
                bindsInput,
                bindsOutput,
                bindInput,
                { arg -> bindOutput(arg as B) },
                attributes
            )
        }
    }

    data class Layout(
        val type: String,
        val attributes: Map<String, String>,
        val children: List<UI>
    ) : UI() {
        override fun <Target> resolve(resolver: Resolver<Target>): Target? {
            val layoutBuilder = resolver.resolveLayout(
                type,
                attributes
            )
                ?: return null

            val resolvedChildren = children.map { it.resolve(resolver) }
                .map { it ?: return null }

            return layoutBuilder(resolvedChildren)
        }
    }

    interface Builder {
        operator fun UI.unaryMinus()

        companion object {
            fun build(build: UI.Builder.() -> Unit): List<UI> {
                val children = mutableListOf<UI>()

                val builder = object: UI.Builder {
                    override fun UI.unaryMinus() {
                        children += this
                    }
                }

                builder.build()

                return children
            }
        }
    }

    interface Resolver<Target> {
        fun bindWidget(
            type: String,
            bindsInput: Class<*>,
            bindsOutput: Class<*>,
            bindInput: Flow<*>,
            bindOutput: (Any?) -> Unit,
            attributes: Map<String, String>
        ): Target?

        fun resolveLayout(
            type: String,
            attributes: Map<String, String>
        ): ((List<Target>) -> Target)?
    }
}

fun UI.Builder.Column(children: UI.Builder.() -> Unit) = UI.Layout(
    type = "Column",
    attributes = mapOf(),
    children = UI.Builder.build(
        children
    )
)

fun UI.Builder.Text(
    valueFlow: Dynamic<String>
): Flow<String> = run {
    val output = MutableSharedFlow<String>()

    -UI.Widget(
        type = "Text",
        attributes = mapOf(
            "textValue" to valueFlow.current()
        ),
        bindsInput = String::class.java,
        bindsOutput = String::class.java,
        bindInput = valueFlow.updated,
        bindOutput = { updatedString ->
            output.tryEmit(updatedString)
        }
    )

    return output
}

fun UI.Builder.Button(
    text: String,
): Flow<Unit> = run {
    val output = MutableSharedFlow<Unit>()

    -UI.Widget(
        type = "Button",
        attributes = mapOf(
            "text" to text
        ),
        bindsInput = Unit::class.java,
        bindsOutput = Unit::class.java,
        bindInput = flow { },
        bindOutput = { _ ->
            output.tryEmit(Unit)
        }
    )

    return output
}

fun UI.Builder.IntEntry(initialValue: Int): Dynamic<Int> = run {
    var currentValue = initialValue
    val outputCurrent = {
        currentValue
    }

    val outputUpdated = MutableSharedFlow<Int>()

    -UI.Widget(
        type = "TextEntry",
        attributes = mapOf(
            "text" to initialValue.toString(),
            "entryType" to "decimal"
        ),
        bindsInput = Int::class.java,
        bindsOutput = Int::class.java,
        bindInput = flow { },
        bindOutput = { updatedValue ->
            currentValue = updatedValue
            outputUpdated
                .tryEmit(currentValue)
        }
    )

    Dynamic(
        outputCurrent,
        outputUpdated
    )
}