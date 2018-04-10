import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;

public class ViewSingleContestServletTest {
	
	String contestStringFormat;
	String headerString;
	
//	private final LocalServiceTestHelper helper =
//		      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	
	@Before
	public void setUp() throws Exception {
//		helper.setUp();
		contestStringFormat = "Favorite: %s\nDog: %s\nSpread: %.1f\nDate: %tc";
	}
	
	@Test
	public void test() {
		doTest();
	}
	
	@SuppressWarnings("deprecation")
	private void doTest() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    assertEquals(10, ds.prepare(new Query("Contest")).countEntities());
//	    ds.put(new Entity("yam"));
//	    ds.put(new Entity("yam"));
//	    assertEquals(2, ds.prepare(new Query("yam")).countEntities());
	}
	
	@After
	public void tearDown() throws Exception {
//		helper.tearDown();
	}

}
