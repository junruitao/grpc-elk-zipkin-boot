package voyager.currencyconversion.api.client;

import voyager.currencyconversion.grpc.CurrencyConversionServiceGrpc;
import voyager.currencyconversion.grpc.CurrencyConversionServiceGrpc.CurrencyConversionServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : github.com/junruitao
 * @project : currency-conversion - elk, zipkin, gRpc with Spring and
 *          Spring Boot 
 **/
@Component
public class GrpcClient {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcClient.class);
	@Value("${grpc.server.host:localhost}")
	private String host;
	@Value("${grpc.server.port:8080}")
	private int port;
	private ManagedChannel channel;
	private CurrencyConversionServiceBlockingStub currencyConversionServiceStub;

	@Autowired
	private ObservationGrpcClientInterceptor observationGrpcClientInterceptor;

	public void start() {
		channel = ManagedChannelBuilder.forAddress(host, port).intercept(observationGrpcClientInterceptor)
				.usePlaintext().build();

		currencyConversionServiceStub = CurrencyConversionServiceGrpc.newBlockingStub(channel);
		LOG.info("gRPC client connected to {}:{}", host, port);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
		LOG.info("gRPC client disconnected successfully.");
	}

	public CurrencyConversionServiceBlockingStub getCurrencyConversionServiceStub() {
		return currencyConversionServiceStub;
	}

}
