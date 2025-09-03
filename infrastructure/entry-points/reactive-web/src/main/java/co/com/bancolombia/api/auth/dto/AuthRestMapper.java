package co.com.bancolombia.api.auth.dto;


import co.com.bancolombia.model.records.Login;
import co.com.bancolombia.model.records.Token;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AuthRestMapper {

  Login toLogin(LoginRequest request);

  TokenResponse toTokenResponse(Token token);
}
