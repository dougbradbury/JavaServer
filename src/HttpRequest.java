import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class HttpRequest {
    InputStream inputStream;
    BufferedReader inputFromClient;
    String requestString;

    public HttpRequest(InputStream is){
        inputStream = is;
        inputFromClient = new BufferedReader(new InputStreamReader(inputStream));
        try{
            requestString = inputFromClient.readLine();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    public String getMethod(){
        return requestString.split(" ")[0];
    }

    public String getPath(){
        return requestString.split(" ")[1];
    }

    public void setPath(String newPath){
        requestString = requestString.replace(getPath(), newPath);
    }

    public String getHttpVersion(){
        return requestString.split(" ")[2];
    }

    //could be private.  Not called outside of the test
    //test for getIndividualParams would cover this function too
    public String getParametersFromPath(){
        String parameters = getPath().substring(getPath().lastIndexOf("?") + 1);
        System.out.println("these are the parameters " + parameters);
        return parameters;
    }

    public String getParameterVariableName(String parameter){
        String variableName = parameter.substring(0, parameter.indexOf("="));
        return variableName;
    }

    public String getParameterVariableValue(String parameter){
        String variableValue = parameter.substring(parameter.indexOf("=") + 1);
        return decodeCharacters(variableValue);
    }

    //what is this returned a HashMap of names to values?
    //Then one wouldn't have to keep calling back in to extract
    //variable names and values
    public String[] getIndividualParams(){
        String params = getParametersFromPath();
        String[] paramPairs = params.split("&");
        return paramPairs;
    }

    //Is there a library function that can do this work?
    //There is also and algorithm that can get this done
    //what comes after the % is actually the ASCII character code
    //of the character wanted
    public String decodeCharacters(String value){
        String s1 = value.replace("%20", " ");
        String s2 = s1.replace("%3C", "<");
        String s3 = s2.replace("%2C", ",");
        String s4 = s3.replace("%3E", ">");
        String s5 = s4.replace("%3D", "=");
        String s6 = s5.replace("%3B", ";");
        String s7 = s6.replace("%2B", "+");
        String s8 = s7.replace("%40", "@");
        String s9 = s8.replace("%23", "#");
        String s10 = s9.replace("%24", "$");
        String s11 = s10.replace("%5B", "[");
        String s12 = s11.replace("%3A", ":");
        String s13 = s12.replace("%22", "\"");
        String s14 = s13.replace("%3F", "?");
        String s15 = s14.replace("%26", "&");
        String s16 = s15.replace("%5D", "]");
        return s16;
    }
}
