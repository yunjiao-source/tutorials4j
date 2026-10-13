package tutorials4j.toolkit.core.util;

import cn.hutool.core.codec.Base64;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
public class ResourceUtils {

  private static volatile ResourceUtils INSTANCE;

  private final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver;

  private ResourceUtils() {
    this.pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
  }

  private static class Holder {
    private static final ResourceUtils INSTANCE = new ResourceUtils();
  }

  public static ResourceUtils getInstance() {
    return ResourceUtils.Holder.INSTANCE;
  }

  public Resource getResource(String location) {
    return pathMatchingResourcePatternResolver.getResource(location);
  }

  public File getFile(String location) throws IOException {
    return getResource(location).getFile();
  }

  public InputStream getInputStream(String location) throws IOException {
    return getResource(location).getInputStream();
  }

  public String getFilename(String location) {
    return getResource(location).getFilename();
  }

  public URI getURI(String location) throws IOException {
    return getResource(location).getURI();
  }

  public URL getURL(String location) throws IOException {
    return getResource(location).getURL();
  }

  public long contentLength(String location) throws IOException {
    return getResource(location).contentLength();
  }

  public long lastModified(String location) throws IOException {
    return getResource(location).lastModified();
  }

  public boolean exists(String location) {
    return getResource(location).exists();
  }

  public boolean isFile(String location) {
    return getResource(location).isFile();
  }

  public boolean isReadable(String location) {
    return getResource(location).isReadable();
  }

  public boolean isOpen(String location) {
    return getResource(location).isOpen();
  }

  public Resource[] getResources(String locationPattern) throws IOException {
    return pathMatchingResourcePatternResolver.getResources(locationPattern);
  }

  public boolean isUrl(String location) {
    return org.springframework.util.ResourceUtils.isUrl(location);
  }

  public boolean isClasspathUrl(String location) {
    return Strings.CS.startsWith(location, ResourceLoader.CLASSPATH_URL_PREFIX);
  }

  public boolean isClasspathAllUrl(String location) {
    return Strings.CS.startsWith(location, ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX);
  }

  public boolean isJarUrl(URL url) {
    return org.springframework.util.ResourceUtils.isJarURL(url);
  }

  public boolean isFileUrl(URL url) {
    return org.springframework.util.ResourceUtils.isFileURL(url);
  }

  public byte[] toBytes(Resource resource) {
    try {
      InputStream inputStream = resource.getInputStream();
      return FileCopyUtils.copyToByteArray(inputStream);
    } catch (IOException e) {
      log.error("转换成字节数组失败", e);
      return null;
    }
  }

  public String toBase64(Resource resource) {
    byte[] bytes = toBytes(resource);
    return Base64.encode(bytes);
  }
}
