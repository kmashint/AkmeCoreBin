package akme.core.test;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import akme.jersey.GsonMessageBodyHandler;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * Test the simplicity of Jersey/JAX-RS for REST/HTTP.
 */
public class JerseyTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(JerseyTest.class);
	}
	
	protected Client webClient;
	
	public JerseyTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		final ClientConfig cc = new DefaultClientConfig();
		Map<String,Object> ccp = cc.getProperties();
		ccp.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 15000);
		ccp.put(ClientConfig.PROPERTY_READ_TIMEOUT, 15000);
		Set<Object> ccs = cc.getSingletons();
		ccs.add(new GsonMessageBodyHandler(new Gson()));
		webClient = Client.create(cc);
		super.setUp();
	}

	public void tearDown() throws Exception {
		webClient.destroy();
		super.tearDown();
	}
	
	public void test1yahoo() {
		setName("test1yahoo");
		WebResource wr = webClient.resource("http://ca.yahoo.com/");
		wr.accept(MediaType.TEXT_HTML);
		wr.header("If-Modified-Since", new Date(0));
		String result = wr.get(String.class);
		System.out.println(result);
		assertNotNull(result);
	}
	
	public void test2multipart() {
		setName("test2multipart");
		FormDataMultiPart mp = new FormDataMultiPart();
		mp.bodyPart(new FileDataBodyPart("file", new File("C:/gradual_decrease_of_quality.png"), MediaType.valueOf("image/png")));
		WebResource wr = webClient.resource("http://ca.yahoo.com/");
		ClientResponse res = wr.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, mp);
		System.out.println("Response: " + res.getStatus() +" "+ res.getStatusInfo());
		assertEquals(200, res.getStatus());
	}
	
}
