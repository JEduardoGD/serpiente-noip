package digital.serpiente.aws.noip.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;

public interface AmazonUtil {
    public static AmazonRoute53 createAmzRouteS3Client(String accessKeyId, String secretKeyId) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKeyId);
        return AmazonRoute53Client.builder().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(com.amazonaws.regions.Regions.US_EAST_2).build();
    }
}
