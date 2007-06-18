package org.mifos.framework.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AddActivityTest {

	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		short newId = 17032;
		AddActivity upgrade = new AddActivity(
			DatabaseVersionPersistence.APPLICATION_VERSION + 1,
			newId,
			SecurityConstants.LOAN_MANAGEMENT,
			TEST_LOCALE,
			"Can use the executive washroom");
		upgrade.upgrade(database.openConnection());
		ActivityEntity fetched = (ActivityEntity) 
			database.openSession().get(ActivityEntity.class, newId);
		fetched.setLocaleId(TEST_LOCALE);
		assertEquals("Can use the executive washroom",
			fetched.getActivityName());
		assertEquals(SecurityConstants.LOAN_MANAGEMENT,
			fetched.getParent().getId());
		
		ActivityContext activityContext = 
			new ActivityContext(newId, TestObjectFactory.HEAD_OFFICE);
		AuthorizationManager authorizer = AuthorizationManager.getInstance();
		authorizer.init(database.openSession());

		UserContext admin = TestUtils.makeUser(
			RolesAndPermissionConstants.ADMIN_ROLE);
		assertTrue(
			authorizer
				.isActivityAllowed(admin, activityContext));

		UserContext nonAdmin = TestUtils.makeUser(TestUtils.DUMMY_ROLE);
		assertFalse(
			authorizer
				.isActivityAllowed(nonAdmin, activityContext));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddActivityTest.class);
	}

}
