/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package features.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import features.WildcardListener;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class UsagePointSteps {

    private WebDriver driver = WebDriverSingleton.getInstance();

    @Given("^I am a Third Party$")
    public void I_am_a_Third_Party() throws Throwable {
    }

    @Given("^there exists a user that has Usage Points$")
    public void there_exists_a_user_that_has_Usage_Points() throws Throwable {
    }

    @When("^I request the usage points for a user$")
    public void I_request_the_usage_points_for_a_user() throws Throwable {
        driver.get("http://localhost:8080/RetailCustomer/1/UsagePoint");
    }

    @Then("^I should receive an xml response with the user's usage points$")
    public void I_should_receive_an_xml_response_with_the_user_s_usage_points() throws Throwable {
        XMLUnit.setIgnoreWhitespace(Boolean.TRUE);

        String xmlResult =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "<title>UsagePoint Feed</title>" +
                "<id>*</id>" +
                "<entry>" +
                "<title>House meter</title>" +
                "<link rel=\"self\" href=\"RetailCustomer/1/UsagePoint/1\" />" +
                "<link rel=\"up\" href=\"RetailCustomer/1/UsagePoint\" />" +
                "<id>1</id>" +
                "<updated>*</updated>" +
                "<published>*</published>" +
                "<content>" +
                "<UsagePoint/>" +
                "</content>" +
                "</entry>" +
                "<entry>" +
                "<title>Gas meter</title>" +
                "<link rel=\"self\" href=\"RetailCustomer/1/UsagePoint/2\" />" +
                "<link rel=\"up\" href=\"RetailCustomer/1/UsagePoint\" />" +
                "<id>2</id>" +
                "<updated>*</updated>" +
                "<published>*</published>" +
                "<content>" +
                "<UsagePoint/>" +
                "</content>" +
                "</entry>" +
                "</feed>";

        String pageSource = driver.getPageSource();
        String flatPageSource = pageSource.replace("\n", "").replaceAll("\\s+<", "<").replaceAll(">\\s+", ">");

        DetailedDiff diffXml = new DetailedDiff(new Diff(xmlResult, pageSource));
        diffXml.overrideDifferenceListener(new WildcardListener());

        assertTrue("\n\n\nXMLUnit ERROR:\n" + diffXml.toString() + ":\n\nEXPECTED:\n[" + xmlResult + "]\n\nGOT:\n[" + flatPageSource + "]\n\n\n", diffXml.similar());
    }
}
