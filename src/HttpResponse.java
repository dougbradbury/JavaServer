import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by elizabethengelman on 3/12/14.
 */

//Single Responsibility Principle Violation:
//How many responsibilities does this class have? I count at least three
        // 1) Determining what kind of request is coming in
        // 2) Retriving content from file system (or generating it) Let's
        //    call that "fulfilling" the request
        // 3) Building the response
        // 4) Sending the respons down a stream
public class HttpResponse {
  //Field that are not part of the public interface should be private
    HttpRequest request;
    byte[] requestBody;
    byte[] requestImageBody; //dead code
    StringBuffer responseReturned = new StringBuffer(); //Returned is a funny name for a thing
    public HttpResponse(HttpRequest req){
        request = req;
    }

    //Open-Close Principle Violation:
    //How can this be refactored to be open to extension
    //(supporting new methods or response types) without
    //actually modifing this class?
    public String createResponse(){

        if (request.getPath().equals("/")){
            responseReturned.append(create200Response());
            requestBody = new byte[0];
        }else if(new File("../cob_spec/public" + request.getPath()).exists()){
            if (request.getMethod().equals("GET")){
                if (isAnImage()){
                    //so deeply nested here in if statements here.
                    //try to stay max 2 ifs deep in a single function
                        responseReturned.append("HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n");
                  //it looks like the Content-Type will be set to png even
                  //if the image is a jpg or a gif
                        setBodyContent();
                }else{
                      responseReturned.append("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
                      setBodyContent();
                }

            }else if (request.getMethod().equals("PUT")){
                responseReturned.append(create405Response());
            }else if (request.getMethod().equals("POST")){
                responseReturned.append(create405Response());
            }
        }else if(request.getPath().equals("/form")) {
            responseReturned.append(create200Response());
        }else if(request.getPath().equals("/method_options")){
          //try to keep a function working on the same level of abstraction
          //sometimes a method is called to create a response
          //and sometimes the response is created inline.
          //If it's constent, then it will be easier to read
            responseReturned.append("HTTP/1.1 200 OK\r\n Allow: GET,HEAD,POST,OPTIONS,PUT\r\n");
        }else if(request.getPath().equals("/redirect")){
            responseReturned.append("HTTP/1.1 307\r\nLocation: http://localhost:5000/");
            request.setPath("/");
        }
        else if(request.getPath().contains("/parameters")){
            responseReturned.append(create200Response());
            String[] params = request.getIndividualParams();
            String tempBody = new String();
            for (String param : params){
                tempBody += request.getParameterVariableName(param) + " = " + request.getParameterVariableValue(param) + "\n";
            }
            System.out.println("this is tempBody: " + tempBody);
            requestBody = tempBody.getBytes();
        }
        else{
            responseReturned.append("HTTP/1.1 404 Not Found\r\n\r\n");
            requestBody = ("File not found.").getBytes();
        }
        return responseReturned.toString();
        //This is a very long method. But the good news is that
        //I am done picking on it now :)
    }
//
//    public void sendResponse(String response, String body, PrintWriter outputToClient){
//        outputToClient.println(response);
//        outputToClient.println(body);
//    }

    //I don't see a test for this method
    public void sendNewResponse(OutputStream outputStream){
        try {
            byte[] requestHeader = responseReturned.toString().getBytes();
            DataOutputStream dOut = new DataOutputStream(outputStream);
            dOut.write(requestHeader);
            dOut.write(requestBody);
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

//    private String readFile() throws IOException {
//        BufferedReader fileBR = new BufferedReader(new FileReader("../cob_spec/public" + request.getPath()));
//        String currentLine;
//        StringBuffer fileContent = new StringBuffer();
//        while ((currentLine = fileBR.readLine()) != null) {
//            fileContent.append(currentLine + '\n');
//        }
//        return fileContent.toString();
//    }

    private byte[] readFile() throws IOException{
//        BufferedImage image = ImageIO.read(new File("../cob_spec/public" + request.getPath()));
        Path path = Paths.get("../cob_spec/public" + request.getPath());
        byte[] imageFileData = Files.readAllBytes(path);
        return imageFileData;
    }

    private String create200Response(){
        return "HTTP/1.1 200 OK\r\n\r\n";
    }

    private String create405Response(){
        return "HTTP/1.1 405 Method Not Allowed\r\n";
    }

    private void setBodyContent(){
        try{
            requestBody = readFile();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private Boolean isAGifFile(){
        Boolean outcome = false;
        if (request.getPath().contains("gif")){
            outcome = true;
        }
        return outcome;
    }

    private Boolean isAJpegFile(){
        Boolean outcome = false;
        if (request.getPath().contains("jpeg")){
            outcome = true;
        }if (request.getPath().contains("jpg")){
            outcome = true;
        }
        return outcome;
    }

    private Boolean isAPngFile(){
        Boolean outcome = false;
        if (request.getPath().contains("png")){
            outcome = true;
        }
        return outcome;
    }

    public Boolean isAnImage() {
        if (isAGifFile() || isAJpegFile() || isAPngFile()){
            return true;
        }else{
            return false;
            }
    }
//    public String getSizeOfImage() throws IOException {
//        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
//        ImageIO.write(requestImageBody, "png", tmp);
//        tmp.close();
//        Integer contentLength = tmp.size();
//
//        return contentLength.toString();
//    }
}
