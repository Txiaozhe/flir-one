package com.flir.flirone.networkhelp;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class WebServiceCall {
    public String NAMESPACE;
    public String WEBSERVICE_URL;
    public String METHOD_NAME;
    public SoapObject request;
    public int TimeOutMS = 2000;

    public WebServiceCall(String NAMESPACE, String WEBSERVICE_URL, String METHOD_NAME, int timeOut) {
        this.NAMESPACE = NAMESPACE;
        this.WEBSERVICE_URL = WEBSERVICE_URL;
        this.METHOD_NAME = METHOD_NAME;
        TimeOutMS = timeOut;
        this.request = new SoapObject(NAMESPACE, METHOD_NAME);

    }

    public Object callWebMethod() throws IOException, XmlPullParserException {
        Object result = null;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(

                SoapEnvelope.VER10);

        envelope.bodyOut = request;

        envelope.dotNet = true;

        // envelope.encodingStyle = SoapSerializationEnvelope.ENC2003;

        envelope.setOutputSoapObject(request);

        HttpTransportSE hts = new HttpTransportSE(WEBSERVICE_URL + "?op=" + METHOD_NAME, TimeOutMS);

        hts.debug = true;

        try {

            hts.call(null, envelope);
            // Object object = ( Object) envelope.getResponse();
            result = (Object) envelope.getResponse();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            throw e;
            //result = "IOException";
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            throw e;
            //result = "XmlPullParserException";
        }

        return result;

    }
}
