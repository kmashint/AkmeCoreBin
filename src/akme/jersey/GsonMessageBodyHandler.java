package akme.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import akme.core.io.StreamUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
 
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class GsonMessageBodyHandler extends AbstractMessageReaderWriterProvider<Object> implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
 
	private final Gson gson;

	/**
	 * Construct with the given Gson handler for JSON.
	 */
	public GsonMessageBodyHandler(final Gson gson) {
		this.gson = gson;
	}
	 
	public Gson getGson() {
		return gson;
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	    return JsonElement.class.isAssignableFrom(type)
		    	|| !(ClientResponse.class.isAssignableFrom(type) || String.class.isAssignableFrom(type) || Reader.class.isAssignableFrom(type) || InputStream.class.isAssignableFrom(type));
	}
	
	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, 
			  MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream) {
    	/*if (type.isAssignableFrom(InputStream.class)) {
    		return entityStream;
    	}*/
		InputStreamReader ins = new InputStreamReader(entityStream, getCharset(mediaType));
	    /*if (type.isAssignableFrom(Reader.class)) {
    		return new BufferedReader(ins);
    	}*/
	    try {
	    	final Type jsonType;
	    	if (type.equals(genericType)) {
	    		jsonType = type;
	    	} else {
	    		jsonType = genericType;
	    	}
	    	return gson.fromJson(ins, jsonType);
	    } finally {
	    	StreamUtil.closeQuiet(ins);
	    }
	}
	 
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	    return JsonElement.class.isAssignableFrom(type)
		    	|| !(ClientResponse.class.isAssignableFrom(type) || String.class.isAssignableFrom(type) || Reader.class.isAssignableFrom(type) || InputStream.class.isAssignableFrom(type));
	}
	 
	@Override
	public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}
 
	@Override
	public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
    	/*if (type.isAssignableFrom(InputStream.class)) {
    		InputStream ins = (InputStream) object;
    		byte[] buf = new byte[1024];
    		try {
    			for (int n; (n=ins.read(buf, 0, buf.length)) != -1; ) entityStream.write(buf, 0, n);
    		}
    		finally {
    			StreamUtil.closeQuiet(ins);
    		}
    	}*/
		OutputStreamWriter ous = new OutputStreamWriter(entityStream, getCharset(mediaType));
    	/*if (type.isAssignableFrom(Reader.class)) {
    		Reader ins = (Reader) object;
    		char[] buf = new char[1024];
    		try {
    			for (int n; (n=ins.read(buf, 0, buf.length)) != -1; ) ous.write(buf, 0, n);
    		}
    		finally {
    			StreamUtil.closeQuiet(ins);
    		}
    	}*/
		try {
			final Type jsonType;
			if (type.equals(genericType)) {
				jsonType = type;
			} else {
				jsonType = genericType;
			}
			gson.toJson(object, jsonType, ous);
		} finally {
			StreamUtil.closeQuiet(ous);
		}
	}
}

