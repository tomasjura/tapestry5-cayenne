package com.googlecode.tapestry5cayenne.integration;

import org.apache.tapestry5.test.AbstractIntegrationTestSuite;
import org.testng.annotations.Test;

@Test(sequential=true,groups="integration")
public class TapestryCayenneIntegrationTests extends AbstractIntegrationTestSuite {
    
    public TapestryCayenneIntegrationTests() {
        super("src/test/app0");
    }
    
    public void test_commit_after() throws InterruptedException {
        open("/commitaftertestpage");
        waitForPageToLoad();
        assertTextPresent("Dali");
        clickAndWait("link=Commit Ok");
        assertTextPresent("commitokname");
        clickAndWait("link=Runtime Exception");
        assertTextPresent("commitokname");
        clickAndWait("link=Checked Exception");
        assertTextPresent("savesokwithcheckedexceptionname");
    }
}
