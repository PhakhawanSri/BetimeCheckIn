package biz.fatez.com.betimecheckin;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



/**
 * Created by Fatez on 3/21/2016.
 */
public class UserStatusWS {

    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://tempuri.org/";
    //Webservice URL - WSDL File location
    private static String URL = "http://intranet.betimes.biz/btcheckin/service.asmx";
    //private static String URL = "http://intranet.betimes.biz/btcheckin_demo/service.asmx";
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://tempuri.org/";

    public static String invokeUserStatusWS(String user_ad,String webMethName) {

       String loginStatus = "null";


        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo unamePI = new PropertyInfo();


        unamePI.setName("user_ad");
        unamePI.setValue(user_ad);
        unamePI.setType(String.class);
        request.addProperty(unamePI);


        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            SoapObject result=(SoapObject)envelope.bodyIn;

            // Get the response
            SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
            loginStatus = response.toString();

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            LoginActivity.errored = true;
            e.printStackTrace();
        }
        //Return boolean to calling object
        return loginStatus;
    }
}
