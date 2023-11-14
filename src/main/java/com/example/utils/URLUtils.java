package com.example.utils;

import com.example.exception.InternalServerException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class URLUtils {
    private static final Logger log = LoggerFactory.getLogger(URLUtils.class);

    /**
     * Join host and path strings with slashes. If a path is null, simply ignored.
     *
     * <ul>
     *     <li>"http://www.rakuten.com","path1", "path2" --> "http://www.rakuten.com/path1/path2"</li>
     *     <li>"http://www.rakuten.com","/path1", "/path2" --> "http://www.rakuten.com/path1/path2"</li>
     *     <li>"http://www.rakuten.com","path1", "/path2" --> "http://www.rakuten.com/path1/path2"</li>
     *     <li>"http://www.rakuten.com/","path1", "/path2" --> "http://www.rakuten.com/path1/path2"</li>
     *     <li>"http://www.rakuten.com/" --> "http://www.rakuten.com/"</li>
     *     <li>"http://www.rakuten.com" --> "http://www.rakuten.com"</li>
     *     <li>"http://www.rakuten.com/", null, "/path2" --> "http://www.rakuten.com/path2"</li>
     * </ul>
     *
     * @param host cannot be null, and must be uri parsable.
     * @param paths url paths
     * @return joined URI object.
     */
    public static URI join(String host, String... paths) {
        final String SLASH = "/";
        URI uri;
        Path path = null;
        try {
            uri = new URI(host);
            if (ArrayUtils.isNotEmpty(paths)) {
                for (String p : paths) {
                    if (StringUtils.isBlank(p)) {
                        continue;
                    }

                    String tempPath = p.trim();
                    while (tempPath.startsWith(SLASH)) {
                        tempPath = tempPath.substring(1);
                    }
                    path = (path == null) ? Paths.get(tempPath) : path.resolve(tempPath);

                }
                if (path != null) {
                    String pathStr = path.toString().replaceAll("\\\\", SLASH);
                    if (host.endsWith(SLASH) || host.endsWith("\\")) {
                        uri = uri.resolve(pathStr);
                    } else {
                        uri = new URI(host + SLASH).resolve(pathStr);
                    }
                }
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new InternalServerException(e);
        }
    }

    public static URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        URLConnection con = null;
        if (proxy == null) {
            con = url.openConnection();
            log.info("Open Connection: [ " + url.toString() + " ]");
        } else {
            con = url.openConnection(proxy);
            log.info("Open Connection: [ " + url.toString() + " ], with Proxy: [ " + proxy.toString() + " ]");
        }
        return con;
    }

    public static String getRequestPath(HttpServletRequest request){
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    public static String encode(String plainText) {
        try {
            return URLEncoder.encode(plainText, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return plainText;
        }
    }
}
