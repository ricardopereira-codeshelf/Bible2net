package de.reiss.bible2net.theword.widget

import android.content.Context
import android.support.annotation.WorkerThread
import de.reiss.bible2net.theword.R
import de.reiss.bible2net.theword.database.BibleItemDao
import de.reiss.bible2net.theword.database.TheWordItemDao
import de.reiss.bible2net.theword.database.converter.Converter
import de.reiss.bible2net.theword.formattedDate
import de.reiss.bible2net.theword.model.TheWord
import de.reiss.bible2net.theword.preferences.AppPreferences
import de.reiss.bible2net.theword.util.extensions.withZeroDayTime
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named

open class WidgetRefresher @Inject constructor(private val context: Context,
                                               @Named("widget") private val executor: Executor,
                                               private val theWordItemDao: TheWordItemDao,
                                               private val bibleItemDao: BibleItemDao,
                                               private val appPreferences: AppPreferences) {

    companion object {

        var currentWidgetText: String = ""

    }

    open fun execute() {
        refreshWidgetText { text ->
            currentWidgetText = text
            WidgetProvider.triggerWidgetRefresh()
        }
    }

    private fun refreshWidgetText(onRefreshed: (String) -> Unit) {
        executor.execute {

            val theWord = findTheWord(Date().withZeroDayTime())
            val text =
                    if (theWord == null) {
                        context.getString(R.string.no_content)
                    } else {
                        widgetText(
                                context = context,
                                theWord = theWord,
                                includeDate = appPreferences.widgetShowDate()
                        )
                    }

            onRefreshed(text)
        }
    }

    @WorkerThread
    private fun findTheWord(date: Date): TheWord? =
            appPreferences.chosenBible?.let { chosenBible ->
                bibleItemDao.find(chosenBible)?.let { bibleItem ->
                    theWordItemDao.byDate(bibleItem.id, date)?.let { theWordItem ->
                        return Converter.theWordItemToTheWord(bibleItem.bible, theWordItem)
                    }
                }
            }

    private fun widgetText(context: Context, theWord: TheWord, includeDate: Boolean): String =
            StringBuilder().apply {
                if (includeDate) {
                    append(formattedDate(context = context, time = theWord.date.time))
                    append("<br><br>")
                }

                if (theWord.content.intro1.isNotEmpty()) {
                    append(theWord.content.intro1)
                    append("<br><br>")
                }
                append(theWord.content.text1)
                append("<br>")
                append(theWord.content.ref1)
                append("<br><br>")

                if (theWord.content.intro2.isNotEmpty()) {
                    append(theWord.content.intro2)
                    append("<br><br>")
                }
                append(theWord.content.text2)
                append("<br>")
                append(theWord.content.ref2)
            }.toString()


}