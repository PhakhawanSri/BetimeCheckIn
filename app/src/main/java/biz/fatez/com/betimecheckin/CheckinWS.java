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
public class CheckinWS {

    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://tempuri.org/";
    //Webservice URL - WSDL File location
    private static String URL = "http://intranet.betimes.biz/btcheckin/service.asmx";
    //private static String URL = "http://intranet.betimes.biz/btcheckin_demo/service.asmx";
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://tempuri.org/";

    public static int invokeCheckinWS(String userName, String latitude, String longitude, String location, String webMethName) {
        int rpStatus = -1;
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo unamePI = new PropertyInfo();
        PropertyInfo latPI = new PropertyInfo();
        PropertyInfo lonPI = new PropertyInfo();
        PropertyInfo locPI = new PropertyInfo();

        unamePI.setName("username");
        unamePI.setValue(userName);
        unamePI.setType(String.class);
        request.addProperty(unamePI);

        latPI.setName("latitude");
        latPI.setValue(latitude);
        latPI.setType(String.class);
        request.addProperty(latPI);

        lonPI.setName("longitude");
        lonPI.setValue(longitude);
        lonPI.setType(String.class);
        request.addProperty(lonPI);

        locPI.setName("location");
        locPI.setValue(location);
        locPI.setType(String.class);
        request.addProperty(locPI);


        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to  boolean variable variable
            rpStatus = Integer.parseInt(response.toString());

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            //     LoginActivity.errored = true;
            e.printStackTrace();
        }
        //Return boolean to calling object
        return rpStatus;
    }

}
