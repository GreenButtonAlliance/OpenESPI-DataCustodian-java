package features;

import cucumber.api.java.en.Given;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.PendingException;
import cucumber.runtime.junit.FeatureRunner;
//import org.junit.runner.RunWith;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(Cucumber.class)
@Cucumber.Options(features={"classpath:features/Homepage.feature",})
public class HomepageFeature {
}
