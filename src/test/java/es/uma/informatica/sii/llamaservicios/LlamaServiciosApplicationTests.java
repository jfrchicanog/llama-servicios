package es.uma.informatica.sii.llamaservicios;

import es.uma.informatica.sii.llamaservicios.dtos.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LlamaServiciosApplicationTests {

    public static final String JWT_ANTONIO = "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQ0xJRU5URSIsInN1YiI6IjIiLCJpYXQiOjE3NDQ5MTQ2NTMsImV4cCI6MTgwNzk4NjY1M30.vTQEIGffqIwqWRbbxihuplJhLfXi6Flhs_zXKOtxjQJoJipIaSxSqPBrqurDu9u296vo7qwpHLvisf3yQHa--w";

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${servicio.usuarios.baseurl}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    private URI uri(String scheme, String host, int port, String ...paths) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub = ubf.builder()
            .scheme(scheme)
            .host(host).port(port);
        for (String path: paths) {
            ub = ub.path(path);
        }
        return ub.build();
    }

    private RequestEntity<Void> get(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host,port, path);
        var peticion = RequestEntity.get(uri)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer "+JWT_ANTONIO)
            .build();
        return peticion;
    }

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testUsuario() {
        // Arrange
        var uriRemota = UriComponentsBuilder.fromUriString(baseUrl+"/usuario")
            .build()
            .toUri();
        // Configura el mockServer para devolver un determinado usuario al hacer GET en la uriRemota
        mockServer.expect(requestTo(uriRemota))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("[{\"id\":1,\"nombre\":\"Usuario\"}]")
            );
        var miURI = get("http", "localhost", port, "/info-usuario");

        // Act
        var resultado = testRestTemplate.exchange(miURI, UsuarioDTO.class);

        // Assert
        assertThat(resultado.getBody().getId()).isEqualTo(1L);
        mockServer.verify(); // Si solo se usa para configurar la respuesta esto no es necesario
    }

}
