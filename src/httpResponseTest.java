import org.junit.Test;

import java.io.ByteArrayInputStream;

import static junit.framework.Assert.assertEquals;

/**
 * Created by elizabethengelman on 3/12/14.
 */
public class httpResponseTest {
    HttpRequest request;
    HttpResponse response;

    @Test
    public void okResponseIfIndex(){
        request = new HttpRequest(new ByteArrayInputStream(("GET / HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        assertEquals("HTTP/1.1 200 OK \r\n",response.createResponse());
    }

    @Test
    public void okResponseIfForm(){
        request = new HttpRequest(new ByteArrayInputStream(("GET /form HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        assertEquals("HTTP/1.1 200 OK \r\n",response.createResponse());
    }

    @Test
    public void correctResponseForOptionsMethod(){
        request = new HttpRequest(new ByteArrayInputStream(("GET /method_options HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        String testResponse = "HTTP/1.1 200 OK\n Allow: GET,HEAD,POST,OPTIONS,PUT";
        assertEquals(testResponse,response.createResponse());
    }

    @Test
    public void okResponseIfFileExists(){
        request = new HttpRequest(new ByteArrayInputStream(("GET /file1 HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        String testResponse = "HTTP/1.1 200 OK\r\n";
        assertEquals(testResponse, response.createResponse());
    }

    @Test
    public void noMethodIfPutsMethodWithFile(){
        request = new HttpRequest(new ByteArrayInputStream(("PUT /file1 HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        String testResponse = "HTTP/1.1 405 Method Not Allowed\r\n";
        assertEquals(testResponse, response.createResponse());
    }

    @Test
    public void notMethodIfPostMethodWithFile(){
        request = new HttpRequest(new ByteArrayInputStream(("POST /file1 HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        String testResponse = "HTTP/1.1 405 Method Not Allowed\r\n";
        assertEquals(testResponse, response.createResponse());
    }

    @Test
    public void FourOhFour(){
        request = new HttpRequest(new ByteArrayInputStream(("GET /dog HTTP/1.1").getBytes()));
        response = new HttpResponse(request);
        String testResponse = "HTTP/1.1 404 Not Found\r\n";
        assertEquals(testResponse, response.createResponse());
    }

}