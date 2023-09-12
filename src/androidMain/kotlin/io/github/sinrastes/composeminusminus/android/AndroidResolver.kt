package io.github.sinrastes.composeminusminus.android

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import io.github.sintrastes.composeminusminus.UI
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AndroidResolver(private val context: Context) : UI.Resolver<View> {
    override fun bindWidget(
        type: String,
        bindsInput: Class<*>,
        bindsOutput: Class<*>,
        bindInput: Flow<*>,
        bindOutput: (Any?) -> Unit,
        attributes: Map<String, String>
    ): View? = when (type) {
        "Button" -> if (bindOutput == Unit::class.java) {
            Button(context).apply {
                text = attributes["text"] ?: ""
                setOnClickListener {
                    bindOutput(Unit)
                }
            }
        } else {
            null
        }
        "Text" -> if (bindInput == String::class.java) {
            TextView(context).apply {
                text = attributes["initialText"]
                GlobalScope.launch {
                    bindInput.collect {
                        text = it as String
                    }
                }
            }
        } else {
            null
        }
        else -> null
    }

    override fun resolveLayout(
        type: String,
        attributes: Map<String, String>
    ): ((List<View>) -> View)? = when (type) {
        "Column" -> { children ->
            LinearLayout(context).apply {
                for (child in children) {
                    addView(child)
                }
            }
        }
        "Row" -> { children ->
            LinearLayout(context).apply {
                for (child in children) {
                    addView(child)
                }
            }
        }
        else -> null
    }
}