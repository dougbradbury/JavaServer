import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import static junit.framework.Assert.assertEquals;

public class HttpRequestTest {
    ByteArrayInputStream inputStream;
    HttpRequest request;

    @Before
    public void setup(){
        inputStream = new ByteArrayInputStream(("GET / HTTP/1.1").getBytes());
        request = new HttpRequest(inputStream);
    }

    @Test
    public void parseTheMethod(){
        assertEquals("GET", request.getMethod());
    }

    //request built in the setup isn't used in these next three tests.
    //Maybe extract a method to build requests instead?
    @Test
    public void parseThePath(){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(("GET /test_path HTTP/1.1").getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        assertEquals("/test_path", request.getPath());
    }

    @Test
    public void parseTheHttpVersion(){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(("GET /test_path HTTP/1.1").getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        assertEquals("HTTP/1.1", request.getHttpVersion());
    }

    @Test
    public void testGetParametersFromPath(){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(("GET /parameters?test=testtesttest").getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        assertEquals("test=testtesttest", request.getParametersFromPath());
    }

    @Test
    public void testGetVariableName(){
        assertEquals("variableName", request.getParameterVariableName("variableName=variableValue"));

    }

    @Test
    public void testGetVariableValue(){
        assertEquals("variableValue", request.getParameterVariableValue("variableName=variableValue"));
    }

    @Test
    public void breakUpParameters(){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(("GET /parameters?variable_1=test1&variable_2=test2").getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        assertEquals("variable_1=test1", request.getIndividualParams()[0]);
        assertEquals("variable_2=test2", request.getIndividualParams()[1]);
    }

    //I don't see any tests for character decoding
}
