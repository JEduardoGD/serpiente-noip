package digital.serpiente.aws.noip.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;

import digital.serpiente.aws.noip.exception.NoIpException;
import digital.serpiente.aws.noip.service.NetworkService;

@Service
public class NetworkServiceImpl implements NetworkService {
    @Override
    public String getMyIp() throws NoIpException {
        URL whatismyip;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            throw new NoIpException(e);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(whatismyip.openStream()))) {
            return br.readLine();
        } catch (IOException e) {
            throw new NoIpException(e);
        }
    }
}
