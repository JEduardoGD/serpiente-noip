package digital.serpiente.aws.noip.service;

import digital.serpiente.aws.noip.exception.NoIpException;

public interface NetworkService {

    String getMyIp() throws NoIpException;

}
