import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ExampleTest {

    private static Logger log = Logger.getLogger(ExampleTest.class.getName());

    @Test
    public void basicTest(){
        Assertions.assertTrue(true);
    }
}
