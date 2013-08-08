package features;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Cucumber.Options(features = {"classpath:features/RetailCustomers.feature"})
public class RetailCustomersFeature {
}
