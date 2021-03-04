package digital.serpiente.aws.noip;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.GetHostedZoneRequest;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

import digital.serpiente.aws.noip.exception.NoIpException;
import digital.serpiente.aws.noip.service.NetworkService;
import digital.serpiente.aws.noip.util.AmazonUtil;
import digital.serpiente.aws.noip.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NoipComponent {
    @Autowired
    private NetworkService networkUtilService;

    @Value("${ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${SECRET_KEY_ID}")
    private String secretKeyId;

    @Value("${ROUTE53_HOSTED_ZONE_ID}")
    private String route53HostedZoneId;

    @Value("${DOMAIN_NAME}")
    private String domainName;

    @Value("${DNS_IP}")
    private String dnsIp;

    private AmazonRoute53 amzRoute53Client;

    @PostConstruct
    private void init() {
        // obtain amazon route 53 client
        amzRoute53Client = AmazonUtil.createAmzRouteS3Client(accessKeyId, secretKeyId);
    }

    @Scheduled(cron = "${cron.expression}", zone="America/Mexico_City")
    public void updateIp() {
        try {
            // steep 1: check you local IP right now
            String myIp = networkUtilService.getMyIp();
            log.info("My publlic IP right now is: {}", myIp);

            // steep 2: check intersted host IP according Google's dns
            String ipOnDns = NetworkUtil.getIpOfDomain(domainName, dnsIp);
            log.info("The IP address for domain \"{}\" in dns {} is {}", domainName, dnsIp, ipOnDns);

            if (!ipOnDns.equals(myIp)) {
                log.info("Changing the IP on domain...");
                // If local IP is not equals ipOnDns you need to update the DNS IP

                // getting hostedZone from amazon by Zone Id
                HostedZone hostedZone = amzRoute53Client.getHostedZone(new GetHostedZoneRequest(route53HostedZoneId))
                        .getHostedZone();

                // getting listResources of hostedZone
                ListResourceRecordSetsRequest listResourceRecordSetsRequest = new ListResourceRecordSetsRequest()
                        .withHostedZoneId(hostedZone.getId());
                // getting listResourcesRecordSet with listResources
                ListResourceRecordSetsResult listResourceRecordSetsResult = amzRoute53Client
                        .listResourceRecordSets(listResourceRecordSetsRequest);

                // getting record set
                List<ResourceRecordSet> resourceRecordSetList = listResourceRecordSetsResult.getResourceRecordSets();

                // A new list to be performed
                List<Change> changes = new ArrayList<>();
                for (ResourceRecordSet resourceRecordSet : resourceRecordSetList) {
                    if (resourceRecordSet.getType().equals("A")
                            && resourceRecordSet.getName().equals(domainName + ".")) {
                        // if record is type A and domainName are equals, list the change
                        List<ResourceRecord> resourceRecords = new ArrayList<>();
                        ResourceRecord resourceRecord = new ResourceRecord();
                        resourceRecord.setValue(myIp);
                        resourceRecords.add(resourceRecord);
                        resourceRecordSet.setResourceRecords(resourceRecords);
                        Change change = new Change(ChangeAction.UPSERT, resourceRecordSet);
                        changes.add(change);
                        log.info("Change stored for domain {}", domainName);
                    }
                }

                // fetch and perform changes
                if (!changes.isEmpty()) {
                    // perform all changes with the localIp
                    ChangeBatch changeBatch = new ChangeBatch(changes);
                    ChangeResourceRecordSetsRequest changeResourceRecordSetsRequest = new ChangeResourceRecordSetsRequest()
                            .withHostedZoneId(route53HostedZoneId).withChangeBatch(changeBatch);
                    amzRoute53Client.changeResourceRecordSets(changeResourceRecordSetsRequest);
                    log.info("Changed", domainName);
                }
            } else {
                log.info("The IP are same, no change required.");
            }

        } catch (NoIpException e) {
            log.error(e.getMessage());
        }

    }
}
