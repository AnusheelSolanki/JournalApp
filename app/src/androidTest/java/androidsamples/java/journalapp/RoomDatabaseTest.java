package androidsamples.java.journalapp;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Ordering;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.jar.JarOutputStream;

@RunWith(JUnit4.class)
public class RoomDatabaseTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private JournalEntryDao dao;
    private JournalRoomDatabase db;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();

        db = Room.inMemoryDatabaseBuilder(context, JournalRoomDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.journalEntryDao();
    }

    @After
    public void closeDB() {
        db.close();
    }

    @Test
    public void getAllEntries() throws InterruptedException {
        // also checks if ascending order by title is maintained
        JournalEntry entry1 = new JournalEntry("B","FRI, JAN 12, 2001","12:30","14:00");
        dao.insert(entry1);
        JournalEntry entry2 = new JournalEntry("A","TUE, DEC 12, 2006","18:15","19:00");
        dao.insert(entry2);
        List<JournalEntry> entries = TestUtil.getValue(dao.getAllEntries());
        assertEquals(entries.get(0).getUid(),entry2.getUid());
        assertEquals(entries.get(1).getUid(),entry1.getUid());
    }

    @Test
    public void insertSingleEntry() throws InterruptedException {
        // insert a single entry and check if it is present in the database
        JournalEntry entry = new JournalEntry("Test","FRI, NOV 17, 2023","14:12","14:30");
        dao.insert(entry);
        List<JournalEntry> entries = TestUtil.getValue(dao.getAllEntries());
        assertEquals(entries.get(0).getUid(),entry.getUid());
    }

    @Test
    public void updateSingleEntry() throws InterruptedException {
        // update an entry's endTime and check for it
        JournalEntry entry = new JournalEntry("Update","THU, NOV 9, 2023","13:00","23:30");
        dao.insert(entry);
        entry.setEndTime("23:45");
        dao.update(entry);
        JournalEntry query = TestUtil.getValue(dao.getEntry(entry.getUid()));
        assertEquals(query.getEndTime(),"23:45");
    }

    @Test
    public void deleteSingleEntry() throws InterruptedException {
        // delete a single entry and check if the DB has been updated
        JournalEntry entry1 = new JournalEntry("Delete","FRI, JAN 12, 2001","12:30","14:00");
        JournalEntry entry2 = new JournalEntry("Don't Delete","TUE, DEC 12, 2006","18:15","19:00");
        dao.insert(entry1);
        dao.insert(entry2);
        List<JournalEntry> entries = TestUtil.getValue(dao.getAllEntries());
        assertEquals(entries.size(),2);
        dao.delete(entry1);
        entries = TestUtil.getValue(dao.getAllEntries());
        assertEquals(entries.size(),1);
        assertEquals(entries.get(0).getUid(),entry2.getUid());
    }

}
