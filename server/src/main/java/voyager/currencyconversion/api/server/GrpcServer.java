package voyager.currencyconversion.api.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import voyager.currencyconversion.api.server.interceptor.ExceptionInterceptor;
import voyager.currencyconversion.api.server.service.CurrencyConversionService;

/**
 * @author : github.com/junruitao
 * @project : currency-conversion-server - elk, zipkin, gRpc with Spring
 *          and Spring Boot 
 **/
@Component
public class GrpcServer {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value("${grpc.port:8080}")
	private int port;

	private Server server;

	private final CurrencyConversionService currencyConversionService;
	private final ExceptionInterceptor exceptionInterceptor;

	private final ObservationGrpcServerInterceptor observationInterceptor;

	public GrpcServer(CurrencyConversionService currencyConversionService, ExceptionInterceptor exceptionInterceptor,
			ObservationGrpcServerInterceptor interceptor) {
		this.currencyConversionService = currencyConversionService;
		this.exceptionInterceptor = exceptionInterceptor;
		this.observationInterceptor = interceptor;
	}

	public void start() throws IOException, InterruptedException {
		LOG.info("gRPC server is starting on port: {}.", port);
		server = ServerBuilder.forPort(port).addService(currencyConversionService).intercept(exceptionInterceptor)
				.intercept(observationInterceptor).build().start();
		LOG.info("gRPC server started and listening on port: {}.", port);
		LOG.info("Following service are available: ");
		server.getServices().stream().forEach(s -> LOG.info("Service Name: {}", s.getServiceDescriptor().getName()));
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LOG.info("Shutting down gRPC server.");
			GrpcServer.this.stop();
			LOG.info("gRPC server shut down successfully.");
		}));
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	public void block() throws InterruptedException {
		if (server != null) {
			// received the request until application is terminated
			server.awaitTermination();
		}
	}
}
