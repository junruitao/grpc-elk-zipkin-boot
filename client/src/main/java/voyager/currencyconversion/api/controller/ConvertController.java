package voyager.currencyconversion.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import voyager.currencyconversion.api.client.GrpcClient;
import voyager.currencyconversion.grpc.CurrencyService.ConvertRequest;
import voyager.currencyconversion.grpc.CurrencyService.ConvertResponse;

/**
 * @author : github.com/junruitao
 * @project : currency-conversion - elk, zipkin, gRpc with Spring and
 *          Spring Boot 
 **/
@RestController
public class ConvertController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private GrpcClient client;

	public ConvertController(GrpcClient client) {
		this.client = client;
	}

	@GetMapping("/convert/{from}/{to}/{amount}")
	public String getSources(@PathVariable("from") String from, @PathVariable("to") String to,
			@PathVariable("amount") float amount) throws InvalidProtocolBufferException {
		LOG.info("From : {} To:{} Amount:{}", from, to, amount);
		ConvertRequest req = ConvertRequest.newBuilder().setAmount(amount).setFromCurrency(from).setToCurrency(to)
				.build();
		ConvertResponse resp = client.getCurrencyConversionServiceStub().convert(req);
		var printer = JsonFormat.printer().includingDefaultValueFields();
		LOG.info("Server response received in Json Format: {}", resp);
		return printer.print(resp);
	}
}
