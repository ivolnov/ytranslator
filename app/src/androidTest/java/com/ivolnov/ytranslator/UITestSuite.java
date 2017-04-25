package com.ivolnov.ytranslator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * UI related test suite.
 *
 * @version %I%, %G%
 * @author ivolnov
 * @since 29.03.17
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UIIntegrationTest.class,
        UIFunctionalTest.class
})
public class UITestSuite {}
