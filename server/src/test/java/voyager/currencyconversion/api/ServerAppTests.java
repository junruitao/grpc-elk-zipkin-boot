package voyager.currencyconversion.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import voyager.currencyconversion.api.server.GrpcServer;
import voyager.currencyconversion.api.server.GrpcServerRunner;
import voyager.currencyconversion.api.server.interceptor.ExceptionInterceptor;
import voyager.currencyconversion.api.server.service.CurrencyConversionService;
import voyager.currencyconversion.grpc.CurrencyConversionServiceGrpc;
import voyager.currencyconversion.grpc.CurrencyService.ConvertRequest;
import voyager.currencyconversion.grpc.CurrencyService.ConvertResponse;

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ServerAppTests {

	/**
	 * This rule manages automatic graceful shutdown for the registered servers and
	 * channels at the end of test.
	 */
	@Rule
	private final static GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
	private static CurrencyConversionServiceGrpc.CurrencyConversionServiceBlockingStub blockingStub;
	@Autowired
	private ApplicationContext context;

	/**
	 * To test the server, make calls with a real stub using the in-process channel,
	 * and verify behaviors or state changes from the client side.
	 */
	@BeforeAll
	public static void setup(@Autowired CurrencyConversionService currencyConversionService,
			@Autowired ExceptionInterceptor exceptionInterceptor) throws IOException {
		// Generate a unique in-process server name.
		String serverName = InProcessServerBuilder.generateName();

		// Create a server, add service, start, and register for automatic graceful
		// shutdown.
		grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor()
				.addService(currencyConversionService).intercept(exceptionInterceptor).build().start());

		blockingStub = CurrencyConversionServiceGrpc.newBlockingStub(
				// Create a client channel and register for automatic graceful shutdown.
				grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
	}

	@Test
	@Order(1)
	void beanGrpcServerRunnerTest() {
		assertNotNull(context.getBean(GrpcServer.class));
		assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(GrpcServerRunner.class),
				"GrpcServerRunner should not be loaded during test");
	}

	@Test
	@Order(2)
	@DisplayName("Creates the source object using create RPC call")
	public void SourceService_Create() {
		ConvertResponse response = blockingStub.convert(
				ConvertRequest.newBuilder().setAmount(100).setFromCurrency("USD").setToCurrency("JPY").build());
		assertNotNull(response);
		assertEquals(11000, response.getConvertedAmount());
	}
}
