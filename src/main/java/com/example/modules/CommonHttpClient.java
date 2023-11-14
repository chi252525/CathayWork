package com.example.modules;


import com.example.utils.URLUtils;
import com.example.exception.ExternalServiceException;
import com.example.exception.InternalServerException;
import net.minidev.json.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public final class CommonHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(CommonHttpClient.class);

    private HttpProperties config;
    private TrustStrategy trustStrategy;
    private ErrorResponseHandler errorResponseHandler;
    private HttpClientContext httpClientContext;

    public CommonHttpClient(HttpProperties config) {
        this.config = config;
        this.trustStrategy = (chain, authType) -> true;
        this.httpClientContext = getDefaultHttpClientContext();
    }

    public void setErrorResponseHandler(ErrorResponseHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
    }

    public void setHttpClientContext(HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

    private HttpClientContext getDefaultHttpClientContext() {
        String username = this.config.getUsername();
        String password = this.config.getPassword();

        HttpClientContext context = HttpClientContext.create();
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            context.setCredentialsProvider(credsProvider);
        }

        return context;
    }

    private CloseableHttpClient getDefaultHttpClient() {

        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, this.trustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new InternalServerException(e.getMessage());
        }

        Builder requestBuilder = RequestConfig.custom().setConnectTimeout(this.config.getConnectionTimeOut()).setConnectionRequestTimeout(this.config.getConnectionTimeOut());

        //@formatter:off
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultRequestConfig(requestBuilder.build());
        //@formatter:on

        if (this.config.getProxyGid() != null) {
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(this.config.getProxyGid());
            httpClientBuilder.setRoutePlanner(routePlanner);
        }

        if (Boolean.TRUE.equals(this.config.disableAutomaticRetries())) {
            httpClientBuilder.disableAutomaticRetries();
        }

        return httpClientBuilder.build();
    }

    public final String executeGetRequest(String requestPath, List<NameValuePair> queryNvps) throws ExternalServiceException {
        HttpGet httpGet = getHttpUriRequest(HttpGet.class, buildUrlQueryString(requestPath, queryNvps));
        return executeRequest(httpGet, httpClientContext);
    }

    public final String executePostRequest(String requestPath, Map<String, Object> body) throws ExternalServiceException {
        return executePostRequest(requestPath, body, null);
    }

    public final String executePostRequest(String requestPath, Map<String, Object> body, List<NameValuePair> queryNvps) throws ExternalServiceException {
        HttpPost httpPost = getHttpUriRequest(HttpPost.class, buildUrlQueryString(requestPath, queryNvps));
        String jsonString = new JSONObject(body).toString();
        return executePostRequest(httpPost, jsonString);
    }

    public final String executePostRequest(String requestPath, String jsonString) throws ExternalServiceException {
        String url = URLUtils.join(this.config.getEndpoint(), requestPath).toString();
        HttpPost httpPost = getHttpUriRequest(HttpPost.class, url);
        return executePostRequest(httpPost, jsonString);
    }

    private String executePostRequest(HttpPost httpPost, String jsonString) throws ExternalServiceException {
        if (!StringUtils.isEmpty(jsonString)) {
            StringEntity stringEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            logger.info("body: {}", jsonString);
        }
        return executeRequest(httpPost, httpClientContext);

    }

    public final String executePutRequest(String requestPath, byte[] body, List<NameValuePair> queryNvps) throws ExternalServiceException {
        HttpPut httpPut = getHttpUriRequest(HttpPut.class, buildUrlQueryString(requestPath, queryNvps));
        if (!ArrayUtils.isEmpty(body)) {
            ByteArrayEntity baEntity = new ByteArrayEntity(body, ContentType.APPLICATION_OCTET_STREAM);
            httpPut.setEntity(baEntity);
        }
        return executeRequest(httpPut, httpClientContext);
    }

    @SuppressWarnings("unchecked")
    private <T extends HttpUriRequest> T getHttpUriRequest(Class<T> clazz, String url) {

        HttpUriRequest request;
        if (clazz.isAssignableFrom(HttpGet.class)) {
            request = new HttpGet(url);
        } else if (clazz.isAssignableFrom(HttpPost.class)) {
            request = new HttpPost(url);
        } else if (clazz.isAssignableFrom(HttpPut.class)) {
            request = new HttpPut(url);
        } else {
            throw new InternalServerException("HttpUriRequest class [{}] is not supported.", clazz);
        }

        logger.info("request: {}; url: {}", request.getMethod(), url);

        if (MapUtils.isNotEmpty(this.config.getHeaderMap())) {
            this.config.getHeaderMap().forEach(request::addHeader);
        }
        // noinspection unchecked
        return (T) request;
    }

    public String buildUrlQueryString(String requestPath, List<NameValuePair> queryNvps) {
        String url = URLUtils.join(this.config.getEndpoint(), requestPath).toString();

        if (CollectionUtils.isNotEmpty(queryNvps)) {
            try {
                URIBuilder builder = new URIBuilder(url);
                if (!CollectionUtils.isEmpty(queryNvps)) {
                    builder.setParameters(queryNvps);
                }
                url = builder.build().toString();
            } catch (URISyntaxException e) {
                throw new InternalServerException(e);
            }
        }

        return url;
    }

    private String executeRequest(HttpUriRequest request, HttpClientContext authorizedContext) throws ExternalServiceException {

        // auto closeable
        try (CloseableHttpClient httpClient = getDefaultHttpClient()) {

            HttpResponse response = httpClient.execute(request, authorizedContext);
            String responseBody = transformHttpResponseToString(response);
            int statusCode = response.getStatusLine().getStatusCode();

            logger.info("response code: {}; body: {}", statusCode, responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else if (errorResponseHandler != null) {
                errorResponseHandler.handleErrorResponseBody(responseBody, statusCode);
            } // else do nothing

            ExternalServiceException ex = new ExternalServiceException(responseBody);
            ex.setErrorResponseBody(responseBody);
            throw ex;
        } catch (IOException e) {
            throw new ExternalServiceException(e);
        }

    }

    private String transformHttpResponseToString(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        byte[] bytes = IOUtils.toByteArray(entity.getContent());
        EntityUtils.consume(entity);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public <T> T transformXmlToObject(String xmlString, Class<T> objClass) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(objClass);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            @SuppressWarnings("unchecked")
            T obj = (T) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return obj;
        } catch (JAXBException e) {
            throw new InternalServerException(e);
        }
    }

    public HttpProperties getConfig() {
        return this.config;
    }

    public interface ErrorResponseHandler {
        public void handleErrorResponseBody(String errorResponseBody, int httpStatusCode) throws ExternalServiceException;
    }

}
