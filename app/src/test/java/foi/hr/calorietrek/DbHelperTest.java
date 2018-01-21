package foi.hr.calorietrek;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import foi.hr.calorietrek.database.DbHelper;
import static org.junit.Assert.assertFalse;

/**
 * Created by juras on 2018-01-21.
 * This unit test is used for testing database deletion.
 */

@RunWith(MockitoJUnitRunner.class)
public class DbHelperTest {

    @Mock
    Context mMockContext;

    @Test
    public void testDropDB()
    {
        assertFalse(mMockContext.deleteDatabase(DbHelper.DATABASE_NAME));
    }
}