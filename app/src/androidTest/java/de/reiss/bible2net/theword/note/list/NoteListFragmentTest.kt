package de.reiss.bible2net.theword.note.list

import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import de.reiss.bible2net.theword.R
import de.reiss.bible2net.theword.architecture.AsyncLoad
import de.reiss.bible2net.theword.model.Note
import de.reiss.bible2net.theword.testutil.*
import org.junit.Before
import org.junit.Test

class NoteListFragmentTest : FragmentTest<NoteListFragment>() {

    private val notesLiveData = MutableLiveData<AsyncLoad<List<Note>>>()

    private val mockedViewModel = mock<NoteListViewModel> {
        on { notesLiveData() } doReturn notesLiveData
    }

    override fun createFragment(): NoteListFragment =
            NoteListFragment.createInstance()
                    .apply {
                        viewModelProvider = mock {
                            on { get(any<Class<NoteListViewModel>>()) } doReturn mockedViewModel
                        }
                    }

    @Before
    fun setUp() {
        launchFragment()
    }

    @Test
    fun whenLoadingThenShowLoading() {
        notesLiveData.postValue(AsyncLoad.loading())

        assertDisplayed(R.id.note_list_loading)
        assertNotDisplayed(R.id.note_list_no_notes, R.id.note_list_recycler_view)
    }

    @Test
    fun whenLoadedNoNotesThenShowEmptyState() {
        notesLiveData.postValue(AsyncLoad.success(emptyList()))

        assertDisplayed(R.id.note_list_no_notes)
        assertNotDisplayed(R.id.note_list_loading, R.id.note_list_recycler_view)
    }

    @Test
    fun whenLoaded1NoteThenShowListWith1Item() {
        val notes = listOf(sampleNote(0))

        notesLiveData.postValue(AsyncLoad.success(notes))

        assertDisplayed(R.id.note_list_recycler_view)
        assertNotDisplayed(R.id.note_list_no_notes, R.id.note_list_loading)
        assertRecyclerViewItemsCount(R.id.note_list_recycler_view, 1)

        checkNoteIsDisplayedAt(note = notes.first(), index = 0)
    }

    @Test
    fun whenLoaded99NotesThenShowListWith99Items() {
        val notes = (1..99).map { sampleNote(it) }

        notesLiveData.postValue(AsyncLoad.success(notes))

        assertDisplayed(R.id.note_list_recycler_view)
        assertNotDisplayed(R.id.note_list_no_notes, R.id.note_list_loading)
        assertRecyclerViewItemsCount(R.id.note_list_recycler_view, 99)

        for ((index, note) in notes.withIndex()) {
            checkNoteIsDisplayedAt(note = note, index = index)
        }

    }

    private fun checkNoteIsDisplayedAt(note: Note, index: Int) {
        onRecyclerView(
                recyclerViewResId = R.id.note_list_recycler_view,
                itemPosition = index,
                viewInItem = R.id.note_list_item_text)
                .check(matches(withText(note.noteText)))
    }

}