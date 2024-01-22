package voyager.currencyconversion.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import voyager.currencyconversion.api.client.GrpcClient;
import voyager.currencyconversion.api.client.GrpcClientRunner;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ClientAppTests {

  @Autowired
  private ApplicationContext context;

  @Test
  @Order(1)
  void beanGrpcServerRunnerTest() {
    assertNotNull(context.getBean(GrpcClient.class));
    assertThrows(NoSuchBeanDefinitionException.class,
        () -> context.getBean(GrpcClientRunner.class),
        "GrpcClientRunner should not be loaded during test");
  }
}
