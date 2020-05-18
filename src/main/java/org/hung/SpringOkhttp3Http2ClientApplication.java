package org.hung;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.tls.HandshakeCertificates;

@Slf4j
@SpringBootApplication
public class SpringOkhttp3Http2ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringOkhttp3Http2ClientApplication.class, args);
	}

	@Bean
	CommandLineRunner runRestClient(RestTemplateBuilder builder) {
		//https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/CustomTrust.java
		
		return (args) -> {
			RestTemplate client = builder
					.additionalCustomizers((restTemplate) -> {
						try {
						CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
						
						X509Certificate rootCA = (X509Certificate)certificateFactory.generateCertificate(new FileInputStream("C:\\Users\\kwong\\Documents\\certs\\selfsigned\\ca\\my-rootCA.crt"));
						
						// Hostname localhost not verified Exception => OkHostnameVerifer check Subject Alt Name
						X509Certificate serviceCA = (X509Certificate)certificateFactory.generateCertificate(new FileInputStream("C:\\Users\\kwong\\Documents\\certs\\selfsigned\\ca\\my-service.crt"));
						
						HandshakeCertificates certs = new HandshakeCertificates.Builder()
								//.addTrustedCertificate(rootCA)
								.addTrustedCertificate(serviceCA)
								.build();
						
						OkHttpClient httpClient = new OkHttpClient.Builder()
								.sslSocketFactory(certs.sslSocketFactory(), certs.trustManager())
								.hostnameVerifier(OkHostnameVerifier.INSTANCE)
								.build();
						
						ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory(httpClient);
						
						restTemplate.setRequestFactory(requestFactory);
						
						} catch (Exception e) {
							log.error("",e);
						}						
					})
					.build();
			
			ResponseEntity<String> resp = client.getForEntity("https://localhost:8080/echo", String.class);
		};
	}
}
