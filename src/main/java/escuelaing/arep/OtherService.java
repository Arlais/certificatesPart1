package escuelaing.arep;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static spark.Spark.*;

public class OtherService {
    public static void main (String[] args){
        secure(getCertificates(), "pulido", "keystores/myTrustStore", "pulido");
        port(getPort());
        get("/hello", (req, res) -> reader());
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 6000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
    static String getCertificates() {
        if (System.getenv("Certificates") != null) {
            return System.getenv("Certificates");
        }
        return "keystores/ecikeystore.p12"; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
    public static String reader() throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, KeyManagementException {
        System.out.println("hola");
        File trustStoreFile = new File("keystores/myTrustStore");
        char[] trustStorePassword = "pulido".toCharArray();
        // Load the trust store, the default type is "pkcs12", the alternative is "jks"
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);
        // Get the singleton instance of the TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        // Itit the TrustManagerFactory using the truststore objecttmf.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
        // We can now read this URL
        System.out.println("entre");
        //return readURL("https://localhost:5000/hello");
        // This one can't be read because the Java default truststore has been changed.
        return readURL("https://www.google.com");
    }

    public static String readURL(String url) throws MalformedURLException {
        URL _url = new URL(url);
        String outputLine="";
        try (BufferedReader reader= new BufferedReader(new InputStreamReader(_url.openStream()))){
            String input;
            while ((input=reader.readLine())!=null){
                System.out.println(input);
                outputLine+=input;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return outputLine+" segundo servidor";
        }
    }
}
