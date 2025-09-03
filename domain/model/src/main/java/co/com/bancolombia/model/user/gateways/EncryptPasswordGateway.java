package co.com.bancolombia.model.user.gateways;

public interface EncryptPasswordGateway {
    String encryptPassword(String password);
    boolean checkPassword(String password, String encryptedPassword);

}
