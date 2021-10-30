package com.home.small.sso.client.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GTsung
 * @date 2021/10/30
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    public static String get(String url, Map<String, String> paramMap) {
        String result = null;
        CloseableHttpResponse response = null;
        String realUrl = url;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            if (paramMap != null && !paramMap.isEmpty()) {
                List<NameValuePair> params = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
                realUrl += "?" + paramStr;
            }
            HttpGet httpGet = new HttpGet(realUrl);
            response = httpClient.execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }
                log.info("url: {} , result: {}", url, result);
            }
        } catch (Exception e) {
            log.info("url: {} , result: {} ", url, result, e);
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.info("关闭HttpClient链接发生异常", e);
            }
        }

        return result;
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String post(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        HttpPost httpPost = null;
        CloseableHttpClient httpClient = null;
        String result = null;

        try {
            httpPost = new HttpPost(url);
            if (paramMap != null && !paramMap.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
            httpPost.setConfig(requestConfig);
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> headerItem : headerMap.entrySet()) {
                    httpPost.setHeader(headerItem.getKey(), headerItem.getValue());
                }
            }
            httpClient = HttpClients.custom().disableAutomaticRetries().build();

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
                log.info("url: {}, result: {}", url, result);
            }

        } catch (Exception e) {
            log.info("url: {}, paramMap: {} ", url, paramMap, e);
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.info("httpClient关闭链接异常", e);
                }
            }
        }

        return result;
    }

    public static String post(String url, Map<String, String> paramMap) {
        return post(url, paramMap, null);
    }

    public static String postHeader(String url, Map<String, String> headerMap) {
        return post(url, null, headerMap);
    }

}
