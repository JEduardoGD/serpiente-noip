package digital.serpiente.aws.noip.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import digital.serpiente.aws.noip.exception.NoIpException;

public interface NetworkUtil {
    public static String getIpOfDomain(String domain, String dnsIp) throws NoIpException {
        try {
            // Override system DNS setting with Google free DNS server
            System.setProperty("sun.net.spi.nameservice.nameservers", dnsIp);
            System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
            InetAddress address;
            address = InetAddress.getByName(domain);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            throw new NoIpException(e);
        }
    }
}
