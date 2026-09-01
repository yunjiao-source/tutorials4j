package tutorials4j.framework.feature.oss.fss;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record UploadParams(
    String platform,
    String objectId,
    String objectType,
    String saveFilename,
    Boolean isImageType) {}
