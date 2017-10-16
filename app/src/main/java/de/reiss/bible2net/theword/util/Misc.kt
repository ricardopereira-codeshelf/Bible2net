package de.reiss.bible2net.theword.util

import android.content.Context
import android.text.Html
import de.reiss.bible2net.theword.formattedDate
import de.reiss.bible2net.theword.model.TheWordContent


@Suppress("DEPRECATION")
fun htmlize(text: String) =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(text)
        }

fun contentAsString(context: Context, time: Long, theWordContent: TheWordContent, note: String) =
        StringBuilder().apply {
            append(formattedDate(context, time))
            append("\n\n")

            if (theWordContent.intro1.isNotEmpty()) {
                append(theWordContent.intro1)
                append("\n\n")
            }
            append(theWordContent.text1)
            append("\n")
            append(theWordContent.ref1)
            append("\n\n")

            if (theWordContent.intro2.isNotEmpty()) {
                append(theWordContent.intro2)
                append("\n\n")
            }
            append(theWordContent.text2)
            append("\n")
            append(theWordContent.ref2)
            if (note.isNotEmpty()) {
                append("\n\n")
                append(note)
            }
        }.toString()