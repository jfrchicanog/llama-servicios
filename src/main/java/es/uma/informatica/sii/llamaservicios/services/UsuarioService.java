package es.uma.informatica.sii.llamaservicios.services;


import es.uma.informatica.sii.llamaservicios.dtos.UsuarioDTO;
import es.uma.informatica.sii.llamaservicios.entities.Usuario;
import es.uma.informatica.sii.llamaservicios.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class UsuarioService {

    @Value("${servicio.usuarios.baseurl}")
    private String baseUrl;


    private RestTemplate restTemplate;
    public UsuarioService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<UsuarioDTO> getUsuario(Long id, String jwtToken) {
        var uri = UriComponentsBuilder.fromUriString(baseUrl+"/usuario")
            .queryParam("id", id)
            .build()
            .toUri();

        var peticion = RequestEntity.get(uri)
            .header("Authorization", jwtToken)
            .build();

        try {
            return Optional.of(restTemplate.exchange(peticion, UsuarioDTO[].class).getBody()[0]);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UsuarioDTO> getUsuarioConectado(String jwtToken) {
        var peticion = RequestEntity.get(baseUrl + "/usuario")
                .header("Authorization", jwtToken)
                    .build();
        try {
            return Optional.of(restTemplate.exchange(peticion, UsuarioDTO[].class).getBody()[0]);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
