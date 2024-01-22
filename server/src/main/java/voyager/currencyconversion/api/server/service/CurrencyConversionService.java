package voyager.currencyconversion.api.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableBiMap;

import io.grpc.stub.StreamObserver;
import voyager.currencyconversion.grpc.CurrencyConversionServiceGrpc;
import voyager.currencyconversion.grpc.CurrencyService.ConvertRequest;
import voyager.currencyconversion.grpc.CurrencyService.ConvertResponse;

@Service
public class CurrencyConversionService extends CurrencyConversionServiceGrpc.CurrencyConversionServiceImplBase {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	static final ImmutableBiMap<String, Float> currencyValue = ImmutableBiMap.of("USD", 1f, "EUR", 0.85f, "JPY", 110f);

	@Override
	public void convert(ConvertRequest req, StreamObserver<ConvertResponse> resObserver) {
		LOG.info("From : {} To:{} Amount:{}", req.getFromCurrency(), req.getToCurrency(), req.getAmount());
		ConvertResponse resp = ConvertResponse.newBuilder()
				.setConvertedAmount(req.getAmount() * getValue(req.getToCurrency()) / getValue(req.getFromCurrency()))
				.build();
		resObserver.onNext(resp);
		resObserver.onCompleted();
	}

	private float getValue(String currency) {
		return currencyValue.get(currency);
	}

}
