package co.com.bancolombia.api.security;

import co.com.bancolombia.model.user.gateways.EncryptPasswordGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;
@Service
@RequiredArgsConstructor
public class EncryptPasswordAdapter implements EncryptPasswordGateway {
    @Override
    public String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
//        return password;
    }

    @Override
    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
