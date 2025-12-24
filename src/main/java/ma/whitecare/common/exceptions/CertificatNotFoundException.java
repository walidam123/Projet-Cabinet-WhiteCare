package ma.whitecare.common.exceptions;

public class CertificatNotFoundException extends RuntimeException {
    public CertificatNotFoundException(Long certificatId) {
        super("Certificat non trouvé avec l'ID: " + certificatId);
    }

    public CertificatNotFoundException(String message) {
        super(message);
    }
}
