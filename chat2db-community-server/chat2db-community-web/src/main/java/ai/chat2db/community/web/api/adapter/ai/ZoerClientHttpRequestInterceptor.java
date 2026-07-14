package ai.chat2db.community.web.api.adapter.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ZoerClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        ClientHttpResponse responseWrapper = new BufferingClientHttpResponseWrapper(response);
        long duration = System.currentTimeMillis() - startTime;
        logResponse(responseWrapper, duration);
        return responseWrapper;
    }


    private void logRequest(HttpRequest request, byte[] body) {
        try {

            String requestBody = new String(body, StandardCharsets.UTF_8);
            log.info("Request body: {}", requestBody);
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
    }


    private void logResponse(ClientHttpResponse response, long duration) {
        try {
            String responseBody = StreamUtils.copyToString(response.getBody(), getCharset(response.getHeaders()));
            log.info("Response body: {}", responseBody);
        } catch (IOException e) {
            log.error("Failed to log response", e);
        }
    }


    private Charset getCharset(HttpHeaders headers) {
        if (headers != null && headers.getContentType() != null && headers.getContentType().getCharset() != null) {
            return headers.getContentType().getCharset();
        }
        return StandardCharsets.UTF_8;
    }


    public static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;
        private byte[] body;

        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }


        @Override
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }


        @Override
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        @Override
        public void close() {
            this.response.close();
        }
    }
}
